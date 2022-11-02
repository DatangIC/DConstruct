package com.datangic.smartlock.viewModels

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import cn.dttsh.dts1586.MSG16
import com.datangic.common.Config
import com.datangic.common.utils.Logger
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.DeviceKeyPagerAdapter
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.components.DeviceKeyItem
import com.datangic.data.database.table.DeviceEnum
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.dialog.MaterialDialog.setImageLevel
import com.datangic.smartlock.dialog.MaterialDialog.setMessage
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class FragmentManagerKeysViewModel(
    application: Application,
    val serialNumber: String,
    val macAddress: String,
    val userID: Int,
    val hasNFC: Boolean,
    val hasFace: Boolean,
    mBleManagerApi: BleManagerApiRepository
) : BaseViewModel(application, mBleManagerApi) {

    private val TAG = FragmentManagerKeysViewModel::class.simpleName
    private var mWaitingDialog: AlertDialog? = null
    private var mFingerprintSamplingOnceDone = 0
    private var mFingerprintHasCallback: Boolean = false
    private val mWaitingDialogTimeOut = Runnable { mWaitingDialog?.cancel() }

    private val mKeysLiveDate = mBleManagerApi.getViewDeviceKeysLiveData(userID)
    private val mDeviceStatusLiveData = mBleManagerApi.mViewDevicesStatusLiveData


    private val onKeyClick = object : DeviceKeyPagerAdapter.OnKeyClick {
        override fun newKey(key: DeviceKeyItem) {
            addOrEdit(key)
        }

        override fun editNameKey(key: DeviceKeyItem) {
            MaterialDialog.getInputStringDialog(
                mActivity,
                R.string.edit_unlock_info_name,
                key.icon,
                hint = key.name
            ) { newName ->
                mBleManagerApi.updateDeviceKeyName(userID, key.keyId, key.type, newName)
            }.show()

        }

        override fun deleteKey(key: DeviceKeyItem, keyId: Int) {
            CreateMessage.createMessage15(macAddress, MSG15_CmdDelete, getCmdType(key.icon), keyId.toByte(), userID).execute()
        }

        override fun modifyKey(key: DeviceKeyItem, keyId: Int) {
            addOrEdit(key, keyId.toByte())
        }

        override fun onLongClick(key: DeviceKeyItem) {
            Logger.i(TAG, "LongClick")
            addOrEdit(key, longClick = true)
        }

        fun getCmdType(id: Int) = when (id) {
            R.drawable.ic_password_36 -> MSG15_TypePassword
            R.drawable.ic_fingerprint_36 -> MSG15_TypeFingerprint
            R.drawable.ic_seized_fingerprint_36 -> MSG15_TypeSeizedFingerprint
            R.drawable.ic_nfc_36 -> MSG15_TypeNFC
            R.drawable.ic_face_36 -> MSG15_TypeFace
            else -> MSG15_TypePassword
        }

        fun addOrEdit(key: DeviceKeyItem, keyId: Byte = 0, longClick: Boolean = false) {
            val cmd = if (keyId == 0.toByte()) MSG15_CmdNew else MSG15_CmdEdit
            val cmdType = getCmdType(key.icon)
            val dialogCancel = object : MaterialDialog.OnMaterialAlterDialogListener {
                override fun onCancel() {
                    CreateMessage.createMessage15(macAddress, MSG15_CmdCancel, cmdType, keyId, userID).execute()
                }

                override fun onConfirm() {
                }
            }
            when (cmdType) {
                MSG15_TypePassword -> {
                    mKeysLiveDate.value?.forEach { key ->
                        if (key.keyType == DeviceEnum.KeyType.PASSWORD && cmd == MSG15_CmdNew) {
                            UtilsMessage.displaySnackBar(mActivity.window.decorView, R.string.password_is_exists)
                            return
                        }
                    }
                    MaterialDialog.getPasswordDialog(mActivity) { password ->
                        CreateMessage.createMessage15(macAddress, cmd, cmdType, keyId, userID, password).execute()
                    }.show()
                }
                MSG15_TypeSeizedFingerprint,
                MSG15_TypeFingerprint -> {
                    var mCount = 0
                    mKeysLiveDate.value?.forEach { key ->
                        if (cmd == MSG15_CmdNew && (key.keyType == DeviceEnum.KeyType.FINGERPRINT || key.keyType == DeviceEnum.KeyType.SEIZED_FINGERPRINT)) {
                            if (++mCount >= 5) {
                                UtilsMessage.displaySnackBar(mActivity.window.decorView, R.string.fingerprint_is_exists)
                                return
                            }
                        }
                    }
                    val _cmdType =
                        if ((mBleManagerApi.mDefaultDeviceView?.seizedFingerprint == true && longClick) || cmdType == MSG15_TypeSeizedFingerprint) {
                            MSG15_TypeSeizedFingerprint
                        } else MSG15_TypeFingerprint
                    CreateMessage.createMessage15(macAddress, cmd, _cmdType, keyId, userID).execute().also { state ->
                        if (state == CreateMessage.State.SUCCESS)
                            if (mFingerprintHasCallback) {
                                mWaitingDialog =
                                    MaterialDialog.getAddFingerprintDialog(mActivity, action = dialogCancel)
                                mWaitingDialog?.show().also {
                                    mFingerprintSamplingOnceDone = 0
                                    dialogDelay()
                                }
                            } else {
                                mWaitingDialog = MaterialDialog.getLoadingDialog(
                                    mActivity,
                                    R.string.add_unlock_information,
                                    action = dialogCancel
                                )
                                mWaitingDialog?.show().also {
                                    mFingerprintSamplingOnceDone = 0
                                    dialogDelay()
                                }
                            }
                    }
                }
                else -> {
                    mKeysLiveDate.value?.forEach { key ->
                        if (cmd == MSG15_CmdNew && cmdType == MSG15_TypeNFC && key.keyType == DeviceEnum.KeyType.NFC) {
                            UtilsMessage.displaySnackBar(mActivity.window.decorView, R.string.nfc_is_exists)
                            return
                        }
                        if (cmd == MSG15_CmdNew && cmdType == MSG15_TypeFace && key.keyType == DeviceEnum.KeyType.FACE) {
                            UtilsMessage.displaySnackBar(mActivity.window.decorView, R.string.face_is_exists)
                            return
                        }
                    }
                    CreateMessage.createMessage15(macAddress, cmd, cmdType, keyId, userID).execute().also { state ->
                        if (state == CreateMessage.State.SUCCESS)
                            mWaitingDialog = MaterialDialog.getLoadingDialog(
                                mActivity,
                                R.string.add_unlock_information,
                                action = dialogCancel
                            )
                        mWaitingDialog?.show().also {
                            mFingerprintSamplingOnceDone = 0
                            dialogDelay()
                        }
                    }
                }
            }
        }
    }


    private fun getKeysPager(context: Context, hasNFC: Boolean, hasFace: Boolean) = DeviceKeyPagerAdapter(context, hasNFC, hasFace).apply {
        mOnKeyClick = onKeyClick
    }

    fun setDefaultSelect(fragment: Fragment, viewPager: ViewPager2, selected: Int) {

        viewPager.adapter = getKeysPager(fragment.requireContext(), hasNFC, hasFace)
        mDeviceStatusLiveData.observe(fragment.viewLifecycleOwner) {
            mFingerprintHasCallback = Config.FINGERPRINT_CALLBACK.contains(
                mDeviceStatusLiveData.value?.secretCode
                    ?: ""
            )
        }
        Logger.e(TAG, " mac=${macAddress} sn =${serialNumber} userID =$userID")
        mKeysLiveDate.observe(fragment.viewLifecycleOwner) {
            Logger.e(TAG, "it=${it.size}")
            it.forEach { key ->
                Logger.e(TAG, "it=${key.keyType}")
            }
            viewModelScope.launch {
                (viewPager.adapter as DeviceKeyPagerAdapter).mKeysItemList = it
            }
        }
        mHandler.postDelayed({
            when (selected) {
                R.drawable.ic_management_nfc, R.drawable.ic_management_face -> {
                    viewPager.currentItem = 2
                }
                R.drawable.ic_management_fingerprint -> {
                    viewPager.currentItem = 1
                }
                else -> {
                    viewPager.currentItem = 0
                }
            }
        }, 30)

    }

    override fun handle1E(errorCode: Int) {
        super.handle1E(errorCode)
        when (errorCode) {
            MSG1E_FingerprintSamplingOnceDone -> {
                mFingerprintSamplingOnceDone += 1
                if (mFingerprintSamplingOnceDone <= 4) {
                    mWaitingDialog?.setImageLevel(mFingerprintSamplingOnceDone)
                }
            }
            MSG1E_ModifyFaceDone,
            MSG1E_AddORModifyFaceFailed,
            MSG1E_ModifyNFCDone,
            MSG1E_AddOrModifyNFCFailed,
            MSG1E_ModifyFingerprintDone,
            MSG1E_AddOrModifyFingerprintFailed -> {
                mWaitingDialog?.cancel()
            }
            MSG1E_FingerprintIsFull -> {
                mWaitingDialog?.setMessage(R.string.fingerprint_is_full)
            }
            MSG1E_FingerprintHasExist -> {
                mWaitingDialog?.setMessage(R.string.fingerprint_already_exist)
            }

        }
    }

    override fun handle16(msg: MSG16) {
        super.handle16(msg)
        if (msg.lockID != 0) {
            mWaitingDialog?.cancel()
        }
    }

    override fun handle18() {
        super.handle18()
        dialogDelay()
    }

    private fun dialogDelay() {
        mHandler.removeCallbacks(mWaitingDialogTimeOut)
        mHandler.postDelayed(mWaitingDialogTimeOut, DIALOG_TIMEOUT)
    }

    /**
     * TabLayout
     */
    fun setTabLayTabLayoutMediator(tabLayout: TabLayout, viewPager: ViewPager2) {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            (viewPager.adapter as DeviceKeyPagerAdapter).let {
                when (it.mDeviceKeyTypeList[position].name) {
                    is Int -> {
                        tab.setText(it.mDeviceKeyTypeList[position].name as Int)
                    }
                    is String -> {
                        tab.text = it.mDeviceKeyTypeList[position].name as String
                    }
                }
            }
        }.attach()
    }
}