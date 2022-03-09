package com.datangic.smartlock.liveData

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import cn.dttsh.dts1586.MSG

class LockBleSendMessageLiveData : LiveData<LockBleSendMessageLiveData>() {
    private val TAG = LockMutableBleStatusLiveData::class.simpleName
    var device: BluetoothDevice? = null
    var msg: MSG = MSG()

    fun sendMessageData(device: BluetoothDevice, msg: MSG) {
        if (msg.tag in listOf(
                        MSG.M11,
                        MSG.M15,
                        MSG.M19,
                        MSG.M1B,
                        MSG.M1D,
                        MSG.M25,
                        MSG.M29,
                        MSG.M2D,
                        MSG.M49
                )) {
            this.device = device
            this.msg = msg
            postValue(this)
        }
    }
}