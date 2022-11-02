package com.datangic.smartlock.viewModels


import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.SystemItemsAdapter
import com.datangic.smartlock.components.SystemItem
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.data.DatabaseRepository
import com.datangic.smartlock.respositorys.MeFragmentRepository
import com.datangic.smartlock.respositorys.ScanQrCodeHelper
import com.datangic.smartlock.ui.system.SystemActivity
import com.datangic.smartlock.utils.FRAGMENT_ID
import com.datangic.common.utils.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentMeViewModel(application: Application, mMeFragmentRepository: MeFragmentRepository, val mDatabase: DatabaseRepository) :
    AndroidViewModel(application) {

    private val TAG: String = FragmentMeViewModel::class.java.simpleName
    private val mSettingItemAdapter = SystemItemsAdapter().apply {
        submitList(mMeFragmentRepository.mSettingItemList)
    }
    val mScanQrCode by lazy { ScanQrCodeHelper }
    val mHandle by lazy { Handler(Looper.myLooper()!!) }
//    val mUserLiveData =

    fun setSettingAdapter(view: RecyclerView, context: Context) {
        view.adapter = mSettingItemAdapter.apply {
            setOnSettingItemListener(mSettingItemOnClick(context))
        }
    }

    fun operationOnDialog(context: Context) = object : MaterialDialog.OnMaterialConfirmationForSecretCodeDialogListener {
        override fun onSelected(selected: String) {
            Logger.e(TAG, "Selected=$selected")
            viewModelScope.launch {
                mDatabase.dataStore.setDefaultSecretCode(selected)
            }
        }

        override fun onAdd() {
            getSecretDialog(context = context).show()
        }

        override fun onDelete(selected: String) {
            GlobalScope.launch {
                mDatabase.dataStore.setDeleteSecretCode(selected)
            }
        }

        override fun onConfirm() {

        }
    }

    private fun mSettingItemOnClick(context: Context) = object : SystemItemsAdapter.OnSettingItemListener {

        override fun onClick(systemItem: SystemItem) {
            when (systemItem.icon) {
                R.drawable.ic_secret -> {
                    Logger.v(TAG, "System Setting")
                    showSecretSelected(context)
                }
                R.drawable.ic_setting_lock -> {
                    context.startActivity(Intent(context, SystemActivity::class.java).apply {
                        this.putExtra(FRAGMENT_ID, R.id.navigationDevice)
                    })

                }
                R.drawable.ic_setting -> {
                    context.startActivity(Intent(context, SystemActivity::class.java).apply {
                        this.putExtra(FRAGMENT_ID, R.id.navigationSystem)
                    })
                }
                R.drawable.ic_about_us -> {
//                    context.startActivity(Intent(context, ScanActivity::class.java))
                    Logger.e(TAG, "getDefaultView = ${mDatabase.mDefaultViewDevice?.serialNumber}")
                }
            }
        }
    }

    fun showSecretSelected(context: Context) {
        mDatabase.mSecretCodeMap.secretCodeMap.let {
            Array(it.size) { i ->
                (it.keys.elementAt(i) + "(" + if (it[it.keys.elementAt(i)]?.length == 10) (it[it.keys.elementAt(i)]?.replaceRange(
                    3,
                    7,
                    "***"
                ) + ")") else "None)")
            }
            MaterialDialog.getConfirmationForSecretCodeDialog(
                context,
                icon = R.drawable.ic_secret,
                title = R.string.select_secret_code,
                message = it.keys.toTypedArray(),
                selected = mDatabase.mSecretCodeMap.default,
                action = operationOnDialog(context)
            ).show()

        }
    }

    fun getSecretDialog(context: Context): androidx.appcompat.app.AlertDialog {
        return MaterialDialog.getSecretCodeDialog(
            context,
            cancelAction = {
                mHandle.postDelayed({ showSecretSelected(context) }, 50)
            }
        ) { key, value ->
            GlobalScope.launch {
                mDatabase.dataStore.addSecretCode(key, value)
                showSecretSelected(context)
            }
        }
    }
}