package com.datangic.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.datangic.data.database.DeviceAndDeviceStatus
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.data.database.table.Device
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao : BaseDao<Device> {

    @Query("SELECT * FROM Device")
    fun getDevices(): Flow<List<Device>>

    @Query("SELECT * FROM ViewManagerDevice WHERE uid =(:userId)")
    fun getManagerDevices(userId: Int): Flow<List<ViewManagerDevice>>

    @Query("SELECT * FROM ViewManagerDevice WHERE macAddress = (:macAddress) LIMIT 1")
    fun getManagerDevices(macAddress: String): ViewManagerDevice?

    @Query("SELECT * FROM Device WHERE mac_address = (:macAddress) LIMIT 1")
    fun getDeviceByMac(macAddress: String): Device?

    @Query("SELECT * FROM Device WHERE mac_address = (:macAddress) AND serial_number=:serialNumber LIMIT 1")
    fun getDeviceAsLiveData(serialNumber: String, macAddress: String): LiveData<Device>

    @Query("SELECT * FROM Device WHERE mac_address = (:macAddress) AND serial_number=:serialNumber LIMIT 1")
    fun getDevice(serialNumber: String, macAddress: String): Device?

    @Query("SELECT  count(*) FROM Device")
    fun getCount(): Int

    @Query("SELECT  count(*) FROM Device WHERE name =:deviceName")
    fun getNameCount(deviceName: String): Int

    @Query("DELETE FROM Device WHERE mac_address = (:macAddress) AND serial_number=:serialNumber")
    fun deleteDevice(serialNumber: String, macAddress: String)

    @Transaction
    @Query("SELECT * FROM Device WHERE serial_number=:serialNumber and mac_address = (:macAddress) LIMIT 1")
    fun getDeviceWithStatusByMac(serialNumber: String, macAddress: String): DeviceAndDeviceStatus?

    @Transaction
    @Query("SELECT * FROM Device WHERE serial_number=:serialNumber and mac_address = (:macAddress) LIMIT 1")
    fun getDeviceWithStatusAsFlow(serialNumber: String, macAddress: String): Flow<DeviceAndDeviceStatus>
}