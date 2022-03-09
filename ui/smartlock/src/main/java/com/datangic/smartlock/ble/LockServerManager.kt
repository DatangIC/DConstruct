package com.datangic.smartlock.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import com.datangic.smartlock.utils.Logger
import no.nordicsemi.android.ble.BleServerManager
import java.util.*

class LockServerManager(context: Context) : BleServerManager(context) {
    private val TAG = LockServerManager::class.simpleName
    override fun log(priority: Int, message: String) {
        Logger.v(TAG, "priority= $priority message=$message")
    }

    override fun initializeServer(): MutableList<BluetoothGattService> {
        val services: MutableList<BluetoothGattService> = ArrayList()
        services.add(
                service(LockBleManager.SERVICE_UUID2,
                        characteristic(LockBleManager.CHAR_WRITE_UUID2,
                                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                                BluetoothGattCharacteristic.PERMISSION_WRITE))
        )

        services.add(
                service(LockBleManager.SERVICE_UUID2,
                        characteristic(LockBleManager.CHAR_READ_UUID2,
                                BluetoothGattCharacteristic.PROPERTY_READ,
                                BluetoothGattCharacteristic.PERMISSION_READ))
        )
        return services
    }
}