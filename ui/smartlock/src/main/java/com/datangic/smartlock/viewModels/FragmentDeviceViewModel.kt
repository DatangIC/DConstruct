package com.datangic.smartlock.viewModels

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import cn.dttsh.dts1586.MSG14
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.BottomSelectorAdapter
import com.datangic.smartlock.adapter.DeviceSettingAdapter
import com.datangic.smartlock.adapter.SwipeRedGreenClickListener
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.ble.ReceivedMessageHandle.RegisterType
import com.datangic.smartlock.components.DeviceKeyItem
import com.datangic.smartlock.components.DeviceWithBluetooth
import com.datangic.smartlock.components.SelectorItem
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.parcelable.ExtendedBluetoothDevice
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.respositorys.ScanQrCodeHelper
import com.datangic.smartlock.ui.scanning.ScanActivity
import com.datangic.smartlock.ui.system.SystemActivity
import com.datangic.smartlock.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentDeviceViewModel(application: Application, bleManagerApiRepository: BleManagerApiRepository) :
    BaseViewModel(application, bleManagerApiRepository) {


    val mTitle = DeviceKeyItem(R.drawable.ic_ble_lock_36dp, name = R.string.new_device)
    val mAdapter = DeviceSettingAdapter()
    var mDialogLoading: AlertDialog? = null
    var mRetrieveDone: Boolean = false
    var mNewDeviceDone: Boolean = false

    fun setObserver(fragment: Fragment) {
        if (fragment.requireActivity() is SystemActivity) {
            (fragment.requireActivity() as SystemActivity).doRetrieve = fun(bluetoothDevice: ExtendedBluetoothDevice?, macAddress: String?) {
                bluetoothDevice?.let {
                    mBleManagerApi.connectWithRegister(bluetoothDevice.device, RegisterType.CALLBACK_REGISTER)
                    if (mDialogLoading == null) {
                        mDialogLoading = MaterialDialog.getLoadingDialog(
                            fragment.requireContext(),
                            isCancel = false,
                            action = object : MaterialDialog.OnMaterialAlterDialogListener {
                                override fun onCancel() {
                                    UtilsMessage.displaySnackBar(
                                        fragment.requireView(),
                                        if (mRetrieveDone) R.string.dialog_restore_the_device_successfully else R.string.dialog_failed_to_restore_device
                                    )
                                }

                                override fun onConfirm() {
                                }

                            })
                    }
                    if (mDialogLoading?.isShowing != true) {
                        mDialogLoading?.show()
                        mHandler.postDelayed(cancelDialog, DIALOG_TIMEOUT)
                    }
                } ?: let {
                    macAddress?.let { mac ->
                        mBleManagerApi.connectWithRegister(mac, fragment, RegisterType.CALLBACK_REGISTER)
                        if (mDialogLoading == null) {
                            mDialogLoading = MaterialDialog.getLoadingDialog(
                                fragment.requireContext(),
                                isCancel = false,
                                action = object : MaterialDialog.OnMaterialAlterDialogListener {
                                    override fun onCancel() {
                                        UtilsMessage.displaySnackBar(
                                            fragment.requireView(),
                                            if (mRetrieveDone) R.string.dialog_restore_the_device_successfully else R.string.dialog_failed_to_restore_device
                                        )
                                    }

                                    override fun onConfirm() {
                                    }

                                })
                        }
                        if (mDialogLoading?.isShowing != true) {
                            mDialogLoading?.show()
                            mHandler.postDelayed(cancelDialog, DIALOG_TIMEOUT * 3)
                        }
                    }
                }

            }
            (fragment.requireActivity() as SystemActivity).doNewDevice = fun(bluetoothDevice: ExtendedBluetoothDevice) {
                mBleManagerApi.connectWithRegister(bluetoothDevice.device, RegisterType.SCAN_REGISTER)
                if (mDialogLoading == null) {
                    mDialogLoading = MaterialDialog.getLoadingDialog(
                        fragment.requireContext(),
                        isCancel = false,
                        action = object : MaterialDialog.OnMaterialAlterDialogListener {
                            override fun onCancel() {
                                UtilsMessage.displaySnackBar(
                                    fragment.requireView(),
                                    if (mNewDeviceDone) R.string.dialog_add_the_device_successfully else R.string.dialog_failed_to_add_device
                                )
                            }

                            override fun onConfirm() {
                            }

                        })
                }
                if (mDialogLoading?.isShowing != true) {
                    mDialogLoading?.show()
                    mHandler.postDelayed(cancelDialog, DIALOG_TIMEOUT)
                }
            }
        }
        mBleManagerApi.mViewDevicesLiveData.observe(fragment.viewLifecycleOwner) { devices ->
            updateDevicesStatus(devices)
        }
        mBleManagerApi.mDefaultDeviceInfoLiveData.observe(fragment.viewLifecycleOwner) {
            updateDevicesStatus(mBleManagerApi.mViewDevices)
        }
        mAdapter.mSwipeClickListener = getOnClickListener(fragment)
        mAdapter.mOnItemClick = fun(blueDevice: DeviceWithBluetooth) {
            mBleManagerApi.setDefaultDeviceInfo(blueDevice.serialNumber, blueDevice.macAddress)
        }
        mAdapter.mOnEditNameClick = fun(blueDevice: DeviceWithBluetooth) {
            MaterialDialog.getInputStringDialog(fragment.requireContext(), null, null, hint = blueDevice.deviceName) { newName ->
                mBleManagerApi.updateDeviceName(newName, blueDevice.serialNumber, blueDevice.macAddress)
            }.show()
        }
    }

    private fun updateDevicesStatus(list: List<ViewManagerDevice>) {
        val deviceWithBluetoothList: MutableList<DeviceWithBluetooth> = ArrayList()
        list.forEach { device ->
            val deviceWithBluetooth = DeviceWithBluetooth(
                deviceName = device.name,
                serialNumber = device.serialNumber,
                macAddress = device.macAddress,
                connect = mBleManagerApi.isConnected(device.macAddress),
                isSelected = mBleManagerApi.mDefaultDeviceView?.serialNumber == device.serialNumber
            )
            deviceWithBluetoothList.add(deviceWithBluetooth)
        }
        mAdapter.submitList(deviceWithBluetoothList)
    }

    override fun handle14(msg14: MSG14) {
        mRetrieveDone = true
        if (mDialogLoading?.isShowing == true) {
            mDialogLoading?.cancel()
        }
    }

    override fun handle12() {
        mNewDeviceDone = true
        if (mDialogLoading?.isShowing == true) {
            mDialogLoading?.cancel()
        }
    }

    private val cancelDialog = Runnable {
        if (mDialogLoading?.isShowing == true) {
            mDialogLoading?.cancel()
        }
    }

    override fun handle0E(errorCode: Int) {
        super.handle0E(errorCode)
        mHandler.removeCallbacks(cancelDialog)
        if (mDialogLoading?.isShowing == true) {
            mDialogLoading?.cancel()
        }
    }


    private fun getOnClickListener(fragment: Fragment) = object : SwipeRedGreenClickListener<DeviceWithBluetooth> {
        override fun onRedClick(any: DeviceWithBluetooth) {
            MaterialDialog.getAlertDialog(
                fragment.requireContext(),
                message = R.string.dialog_delete_device_tips,
                action = object : MaterialDialog.OnMaterialAlterDialogListener {
                    override fun onCancel() {
                    }

                    override fun onConfirm() {
                        if (any.connect) {
                            MaterialDialog.getDelayDialog(
                                fragment.requireContext(),
                                message = R.string.dialog_delete_device_tips_restore,
                                action = object : MaterialDialog.OnMaterialAlterDialogListener {
                                    override fun onCancel() {
                                        mBleManagerApi.disconnect(any.macAddress)
                                        mBleManagerApi.deleteDevice(any.serialNumber, any.macAddress)
                                    }

                                    override fun onConfirm() {
                                        CreateMessage.createMessage19(any.macAddress, MSG19_RestoreFactorySettings).execute()
                                    }
                                }).apply {
                                this.setCanceledOnTouchOutside(true)
                            }.show()
                        } else {
                            mBleManagerApi.deleteDevice(any.serialNumber, any.macAddress)
                        }
                    }
                }).show()

        }

        override fun onGreenClick(any: DeviceWithBluetooth) {

        }

    }

    fun getSheetDialog(fragment: Fragment, title: Any): BottomSheetDialog {
        val dialog = MaterialDialog.getBottomSheetDialogWithLayout(fragment.requireContext(), title = title)
        val mSelectorItemList = if (title == R.string.new_device) {
            listOf(
                SelectorItem(R.string.scan_for_add),
                SelectorItem(R.string.search_for_add)
            )
        } else {
            listOf(
                SelectorItem(R.string.scan_for_retrieve),
                SelectorItem(R.string.search_for_retrieve)
            )
        }
        dialog.findViewById<RecyclerView>(R.id.dialog_recycle_view)?.adapter = BottomSelectorAdapter(mSelectorItemList) {
            dialog.cancel()
            mHandler.postDelayed({
                mDialogClick(fragment, it)
            }, 200)
        }
        return dialog
    }

    val mDialogClick = object : (Fragment, SelectorItem) -> Unit {
        override fun invoke(fragment: Fragment, item: SelectorItem) {
            when (item.itemName) {
                R.string.scan_for_retrieve -> {
                    ScanQrCodeHelper.onScanQrCode(fragment.requireActivity(), REQUEST_RETRIEVE)
                }
                R.string.search_for_retrieve -> {
                    fragment.requireActivity().startActivityForResult(
                        Intent(fragment.requireActivity(), ScanActivity::class.java)
                            .apply {
                                putExtra(SCAN_FOR_RETRIEVE, true)
                            }, REQUEST_RETRIEVE
                    )
                }
                R.string.scan_for_add -> {
                    ScanQrCodeHelper.onScanQrCode(fragment.requireActivity(), REQUEST_NEW_DEVICE)
                }
                R.string.search_for_add -> {
                    fragment.requireActivity().startActivity(Intent(fragment.requireActivity(), ScanActivity::class.java))

                }
            }
        }
    }
}