package com.datangic.data.database.view

import androidx.room.DatabaseView
import com.datangic.data.database.table.DeviceEnum

@DatabaseView(
    "SELECT " +
            "DeviceLog.serial_number AS serialNumber," +
            "DeviceLog.mac_address AS macAddress," +
            "DeviceLog.device_user_id AS deviceUserID," +
            "DeviceLog.log_id AS logId," +
            "DeviceLog.log_unlock_type AS logUnlockType ," +
            "DeviceLog.log_state AS logState," +
            "DeviceLog.log_lock_id AS logLockId," +
            "DeviceLog.log_create_at AS logCreateAt," +
            "Device.name AS deviceName," +
            "DeviceUser.device_username AS deviceUserName " +
            " FROM DeviceLog " +
            " INNER JOIN Device ON Device.serial_number = DeviceLog.serial_number " +
            " AND Device.mac_address = DeviceLog.mac_address " +
            " LEFT JOIN DeviceUser ON DeviceUser.serial_number = DeviceLog.serial_number " +
            " AND DeviceUser.mac_address = DeviceLog.mac_address " +
            " AND DeviceUser.device_user_id = DeviceLog.device_user_id "
)
data class ViewDeviceLog(
    val serialNumber: String,
    val macAddress: String,
    val deviceUserID: Pair<Int, String>,
    val logId: Int,
    val logUnlockType: DeviceEnum.UnlockType,
    val logState: DeviceEnum.LogState,
    val logLockId: Int,
    val logCreateAt: Long,
    val deviceName: String?,
    val deviceUserName: String?
)