package com.datangic.data.database.view

import androidx.room.DatabaseView
import com.datangic.data.database.table.DeviceEnum

@DatabaseView("SELECT " +
        "DeviceKey.serial_number AS serialNumber," +
        "DeviceKey.mac_address AS macAddress," +
        "DeviceKey.device_user_id AS deviceUserID," +
        "DeviceKey.dead_time AS deadTime," +
        "DeviceKey.key_type AS keyType," +
        "DeviceKey.key_name AS keyName," +
        "DeviceKey.key_value AS keyValue," +
        "DeviceKey.key_lock_id AS keyLockId," +
        "DeviceKey.create_at AS createAt," +
        "DeviceKey.update_at AS updateAt," +
        "Device.temporary_password_secret_code AS temporaryPasswordSecretCode," +
        "Device.secret_code AS secretCode," +
        "Device.temp_password_without_asterisk AS temporaryPasswordWithoutAsterisk " +
        " FROM DeviceKey " +
        " INNER JOIN Device ON Device.serial_number = DeviceKey.serial_number " +
        " AND Device.mac_address = DeviceKey.mac_address "
)
data class ViewDeviceKey(
    val serialNumber: String,
    val macAddress: String,
    val deviceUserID: Pair<Int, String>,
    val deadTime: Long = 0,
    val keyType: DeviceEnum.KeyType,
    val keyName: String?,
    val keyValue: String?,
    val keyLockId: Int,
    val createAt: Long = 0,
    val updateAt: Long = 0,
    val temporaryPasswordSecretCode: String,
    val secretCode: String?,
    val temporaryPasswordWithoutAsterisk: Boolean,
)