package com.datangic.smartlock.ble

import android.bluetooth.BluetoothDevice
import cn.dttsh.dts1586.DTS1586
import com.datangic.common.utils.Logger
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.utils.ParserUtils


abstract class ReceivedDataCallback : ProfileDataCallback {

    private val TAG = ReceivedDataCallback::class.java.simpleName

    private class Session {
        val MESSAGE_MAX_LEN = 256
        var mMessageLen = 0
        var mReceivedDataLen = 0
        var mReceivedBytes: ByteArray = ByteArray(MESSAGE_MAX_LEN)
        fun isNewMessage(): Boolean {
            return mReceivedDataLen == 0
        }

        fun clearSession() {
            mMessageLen = 0
            mReceivedDataLen = 0
            mReceivedBytes = ByteArray(MESSAGE_MAX_LEN)
        }

        companion object {
            private val session by lazy { Session() }
            fun newInstance(): Session {
                return session
            }
        }
    }

    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        val mSession = Session.newInstance()
        if (data.value == null || (data.value!!.size < 5 && mSession.isNewMessage())) return
        if (mSession.isNewMessage()) {
            mSession.mMessageLen = DTS1586.parserCmdLen(data.value)
            if (mSession.mMessageLen > mSession.MESSAGE_MAX_LEN || mSession.mMessageLen <= 0) {
                return
            }
        }
        for (i in data.value!!.indices) {
            if (mSession.mReceivedDataLen + i > mSession.mMessageLen) {
                mSession.clearSession()
                return
            }
            mSession.mReceivedBytes[mSession.mReceivedDataLen + i] = data.value!![i]
        }
        mSession.mReceivedDataLen += data.value?.size ?: 0
        if (mSession.mReceivedDataLen < mSession.mMessageLen || mSession.mMessageLen <= 0) {
            return
        }
        val mMessageBytes = ByteArray(mSession.mMessageLen)
        System.arraycopy(mSession.mReceivedBytes, 0, mMessageBytes, 0, mSession.mMessageLen)
        mSession.clearSession()
        Logger.i(TAG, "value = ${ParserUtils.parse(mMessageBytes)}")
        onMessageReceived(device, mMessageBytes)
    }

    abstract fun onMessageReceived(device: BluetoothDevice, bytes: ByteArray)
}
