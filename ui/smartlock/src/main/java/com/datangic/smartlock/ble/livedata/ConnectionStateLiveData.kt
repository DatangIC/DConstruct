package com.datangic.smartlock.ble.livedata


import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import com.datangic.smartlock.ble.livedata.state.ConnectionState
import no.nordicsemi.android.ble.observer.ConnectionObserver

@Suppress("unused")
class ConnectionStateLiveData(bluetoothDevice: BluetoothDevice) : LiveData<ConnectionState>(
    ConnectionState.Disconnected(bluetoothDevice, reason = ConnectionObserver.REASON_UNKNOWN)
), ConnectionObserver {

    init {
        value = ConnectionState.Disconnected(bluetoothDevice, reason = ConnectionObserver.REASON_UNKNOWN)
    }


    fun onScanning(device: BluetoothDevice) {
        postValue(ConnectionState.Scanning(device))
    }

    fun onDeviceFounding(device: BluetoothDevice) {
        postValue(ConnectionState.DeviceFound(device))
    }

    override fun onDeviceConnecting(device: BluetoothDevice) {
        postValue(ConnectionState.Connecting(device))
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        postValue(ConnectionState.Initializing(device))
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        postValue(ConnectionState.Ready(device))
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        postValue(ConnectionState.Disconnecting(device))
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        postValue(ConnectionState.Disconnected(device, reason))
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        postValue(ConnectionState.Disconnected(device, reason))
    }

}