package com.datangic.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.table.DeviceLog
import com.datangic.data.database.view.ViewDeviceLog

@Dao
interface DeviceLogDao : BaseDao<DeviceLog> {

    @Query("SELECT * from DeviceLog WHERE serial_number= :serialNumber and mac_address=:macAddress and device_user_id=:deviceUserId and log_id=:logId and log_state=:logState LIMIT 1")
    fun getDeviceLog(
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        logId: Int,
        logState: DeviceEnum.LogState
    ): DeviceLog?

    @Query("Delete  from DeviceLog WHERE serial_number= :serialNumber and mac_address=:macAddress and device_user_id=:deviceUserId and log_id=:logId and log_state=:logState")
    fun deleteDeviceLog(
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        logId: Int,
        logState: DeviceEnum.LogState
    )

    @Query("SELECT * FROM ViewDeviceLog WHERE serialNumber= :serialNumber and macAddress=:macAddress and deviceUserId=:deviceUserId ORDER BY logCreateAt")
    fun getViewDeviceLogsAsLiveData(
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>
    ): LiveData<List<ViewDeviceLog>>


    @Query("SELECT * FROM ViewDeviceLog WHERE serialNumber= :serialNumber and macAddress=:macAddress  ORDER BY logCreateAt")
    fun getViewDeviceLogsAsLiveData(
        serialNumber: String,
        macAddress: String
    ): LiveData<List<ViewDeviceLog>>

    fun insertOrUpdate(item: DeviceLog) {
        val itemDB = getDeviceLog(item.serialNumber, item.macAddress, deviceUserId = item.deviceUserId, item.logId, item.logState)
        if (itemDB == null) {
            item.createAt = System.currentTimeMillis() / 1000
            item.updateAt = System.currentTimeMillis() / 1000
            insert(item)
        } else {
            itemDB.updateAt = System.currentTimeMillis() / 1000
            update(itemDB)
        }
    }
}