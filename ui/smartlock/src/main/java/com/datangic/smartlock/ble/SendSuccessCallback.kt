package com.datangic.smartlock.ble

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import no.nordicsemi.android.ble.callback.SuccessCallback

open class SendSuccessCallback : SuccessCallback {
    var mSendSuccessLiveData: MutableLiveData<BluetoothDevice> = MutableLiveData(null)
    override fun onRequestCompleted(device: BluetoothDevice) {
        mSendSuccessLiveData.postValue(device)
    }
}