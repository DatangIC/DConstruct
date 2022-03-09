package com.datangic.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.datangic.data.database.view.ViewDeviceStatus
import com.datangic.data.database.table.DeviceStatus

@Dao
interface DeviceStatusDao : BaseDao<DeviceStatus> {

    @Query("SELECT * FROM DeviceStatus WHERE mac_address = :macAddress AND serial_number =:serialNumber LIMIT 1")
    fun getDeviceStatus(serialNumber: String, macAddress: String): DeviceStatus?


    @Query("SELECT * FROM ViewDeviceStatus DeviceStatus WHERE macAddress = :macAddress AND serialNumber =:serialNumber LIMIT 1")
    fun getViewDeviceStatusLiveData(serialNumber: String, macAddress: String): LiveData<ViewDeviceStatus>

    @Query("SELECT * FROM ViewDeviceStatus DeviceStatus WHERE macAddress = :macAddress AND serialNumber =:serialNumber LIMIT 1")
    fun getViewDeviceStatus(serialNumber: String, macAddress: String): ViewDeviceStatus?

}