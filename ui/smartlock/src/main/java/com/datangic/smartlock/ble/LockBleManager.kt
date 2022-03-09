package com.datangic.smartlock.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.datangic.smartlock.ble.livedata.ObservableBleManager
import com.datangic.smartlock.utils.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.callback.DataReceivedCallback
import java.util.*


class LockBleManager(
    device: BluetoothDevice,
    context: Context,
    val mSuccessCallback: SendSuccessCallback,
    val mDataReceivedCallback: DataReceivedCallback
) : ObservableBleManager(device, context) {
    private val TAG = LockBleManager::class.simpleName

    companion object {
        val MESH_PROXY_UUID: UUID = UUID.fromString("00001828-0000-1000-8000-00805F9B34FB")
        val SERVICE_UUID: UUID = UUID.fromString("6e400001-B5A3-F393-E0A9-E50E24DCCA9E")
        val CHAR_WRITE_UUID: UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
        val CHAR_READ_UUID: UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
        val SERVICE_UUID2: UUID = UUID.fromString("0000FFEA-0000-1000-8000-00805F9B34FB")
        val CHAR_WRITE_UUID2: UUID = UUID.fromString("0000ffeb-0000-1000-8000-00805f9b34fb")
        val CHAR_READ_UUID2: UUID = UUID.fromString("0000ffec-0000-1000-8000-00805f9b34fb")
    }

    private var isSupported: Boolean = false

    // Client characteristics
    var mWriteCharacteristic: BluetoothGattCharacteristic? = null

    // Client characteristics
    var mReadCharacteristic: BluetoothGattCharacteristic? = null


    override fun getGattCallback(): BleManagerGattCallback {
        return LockBleManagerGattCallback()
    }

    override fun log(priority: Int, message: String) {
        GlobalScope.launch {
            Logger.v(TAG, "priority=$priority\n message=$message")
        }
    }

    override fun shouldClearCacheWhenDisconnected(): Boolean {
        return true
    }

    fun getConnectionStateLiveData() = mConnectionStateLiveData

    /**
     * BluetoothGatt callbacks object
     */
    private inner class LockBleManagerGattCallback : BleManagerGattCallback() {
        override fun initialize() {
            setNotificationCallback(mReadCharacteristic).with(mDataReceivedCallback)
            readCharacteristic(mWriteCharacteristic).with(mDataReceivedCallback).enqueue()
            readCharacteristic(mReadCharacteristic).with(mDataReceivedCallback).enqueue()
            enableNotifications(mReadCharacteristic).enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            var service = gatt.getService(SERVICE_UUID2)
            if (service != null) {
                this@LockBleManager.mWriteCharacteristic = service.getCharacteristic(CHAR_WRITE_UUID2)
                this@LockBleManager.mReadCharacteristic = service.getCharacteristic(CHAR_READ_UUID2)
            } else {
                service = gatt.getService(SERVICE_UUID)
                if (service != null) {
                    this@LockBleManager.mWriteCharacteristic = service.getCharacteristic(CHAR_WRITE_UUID)
                    this@LockBleManager.mReadCharacteristic = service.getCharacteristic(CHAR_READ_UUID)
                }
            }
            // Validate properties
            var writeRequest = false
            mWriteCharacteristic?.let {
                val properties: Int = it.properties
                writeRequest = properties and BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT != 0
                it.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            }
            var notify = false
            mReadCharacteristic?.let {
                val properties: Int = it.properties
                notify = properties and BluetoothGattCharacteristic.PROPERTY_READ != 0
            }
            // Return true if all required services have been found
            isSupported = mWriteCharacteristic != null && mReadCharacteristic != null && notify && writeRequest
            Logger.v(TAG, "BLE Server isSupported=${isSupported}")
            return isSupported
        }

        override fun onDeviceDisconnected() {
            mWriteCharacteristic = null
            mReadCharacteristic = null
        }
    }


    fun send(bytes: ByteArray) {
        GlobalScope.launch {
            val bufSize = 137
            if (bytes.size > bufSize) {
                val count = bytes.size / bufSize
                for (i in 0 until count) {
                    writeCharacteristic(mWriteCharacteristic, bytes.copyOfRange(i * bufSize, i * bufSize + bufSize)).enqueue()
                }
                writeCharacteristic(mWriteCharacteristic, bytes.copyOfRange(count * bufSize, bytes.size)).done(mSuccessCallback).enqueue()
            } else {
                writeCharacteristic(mWriteCharacteristic, bytes).done(mSuccessCallback).enqueue()
            }

        }
    }
}