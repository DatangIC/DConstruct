package com.datangic.smartlock.ble.livedata.state


import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.annotation.DisconnectionReason
import no.nordicsemi.android.ble.observer.ConnectionObserver

@Suppress("unused")
sealed class ConnectionState(val device: BluetoothDevice?, val state: State) {
    /** The connection state. This can be used in <i>switch</i> in Java. */
    enum class State {
        CONNECTING, INITIALIZING, READY, DISCONNECTING, DISCONNECTED, SCANNING, STOP_SCAN, SCAN_TIMEOUT, DEVICE_FOUND, UNKNOW
    }

    /** UNKNOW**/
    class Unknow : ConnectionState(null, State.UNKNOW)

    /** A device was be found. */
    class DeviceFound(device: BluetoothDevice) : ConnectionState(device, State.DEVICE_FOUND)

    /** Stop Scan. */
    class ScanStop(device: BluetoothDevice? = null) : ConnectionState(device, State.STOP_SCAN)

    /** Scanning for device. */
    class Scanning(device: BluetoothDevice? = null) : ConnectionState(device, State.SCANNING)

    /** Scanning for device. */
    class ScanTimeout(device: BluetoothDevice? = null) : ConnectionState(device, State.SCAN_TIMEOUT)

    /** A connection to the device was initiated. */
    class Connecting(device: BluetoothDevice) : ConnectionState(device, State.CONNECTING)

    /** The device has connected and begun service discovery and initialization. */
    class Initializing(device: BluetoothDevice) : ConnectionState(device, State.INITIALIZING)

    /** The initialization is complete, and the device is ready to use. */
    class Ready(device: BluetoothDevice) : ConnectionState(device, State.READY)

    /** The disconnection was initiated. */
    class Disconnecting(device: BluetoothDevice) : ConnectionState(device, State.DISCONNECTING)

    /**
     * The device disconnected or failed to connect.
     *
     * @param reason The reason of disconnection.
     */
    class Disconnected(device: BluetoothDevice, @DisconnectionReason val reason: Int) : ConnectionState(device, State.DISCONNECTED) {
        /** Whether the device, that was connected using auto connect, has disconnected. */
        val isLinkLoss: Boolean
            get() = reason == ConnectionObserver.REASON_LINK_LOSS

        /** Whether at least one required service was not found. */
        val isNotSupported: Boolean
            get() = reason == ConnectionObserver.REASON_NOT_SUPPORTED

        /** Whether the connection timed out. */
        val isTimeout: Boolean
            get() = reason == ConnectionObserver.REASON_TIMEOUT
    }


    /**
     * Whether the target device is connected, or not.
     */
    val isConnected: Boolean
        get() = this is Initializing || this is Ready

    /**
     * Whether the target device is ready to use.
     */
    val isReady: Boolean
        get() = this is Ready

}