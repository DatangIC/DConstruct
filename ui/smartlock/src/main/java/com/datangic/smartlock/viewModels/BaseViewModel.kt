package com.datangic.smartlock.viewModels

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import cn.dttsh.dts1586.*
import com.datangic.api.smartlock.SmartLockApi
import com.datangic.common.utils.Logger
import com.datangic.data.SystemSettings
import com.datangic.libs.base.ApplicationProvider.Companion.getCurrentActivity
import com.datangic.smartlock.R
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.ble.ReceivedMessageHandle
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.liveData.LockBleReceivedLiveData
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.DIALOG_TIMEOUT
import com.datangic.smartlock.utils.RequestPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

open class BaseViewModel constructor(application: Application, val mBleManagerApi: BleManagerApiRepository) : AndroidViewModel(application) {

    private val TAG = BaseViewModel::class.simpleName

    protected val mApplication: Application
        get() = getApplication<Application>()

    protected val mActivity: AppCompatActivity
        get() = mApplication.getCurrentActivity() as AppCompatActivity

    var onSecretCodeSelectedAction: (() -> Unit)? = null

    var mLockApi: SmartLockApi? = null


    protected val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val mDialogLoading: AlertDialog = MaterialDialog.getLoadingDialog(mActivity, R.string.dialog_waiting, false, null)

    private val mConnectErrorDialog by lazy {
        MaterialDialog.getAlertDialog(
            mActivity,
            message = R.string.ble_link_loss,
            isError = true,
            isCancel = true,
            action = object : MaterialDialog.OnMaterialAlterDialogListener {
                override fun onCancel() {
                }

                override fun onConfirm() {
                    RequestPermissions.requestPermissions(mActivity) {
                        mBleManagerApi.connectWithRegister(
                            mBleManagerApi.mDefaultDeviceInfo?.second!!,
                            mActivity,
                            ReceivedMessageHandle.RegisterType.NORMAL_REGISTER
                        )
                    }
                }
            })
    }


    protected lateinit var mSystemSetting: SystemSettings

    protected fun showLoadingDialog(timeout: Long = DIALOG_TIMEOUT) {
        mDialogLoading.let {
            if (it.isShowing) return@let
            it.show()
            mHandler.postDelayed(
                hideDialogDelay, timeout
            )
        }
    }

    private val hideDialogDelay = Runnable {
        mDialogLoading.cancel()
    }

    protected fun cancelLoadingDialog() {
        mDialogLoading.cancel()
        mHandler.removeCallbacks(hideDialogDelay)
    }

    private val mMessageListenerObserver = Observer<LockBleReceivedLiveData> {
        Logger.e(TAG, "tag =${it.msg.tag.toString(16)}")
        viewModelScope.launch {
            when (it.msg.tag) {
                MSG.M04 -> handle04(it.msg as MSG04)
                MSG.M0E -> handle0E(it.msg.errCode)
                MSG.M12 -> handle12()
                MSG.M14 -> handle14(it.msg as MSG14)
                MSG.M18 -> handle18()
                MSG.M1A -> handle1A(it.msg as MSG1A)
                MSG.M1C -> handle1C(it.msg as MSG1C)
                MSG.M1E -> handle1E(it.msg.errCode)
                MSG.M16 -> handle16(it.msg as MSG16)
                MSG.M26 -> handle26(it)
                MSG.M2E -> handle2E(it.msg.errCode)
                MSG.M3E -> handle3E(it.msg.errCode)
                MSG.M42 -> handle42(it.msg as MSG42)
                MSG.M4E -> handle4E(it.msg.errCode)
                MSG.M5E -> handle5E((it.msg as MSG5E).type, it.msg.errCode)
            }
        }
    }

    init {
        mBleManagerApi.mSystemSettingsLiveData.observeForever { settings ->
            mSystemSetting = settings
        }
        mBleManagerApi.getReceivedMessageLiveData().observeForever(mMessageListenerObserver)
        MainScope().launch(Dispatchers.IO) {
            mBleManagerApi.mDatabase.mDataStore.mUserPrivateInfoFlow.collect {
                mLockApi = SmartLockApi.create { return@create it.authentication }
            }
        }
    }

    fun Pair<String, MSG>.execute(): CreateMessage.State {
        return if (isConnectedWithDialog(this.first))
            mBleManagerApi.sendMessage(this.first, this.second)
        else {
            CreateMessage.State.DISCONNECT
        }
    }


    fun isConnectedWithDialog(macAddress: String): Boolean {
        return if (mBleManagerApi.isConnected(macAddress)) true else {
            if (!mConnectErrorDialog.isShowing) {
                mConnectErrorDialog.show()
            }
            false
        }
    }

    private val mOnSelectSecretCode = object : MaterialDialog.OnMaterialConfirmationForSecretCodeDialogListener {
        override fun onSelected(selected: String) {
            mBleManagerApi.setDefaultSecretCode(selected)
        }

        override fun onAdd() {
        }

        override fun onDelete(selected: String) {
        }

        override fun onConfirm() {
            onSecretCodeSelectedAction?.let {
                it()
                onSecretCodeSelectedAction = null
            }
        }

    }

    open fun handle0E(errorCode: Int) {
        val error: Int? = when (errorCode) {
            0x01 -> R.string._0e_0x01_
            0x02 -> R.string._0e_0x02_
            0x03 -> R.string._0e_0x03_
            0x04 -> R.string._0e_0x04_
            0x05 -> R.string._0e_0x05_
            0x06 -> R.string._0e_0x06_
            0x07 -> R.string._0e_0x07_
            0x08 -> R.string._0e_0x08_
            0x09 -> R.string._0e_0x09_
            0x0a -> R.string._0e_0x0a_
            else -> null
        }
        if (error != null) {
            if (errorCode in listOf(7, 10)) {
                mBleManagerApi.mSecretCodeMap.let { secretCodeMap ->
                    Logger.e(TAG, "New Dialog SecretCodeMap")
                    MaterialDialog.getConfirmationForSecretCodeDialog(
                        mActivity,
                        icon = R.drawable.ic_secret,
                        title = R.string.select_secret_code,
                        message = secretCodeMap.secretCodeMap.keys.toTypedArray(),
                        selected = secretCodeMap.default,
                        hasNeutral = false,
                        action = mOnSelectSecretCode
                    ).show()
                }
            }
        }
    }


    open fun handle04(msg: MSG04) {}
    open fun handle1A(msg: MSG1A) {}
    open fun handle1C(msg: MSG1C) {}
    open fun handle12() {}
    open fun handle14(msg: MSG14) {}
    open fun handle18() {}
    open fun handle1E(errorCode: Int) {}
    open fun handle16(msg: MSG16) {}
    open fun handle26(msg: LockBleReceivedLiveData) {}
    open fun handle2E(errorCode: Int) {}
    open fun handle3E(errorCode: Int) {}
    open fun handle42(msg: MSG42) {}
    open fun handle4E(errorCode: Int) {}
    open fun handle5E(type: Byte, errorCode: Int) {}

}