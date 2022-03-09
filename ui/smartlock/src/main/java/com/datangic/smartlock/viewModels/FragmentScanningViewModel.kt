package com.datangic.smartlock.viewModels

import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.ScannerDeviceAdapter
import com.datangic.smartlock.ble.ReceivedMessageHandle.RegisterType
import com.datangic.smartlock.components.DynamicTitle
import com.datangic.data.database.table.Device
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.parcelable.ExtendedBluetoothDevice
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.ui.scanning.ScanActivity
import com.datangic.smartlock.ui.scanning.ScanningFragmentDirections
import com.datangic.smartlock.utils.*
import com.datangic.smartlock.widgets.LoadingMaterialButton
import kotlinx.coroutines.launch

class FragmentScanningViewModel(application: Application, bleManagerApi: BleManagerApiRepository) : BaseViewModel(application, bleManagerApi) {

    private val TAG = FragmentScanningViewModel::class.simpleName
    val mScannerDeviceAdapter by lazy { ScannerDeviceAdapter(mBleManagerApi.mScannerRepository.mScannerLiveData.mDevices as ArrayList<ExtendedBluetoothDevice>) }
    val mScanningButtonName by lazy { DynamicTitle(R.string.scan_start) }

    fun setObserve(activity: FragmentActivity, loadingButton: LoadingMaterialButton) {
        /**
         * Scanning Item List
         */
        mBleManagerApi.mScannerRepository.mScannerLiveData.observe(activity) {
            val i: Int = it.mUpdatedDeviceIndex
            if (i != -1) {
                mScannerDeviceAdapter.notifyItemChanged(i)
            } else {
                mScannerDeviceAdapter.mItemList = it.mDevices
                mScannerDeviceAdapter.notifyDataSetChanged()
            }
        }
        /**
         * Scanning State
         */
        mBleManagerApi.mScannerRepository.mScannerStateLiveData.observe(activity) {
            if (it.isScanning()) {
                mScanningButtonName.title = R.string.scan_stop
                (activity as ScanActivity).mToolbarRepository.mScannerActivityToolBar.isProgress = true
                loadingButton.stopAnim()
            } else {
                mScanningButtonName.title = R.string.scan_start
                loadingButton.stopAnim()
                (activity as ScanActivity).mToolbarRepository.mScannerActivityToolBar.isProgress = false
            }
        }
    }


    fun getButtonClickListener(fragment: Fragment): View.OnClickListener {
        return View.OnClickListener {
            if (mBleManagerApi.mScannerRepository.mScannerStateLiveData.isScanning()) {
                mBleManagerApi.mScannerRepository.stopScan()
                (it as LoadingMaterialButton).startAnim()
            } else {
                mBleManagerApi.mScannerRepository.startScan(fragment)
                (it as LoadingMaterialButton).startAnim()
            }
        }
    }

    fun setItemsOnClickListener(context: Fragment) {
        mScannerDeviceAdapter.setOnItemClickListener(object : ScannerDeviceAdapter.OnItemClickListener {
            override fun onItemClick(view: View, device: ExtendedBluetoothDevice) {
                viewModelScope.launch {
                    when {
                        context.requireActivity().intent.extras?.getBoolean(SCAN_FOR_RETRIEVE, false) == true -> {
                            if (!hasDevice(context, device.device.address)) {
                                context.requireActivity().setResult(RESULT_OK, Intent().putExtra(SCAN_BLE_RESULT, device))
                                context.requireActivity().finish()
                            }
                        }
                        context.requireActivity().intent.extras?.getInt(SCAN_FOR_ACTION, 0) == REQUEST_SET_DEVICE_INFO -> {
                            if (!hasDevice(context, device.device.address)) {
                                context.requireActivity().intent.extras?.getString(SCAN_QRCODE_RESULT, null)?.let {
                                    UtilsMessage.getMacAddressFromDeviceInfo(it)
                                        ?.let { it1 -> mBleManagerApi.connectWithSetDeviceInfo(device.device, it1) }
                                    showLoadingDialog(5 * 1000L)
                                }
                            }
                        }
                        context.requireActivity().intent.extras?.getInt(SCAN_FOR_ACTION, 0) == REQUEST_SET_SECRET_CODE -> {
                            mBleManagerApi.connectWithRegister(device.device, RegisterType.SET_SECRET_CODE)
                            showLoadingDialog(5 * 1000L)
                        }
                        else -> {
                            if (!hasDevice(context, device.device.address)) {
                                view.findNavController()
                                    .navigate(ScanningFragmentDirections.actionNavigationScanningToNavigationVerifying(device, null))
                            }
                        }
                    }
                }
            }
        }
        )
    }

    private suspend fun hasDevice(fragment: Fragment, macAddress: String): Boolean {
        val device: Device? = mBleManagerApi.getDeviceByMac(macAddress)
        device?.let {
            Logger.e(TAG, "has Device")
            MaterialDialog.getAlertDialog(fragment.requireContext(), message = R.string.dialog_device_exists, isCancel = false).show()
            return true
        } ?: let {
            return false
        }
    }

}
