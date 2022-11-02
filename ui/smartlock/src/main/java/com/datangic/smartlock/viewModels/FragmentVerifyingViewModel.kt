package com.datangic.smartlock.viewModels

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.datangic.smartlock.R
import com.datangic.smartlock.ble.ReceivedMessageHandle.RegisterType
import com.datangic.smartlock.ble.livedata.state.ConnectionState
import com.datangic.smartlock.databinding.FragmentVerifyingBinding
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.dialog.MaterialDialog.setMessage
import com.datangic.smartlock.dialog.MaterialDialog.setTitle
import com.datangic.smartlock.liveData.LockBleReceivedLiveData
import com.datangic.smartlock.parcelable.ExtendedBluetoothDevice
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.common.utils.Logger
import com.datangic.smartlock.utils.UtilsMessage
import com.datangic.smartlock.utils.UtilsMessage.displaySnackBar
import kotlinx.coroutines.launch

class FragmentVerifyingViewModel(application: Application, mBleManagerApi: BleManagerApiRepository) : BaseViewModel(application, mBleManagerApi) {

    private val TAG = FragmentVerifyingViewModel::class.simpleName

    var mScanQrCodeResult: String? = null
        set(value) {
            field = value
            Logger.e(TAG, "value=$value")
        }
    var mScanBleResult: ExtendedBluetoothDevice? = null
    var mConnectErrorDialog: AlertDialog? = null
    var mShowEdit: ((macAddress: String) -> Unit)? = null
    var macAddress: String? = null
    private var mCount = 3


    fun init(fragment: Fragment, binding: FragmentVerifyingBinding) {
        mCount = 3
        val mConnectErrorDialogOperateListener = object : MaterialDialog.OnMaterialAlterDialogListener {
            override fun onCancel() {
                mActivity.finish()
            }

            override fun onConfirm() {
                connectDevice(fragment)
            }

        }
        mShowEdit = fun(macAddress: String) {
            binding.editView = true
            viewModelScope.launch {
                val device = mBleManagerApi.getDeviceByMac(macAddress)
                device?.let {
                    binding.editName.hint = device.name
                }
                binding.confirmClick = View.OnClickListener { view ->
                    if (binding.editName.editText != null) {
                        device?.let {
                            if (binding.editName.editText!!.text.isNotEmpty()) {
                                it.name = binding.editName.editText!!.text.toString()
                                viewModelScope.launch {
                                    mBleManagerApi.update(it)
                                    mBleManagerApi.setDefaultDeviceInfo(it.serialNumber, it.macAddress)
                                }
                            }
                        }
                        mHandler.postDelayed({
                            fragment.requireActivity().finish()
                        }, 200)

                    } else {
                        displaySnackBar(view, R.string.name_is_required)
                    }
                }
            }
        }

        mBleManagerApi.setLockBleManagerStateObserver(mActivity) { stateData ->
            Logger.e(TAG, "address1=${stateData.device?.address}\n address2= $macAddress ")
            if (stateData.device?.address == macAddress) {
                when (stateData.mConnectionState) {
                    is ConnectionState.Disconnected -> {
                        if (mCount >= 0) {
                            mCount--
                            Logger.e(TAG, "DISCONNECTED")
                            mHandler.postDelayed({
                                connectDevice(mActivity)
                                binding.verifyText.text = mActivity.getString(R.string.ble_connecting)
                            }, 500)
                        } else {
                            UtilsMessage.displayToast(mActivity, mActivity.getString(R.string.ble_connect_timeout))
                            mActivity.finish()
                        }
                    }
                    is ConnectionState.Scanning -> {
                        binding.verifyText.text = fragment.getString(R.string.ble_scanning)
                    }

                    is ConnectionState.Ready -> {
                        binding.verifyText.text = fragment.getString(R.string.verify)

                    }
                    else -> {
                        binding.verifyText.text = fragment.getString(R.string.ble_connecting)
                    }
                }
            }
        }
    }

    private fun register(device: BluetoothDevice) {
        mBleManagerApi.getMessageOperation().register(
            RegisterType.SCAN_REGISTER,
            device
        )
    }

    private fun getAlterDialog(
        context: Context,
        title: Any,
        message: Any,
        action: MaterialDialog.OnMaterialAlterDialogListener
    ): AlertDialog {
        return if (mConnectErrorDialog != null) {
            mConnectErrorDialog!!.apply {
                this.setTitle(title)
                this.setMessage(message)
            }
        } else {
            return MaterialDialog.getAlertDialog(
                context,
                icon = R.drawable.ic_tips,
                title = title,
                message = message,
                isError = true,
                action = action
            ).also {
                mConnectErrorDialog = it
            }

        }
    }

    fun connectDevice(lifecycleOwner: LifecycleOwner) {

        mScanBleResult?.let {
            mBleManagerApi.connectWithRegister(it.device, RegisterType.SCAN_REGISTER)
            macAddress = it.device.address
        }
        mScanQrCodeResult?.let {
            when (it.length) {
                in listOf(12, 17, 47) -> {
                    macAddress = UtilsMessage.getMacAddressFromQrCode(it)
                    macAddress?.let { it1 ->
                        mBleManagerApi.connectWithRegister(it1, lifecycleOwner, RegisterType.SCAN_REGISTER)
                        macAddress = it1
                    }
                }
                in listOf(127, 128) -> {
                    UtilsMessage.getMacAddressFromShareCode(it)?.let { triple ->
                        Logger.e(TAG, "Add ShareCode")
                        macAddress = triple.second
                        mBleManagerApi.connectWithShareCode(triple.second, lifecycleOwner, Pair(triple.first, triple.third))
                    }
                }
                else -> {

                }
            }
        }
    }

    override fun handle26(msg: LockBleReceivedLiveData) {
        super.handle26(msg)
        msg.device?.let {
            if (it.address == macAddress) {
                mShowEdit?.let { it1 ->
                    mHandler.postDelayed(
                        {
                            it1(it.address)
                        }, 500
                    )
                }
            }
        }
    }
}