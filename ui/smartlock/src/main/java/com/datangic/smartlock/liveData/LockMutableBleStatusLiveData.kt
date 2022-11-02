package com.datangic.smartlock.liveData

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.LiveData
import com.datangic.smartlock.R
import com.datangic.smartlock.ble.livedata.state.ConnectionState
import com.datangic.common.utils.Logger
import no.nordicsemi.android.ble.observer.ConnectionObserver

class LockMutableBleStatusLiveData : LiveData<LockMutableBleStatusLiveData>() {
    private val TAG = "LockBleStatusLiveDate"

    /**
     * key MacAddress
     * value Conn
     */
    val mBleStatusMap: MutableMap<String, ConnectionState> = mutableMapOf()

    var mConnectionState: ConnectionState = ConnectionState.Unknow()
        private set

    var macAddress: String? = null
    var device: BluetoothDevice? = null
        private set

    fun scanning(address: String) {
        macAddress = address
        mBleStatusMap[address] = ConnectionState.Scanning()
        mConnectionState = ConnectionState.Scanning()
        device = null
        postValue(this)
    }

    fun scanning(address: List<String>) {
        address.forEach {
            macAddress = it
            mBleStatusMap[it] = ConnectionState.Scanning()
        }
        mConnectionState = ConnectionState.Scanning()
        device = null
        postValue(this)
    }

    fun scanTimeout(address: String) {
        macAddress = address
        mBleStatusMap[address] = ConnectionState.ScanTimeout()
        mConnectionState = ConnectionState.ScanTimeout()
        postValue(this)
    }

    fun scanTimeout(address: List<String>) {
        address.forEach { i ->
            macAddress = i
            mBleStatusMap[i] = ConnectionState.ScanTimeout()
        }
        mConnectionState = ConnectionState.ScanTimeout()
        postValue(this)
    }

//    fun scanStop(address: String) {
//        macAddress = address
//        if (mBleStatusMap[address] is ConnectionState.Initializing ||
//            mBleStatusMap[address] is ConnectionState.Ready
//        ) return
//        mBleStatusMap[address] = ConnectionState.ScanStop()
//        mConnectionState = ConnectionState.ScanStop()
//        device = null
//        postValue(this)
//    }

    fun scanStop() {
        for (i in mBleStatusMap) {
            macAddress = i.key
            if (i.value is ConnectionState.Initializing ||
                i.value is ConnectionState.Ready
            ) continue
            mBleStatusMap[i.key] = ConnectionState.ScanStop()
        }

        mConnectionState = ConnectionState.ScanStop()
        device = null
        postValue(this)
    }

    fun deviceFound(device: BluetoothDevice) {
        macAddress = device.address
        this.device = device
        mBleStatusMap[device.address] = ConnectionState.DeviceFound(device)
        mConnectionState = ConnectionState.DeviceFound(device)
        postValue(this)
    }

    fun changeState(connectionState: ConnectionState) {
        Logger.e(TAG, "Change mac = ${connectionState.device?.address} State = ${connectionState.state}")
        connectionState.device?.let { bluetoothDevice ->
            macAddress = bluetoothDevice.address
            mBleStatusMap[bluetoothDevice.address] = connectionState
            device = bluetoothDevice
        }
        mConnectionState = connectionState
        postValue(this)
    }

    fun clear() {
        Logger.e(TAG, "BLE state clear")
        device = null
        macAddress = null
    }

    fun getStatusMessageByMac(macAddress: String, context: Context): String {
        return mBleStatusMap[macAddress]?.let {
            it.getStatusMessage(context)
        } ?: context.getString(R.string.ble_disconnected)
    }

    companion object {

        fun ConnectionState.getStatusMessage(context: Context): String {
            val desId = when (this) {
                is ConnectionState.Scanning -> R.string.ble_scanning
                is ConnectionState.DeviceFound -> R.string.ble_found
                is ConnectionState.Connecting -> R.string.ble_connecting
                is ConnectionState.Initializing -> R.string.ble_initializing
                is ConnectionState.Ready -> R.string.ble_connected
                is ConnectionState.Disconnected -> {
                    when (this.reason) {
                        ConnectionObserver.REASON_LINK_LOSS -> {
                            R.string.ble_link_loss
                        }
                        ConnectionObserver.REASON_NOT_SUPPORTED -> {
                            R.string.ble_not_supported
                        }
                        ConnectionObserver.REASON_TERMINATE_PEER_USER,
                        ConnectionObserver.REASON_SUCCESS -> {
                            R.string.ble_terminate_peer_user
                        }
                        ConnectionObserver.REASON_TERMINATE_LOCAL_HOST -> {
                            R.string.ble_terminate_peer_device
                        }
                        ConnectionObserver.REASON_TIMEOUT -> {
                            R.string.ble_connect_timeout
                        }
                        else -> R.string.ble_disconnected
                    }
                }
                is ConnectionState.ScanTimeout -> R.string.ble_scan_timeout
                else -> R.string.ble_disconnected
            }
            return context.getString(desId)
        }
    }
}