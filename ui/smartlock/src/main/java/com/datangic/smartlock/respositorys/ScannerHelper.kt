package com.datangic.smartlock.respositorys

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import com.datangic.common.Config
import com.datangic.common.Config.SCAN_DURATION
import com.datangic.smartlock.ble.LockBleManager.Companion.SERVICE_UUID2
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.liveData.ScannerLiveData
import com.datangic.smartlock.liveData.ScannerStateLiveData
import com.datangic.common.utils.Logger
import com.datangic.smartlock.utils.UtilsBle
import no.nordicsemi.android.support.v18.scanner.*


open class ScannerHelper(val mContext: Context) {
    private val TAG: String = ScannerHelper::class.java.simpleName

    val mScannerStateLiveData by lazy { ScannerStateLiveData() }
    val mScannerLiveData: ScannerLiveData by lazy { ScannerLiveData() }
    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    /**
     * Register for required broadcast receivers.
     */
    protected fun registerBroadcastReceivers() {
        mContext.registerReceiver(mBluetoothStateBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        if (UtilsBle.isMarshmallowOrAbove()) {
            mContext.registerReceiver(mLocationProviderChangedReceiver, IntentFilter(LocationManager.MODE_CHANGED_ACTION))
        }
    }

    /**
     * Unregister for required broadcast receivers.
     */
    fun unregisterBroadcastReceivers() {
        mContext.unregisterReceiver(mBluetoothStateBroadcastReceiver)
        if (UtilsBle.isMarshmallowOrAbove()) {
            mContext.unregisterReceiver(mLocationProviderChangedReceiver)
        }
    }

    /**
     * Broadcast receiver to monitor the changes in the location provider
     */
    private val mLocationProviderChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val enabled: Boolean = UtilsBle.isLocationEnabled(context)
            mScannerStateLiveData.setLocationEnabled(enabled)
        }
    }

    /**
     * Broadcast receiver to monitor the changes in the bluetooth adapter
     */
    private val mBluetoothStateBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
            val previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF)
            when (state) {
                BluetoothAdapter.STATE_ON -> mScannerStateLiveData.bluetoothEnabled()
                BluetoothAdapter.STATE_TURNING_OFF, BluetoothAdapter.STATE_OFF -> if (previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
                    stopScan()
                    mScannerStateLiveData.bluetoothDisabled()
                }
            }
        }
    }

    private val mScanCallbacks: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            try {
                if (UtilsBle.isLocationEnabled(mContext) && UtilsBle.isLocationEnabled(mContext))
                    updateScannerLiveData(result)
            } catch (ex: Exception) {
                Logger.e(TAG, "Error: " + ex.message)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {}

        override fun onScanFailed(errorCode: Int) {
            mScannerStateLiveData.scanStopped()
        }

    }

    private fun updateScannerLiveData(result: ScanResult) {
        val scanRecord = result.scanRecord
        if (scanRecord != null) {
            if (scanRecord.bytes != null) {
                mScannerLiveData.deviceDiscovered(result)
                mScannerStateLiveData.deviceFound()
            }
        }
    }


    /**
     * Start reconnection to the device
     */
    fun startBLEScan(context: Context, filters: MutableList<ScanFilter>? = null) {
        val nFilters: MutableList<ScanFilter> = filters ?: getDefaultFilters()
        Logger.e(TAG, "startBLEScan")
        if (mScannerStateLiveData.isScanning()) return
        mScannerStateLiveData.scanningStarted()
        mScannerLiveData.clear()
        // Scanning Settings
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .setUseHardwareFilteringIfSupported(true)
            .build()
        if (checkBluetoothValid(context)) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.startScan(nFilters, settings, mScanCallbacks)
            mHandler.postDelayed({
                if (mScannerStateLiveData.isScanning()) {
                    stopScan()
                    mScannerStateLiveData.scanTimeout()
                }
            }, SCAN_DURATION)
        }
    }

    fun getWriteFilters(): MutableList<ScanFilter> {
        val filters: MutableList<ScanFilter> = ArrayList()
        for (i in Config.FILTER_BLE_NAME_WRITE) {
            filters.add(ScanFilter.Builder().setDeviceName(i).build())
        }
        return filters
    }

    private fun getDefaultFilters(): MutableList<ScanFilter> {
        val filters: MutableList<ScanFilter> = ArrayList()
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(SERVICE_UUID2)).build())
        for (i in Config.FILTER_BLE_NAME) {
            filters.add(ScanFilter.Builder().setDeviceName(i).build())
        }
        for (i in Config.FILTER_BLE_SERVER_DATA_UUID) {
            filters.add(ScanFilter.Builder().setServiceData(ParcelUuid(i.key), i.value).build())
        }
        return filters
    }

    private fun checkBluetoothValid(context: Context): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null) {
            MaterialDialog.getAlertDialog(
                context,
                message = "你的设备不具备蓝牙功能",
                isError = true,
                isCancel = false
            ).show()
            return false
        }
        return if (!adapter.isEnabled) {
            MaterialDialog.getAlertDialog(
                context = context,
                message = "蓝牙设备未打开,请开启此功能后重试!",
                isError = true,
                isCancel = false,
                action = object : MaterialDialog.OnMaterialAlterDialogListener {
                    override fun onCancel() {

                    }

                    override fun onConfirm() {
                        val mIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        context.startActivity(mIntent)
                    }
                }).show()
            false
        } else {
            true
        }
    }

    /**
     * stop scanning for bluetooth devices.
     */
    fun stopScan(clear: Boolean = false) {
        if (mScannerStateLiveData.isScanning()) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(mScanCallbacks)
            mScannerStateLiveData.scanStopped()
            if (clear) {
                mScannerLiveData.clear()
            }
        }
    }
}