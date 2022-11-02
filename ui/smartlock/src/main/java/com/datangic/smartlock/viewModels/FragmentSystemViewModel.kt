package com.datangic.smartlock.viewModels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.datangic.common.utils.Logger
import com.datangic.localLock.biometricprompt.FingerprintCallback
import com.datangic.localLock.biometricprompt.FingerprintVerifyManager
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.SettingItemAdapter
import com.datangic.smartlock.components.LockStatusItem
import com.datangic.smartlock.components.SwitchItem
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.preference.LanguageHelper
import com.datangic.smartlock.preference.ThemeHelper
import com.datangic.smartlock.preference.ThemeHelper.DARK_MODE
import com.datangic.smartlock.preference.ThemeHelper.DEFAULT_MODE
import com.datangic.smartlock.preference.ThemeHelper.LIGHT_MODE
import com.datangic.smartlock.respositorys.LocalPasswordRepository
import com.datangic.smartlock.respositorys.ScanQrCodeHelper
import com.datangic.smartlock.respositorys.SystemFragmentRepository
import com.datangic.smartlock.ui.scanning.ScanActivity
import com.datangic.smartlock.utils.*
import kotlinx.coroutines.launch


class FragmentSystemViewModel(
    application: Application,
    private val mRepository: SystemFragmentRepository,
    val mLocalPasswordRepository: LocalPasswordRepository
) : AndroidViewModel(application) {

    private val TAG = FragmentSystemViewModel::class.simpleName
    val mItemList = mRepository.systemItem
    val mAdapter = SettingItemAdapter(mItemList)
    var isDebug = false
    fun setObserver(fragment: Fragment) {
        mRepository.mDatabase.mSystemSettingsLiveData.observe(fragment.viewLifecycleOwner) {
            if (isDebug) {
                mItemList.forEach { item ->
                    if (item is SwitchItem) {
                        when (item.itemName) {
                            R.string.debug_ota -> item.checked = it.debugOta
                            R.string.debug -> item.checked = it.debug
                        }
                    }
                }
                mAdapter.submitList(mItemList)
            } else {
                mRepository.testItem.forEach { item ->
                    if (item is SwitchItem) {
                        when (item.itemName) {
                            R.string.debug_ota -> item.checked = it.debugOta
                            R.string.debug -> item.checked = it.debug
                        }
                    }
                }
            }

            Logger.e(TAG, "size=${mItemList.size}")
            mAdapter.submitList(mItemList)
        }
        mLocalPasswordRepository.mLocalPasswordLiveData.observe(fragment.viewLifecycleOwner) { triple ->
            Log.e(TAG, "password=${triple?.second} \nlocal=${triple}")
            if (triple == null || triple.second == "") {
                mItemList.removeAll(mRepository.hasPassword.toSet())
                mItemList.removeAll(mRepository.noPassword.toSet())
                mItemList.addAll(mRepository.noPassword)
                mAdapter.submitList(mItemList)
            } else {
                mItemList.removeAll(mRepository.noPassword.toSet())
                mItemList.removeAll(mRepository.hasPassword.toSet())
                mItemList.addAll(mRepository.hasPassword)
                mAdapter.submitList(mItemList)
            }
            mItemList.forEach { each ->
                if (each is SwitchItem && each.itemName == R.string.system_fingerprint) {
                    each.checked = triple?.third ?: false
                }
            }
        }
        mAdapter.setOnSettingItemListener(getOnItemClickListener(fragment))
    }

    fun addTestItem() {
        if (!isDebug) {
            mItemList.addAll(mRepository.testItem)
            mAdapter.submitList(mItemList)
            onResume()
            isDebug = true
        }
    }

    private fun getOnItemClickListener(fragment: Fragment) = object : SettingItemAdapter.OnSettingItemListener {
        override fun onClick(systemItem: Any) {
            when (systemItem) {
                is SwitchItem -> {
                    when (systemItem.itemName) {
                        R.string.system_fingerprint -> {
                            FingerprintVerifyManager.Builder(fragment, fingerprintCallback).build()
                        }

                        R.string.debug_ota -> {
                            viewModelScope.launch {
                                mRepository.mDatabase.dataStore.updateOTADebug(!systemItem.checked)
                            }
                        }
                        R.string.debug -> {
                            viewModelScope.launch {
                                mRepository.mDatabase.dataStore.updateDebug(!systemItem.checked)
                            }
                        }
                    }
                }
                is LockStatusItem -> {
                    when (systemItem.itemName) {
                        R.string.theme, R.string.language -> {
                            Logger.e(TAG, "name=${systemItem.itemName} status=${fragment.getString(systemItem.itemStatus as Int)}")
                            getItemDialog(fragment, systemItem.itemName, systemItem.itemStatus).show()
                        }
                        else -> {
                        }
                    }
                }
                R.string.system_password -> {
                    mLocalPasswordRepository.getNewPasswordDialog(fragment.requireActivity(), R.string.password, true)
                }
                R.string.system_password_change -> {
                    mLocalPasswordRepository.mLocalPassword?.let {
                        mLocalPasswordRepository.getChangePasswordDialog(fragment.requireActivity(), R.string.password)
                    }
                }
                R.string.set_info -> {
                    ScanQrCodeHelper.onScanQrCode(fragment.requireActivity(), REQUEST_SET_DEVICE_INFO)
                }
                R.string.set_secret_code -> {
                    fragment.requireActivity().startActivity(Intent(fragment.requireActivity(), ScanActivity::class.java)
                        .apply {
                            putExtra(SCAN_FOR_ACTION, REQUEST_SET_SECRET_CODE)
                        })
                }
                R.string.select_secret_code -> {
                    mRepository.mDatabase.mSecretCodeMap.secretCodeMap.let {
                        MaterialDialog.getConfirmationForSecretCodeDialog(
                            fragment.requireContext(),
                            icon = R.drawable.ic_secret,
                            title = R.string.select_secret_code,
                            message = it.keys.toTypedArray(),
                            selected = mRepository.mDatabase.mSecretCodeMap.default
                                ?: "",
                            action = selectCodeOnDialog
                        ).show()
                    }
                }
            }
        }
    }

    val selectCodeOnDialog = object : MaterialDialog.OnMaterialConfirmationForSecretCodeDialogListener {
        override fun onSelected(selected: String) {
            viewModelScope.launch {
                mRepository.mDatabase.dataStore.setDefaultSecretCode(selected)
            }
        }

        override fun onAdd() {
        }

        override fun onDelete(selected: String) {
        }

        override fun onConfirm() {

        }
    }

    private val fingerprintCallback = object : FingerprintCallback {
        override fun onHwUnavailable() {}

        override fun onNoneEnrolled() {}

        override fun onSucceeded() {
            mItemList.forEach {
                if (it is SwitchItem && it.itemName == R.string.system_fingerprint) {
                    mLocalPasswordRepository.setBiometric(!it.checked)
                }
            }
        }

        override fun onFailed() {}

        override fun onUsepwd() {}

        override fun onCancel() {}

    }

    fun getItemDialog(fragment: Fragment, type: Any, selected: Any): AlertDialog {
        val items = when (type) {
            R.string.language -> {
                arrayOf(
                    fragment.requireContext().getString(R.string.language_chinese),
                    fragment.requireContext().getString(R.string.language_english)
                )

            }
            R.string.theme -> {
                arrayOf(
                    fragment.requireContext().getString(R.string.theme_dark),
                    fragment.requireContext().getString(R.string.theme_light),
                    fragment.requireContext().getString(R.string.theme_system)
                )
            }
            else -> {
                arrayOf()
            }
        }
        return MaterialDialog.getConfirmationDialog(
            fragment.requireContext(),
            icon = when (type) {
                R.string.language -> R.drawable.ic_language
                R.string.theme -> R.drawable.ic_theme
                else -> {
                    R.drawable.ic_tips_36
                }
            },
            title = type,
            items,
            selected = when (selected) {
                is Int -> fragment.requireContext().getString(selected)
                is String -> selected
                else -> NULL_STRING
            },
            action = onConfirmationClick(fragment)
        )
    }

    private val onConfirmationClick = object : (Fragment) -> MaterialDialog.OnMaterialConfirmationDialogListener {
        override fun invoke(fragment: Fragment): MaterialDialog.OnMaterialConfirmationDialogListener {
            return object : MaterialDialog.OnMaterialConfirmationDialogListener {
                override fun onCancel() {

                }

                override fun onSelected(selected: String) {

                }

                override fun onConfirm(selected: String) {
                    Logger.e(TAG, "selected=$selected")
                    when (selected) {
                        fragment.getString(R.string.language_english) -> {
                            if (mRepository.getLanguageStatus() != R.string.language_english) {
                                LanguageHelper.applyLanguage(fragment.requireContext(), LanguageHelper.Language.ENGLISH)
                                fragment.requireActivity().recreate()
                            }
                        }
                        fragment.getString(R.string.language_chinese) -> {
                            if (mRepository.getLanguageStatus() != R.string.language_chinese) {
                                LanguageHelper.applyLanguage(fragment.requireContext(), LanguageHelper.Language.CHINESE)
                                fragment.requireActivity().recreate()
                            }
                        }
                        fragment.getString(R.string.theme_system) -> {
                            Logger.e(TAG, "")
                            if (mRepository.getThemeStatus() != R.string.theme_system) {
                                ThemeHelper.applyTheme(fragment.requireContext(), DEFAULT_MODE)
                            }
                        }
                        fragment.getString(R.string.theme_light) -> {
                            if (mRepository.getThemeStatus() != R.string.theme_light) {
                                ThemeHelper.applyTheme(fragment.requireContext(), LIGHT_MODE)
                            }
                        }
                        fragment.getString(R.string.theme_dark) -> {
                            if (mRepository.getThemeStatus() != R.string.theme_dark) {
                                ThemeHelper.applyTheme(fragment.requireContext(), DARK_MODE)

                            }
                        }
                    }
                    setTheme()
                }
            }
        }
    }

    fun onResume() {
        setTheme()
    }

    private fun setTheme() {
        mItemList.forEach {
            if (it is LockStatusItem) {
                if (it.itemName == R.string.theme)
                    it.itemStatus = mRepository.getThemeStatus()
                else if (it.itemName == R.string.language) {
                    it.itemStatus = mRepository.getLanguageStatus()
                }
            }
        }
    }

}