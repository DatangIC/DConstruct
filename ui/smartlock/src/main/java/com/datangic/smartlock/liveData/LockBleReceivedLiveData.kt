package com.datangic.smartlock.liveData

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import cn.dttsh.dts1586.DTS1586
import cn.dttsh.dts1586.MSG
import com.datangic.smartlock.utils.Logger
import no.nordicsemi.android.ble.utils.ParserUtils

class LockBleReceivedLiveData : LiveData<LockBleReceivedLiveData>() {
    private val TAG = LockBleReceivedLiveData::class.simpleName

    var device: BluetoothDevice? = null
    var msg: MSG = MSG()
    var mark: Int = 0


    fun receivedData(device: BluetoothDevice, bytes: ByteArray) {
        this.device = device
        this.mark = device.hashCode()
        this.msg = DTS1586.parserCmd(this.mark, bytes)
        Logger.e(
            TAG, "Service Message = 0x${msg.tag.toString(16)} " +
                    "errCode = 0x${msg.errCode.toString(16)} " +
                    "data = ${ParserUtils.parse(bytes)}"
        )
        value = this
    }
}