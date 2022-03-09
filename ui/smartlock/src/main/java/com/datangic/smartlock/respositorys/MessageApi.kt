package com.datangic.smartlock.respositorys

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LifecycleOwner
import cn.dttsh.dts1586.MSG
import com.datangic.smartlock.ble.*
import com.datangic.smartlock.liveData.LockBleReceivedLiveData
import kotlin.reflect.KFunction1

class MessageApi(mDatabaseRepository: DatabaseRepository, execute: (BluetoothDevice, MSG) -> CreateMessage.State) {
    val mSendSuccessCallbacks = SendSuccessCallback()
    val mReceiveMessageCallbacks = object : ReceivedDataCallback() {
        override fun onMessageReceived(device: BluetoothDevice, bytes: ByteArray) {
            mReceivedDataLiveData.receivedData(device, bytes)
        }
    }
    val mReceivedDataLiveData: LockBleReceivedLiveData by lazy { LockBleReceivedLiveData() }
    private val mMessageOperation = MessageOperation(mDatabaseRepository, execute)

    init {
        mReceivedDataLiveData.observeForever {
            mMessageOperation.messageObserver(it)
        }
    }

    fun getMessageOperation(): MessageOperation {
        return mMessageOperation
    }

    fun setReceivedDataObserver(lifecycleOwner: LifecycleOwner, action: (LockBleReceivedLiveData) -> Unit) {
        mReceivedDataLiveData.observe(lifecycleOwner) {
            action(it)
        }
    }
}