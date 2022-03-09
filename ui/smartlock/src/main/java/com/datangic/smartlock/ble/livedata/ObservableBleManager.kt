package com.datangic.smartlock.ble.livedata

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.LiveData
import com.datangic.smartlock.ble.livedata.state.ConnectionState
import no.nordicsemi.android.ble.BleManager

/**
 * The Observable Ble Manager extends [BleManager] and adds support for observing
 * connection and bond state using AndroidX [LiveData].
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class ObservableBleManager(device: BluetoothDevice, context: Context) : BleManager(context) {


    protected val mConnectionStateLiveData = ConnectionStateLiveData(device)

    init {
        setConnectionObserver(mConnectionStateLiveData)
    }

}