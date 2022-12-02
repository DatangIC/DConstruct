package com.datangic.data.database.view

import androidx.room.DatabaseView
import com.datangic.data.database.table.DeviceEnum

@DatabaseView(
    "SELECT " +
            "Device.uid," +
            "Device.syn_network AS synNetwork," +
            "Device.name," +
            "Device.type," +
            "Device.serial_number AS serialNumber," +
            "Device.mac_address AS macAddress," +
            "Device.imei," +
            "Device.nfc," +
            "Device.face," +
            "Device.seized_fingerprint AS seizedFingerprint," +
            "Device.permission_switch AS permissionSwitch," +
            "Device.secret_code AS secretCode," +
            "Device.dirty AS deviceDirty," +
            "DeviceUser.device_user_id AS deviceUserID," +
            "DeviceUser.administrator," +
            "DeviceUser.auth_code AS authCode," +
            "DeviceUser.user_status AS userStatus," +
            "DeviceUser.dirty AS deviceUserDirty," +
            "DeviceStatus.battery," +
            "DeviceStatus.create_at AS createAt," +
            "DeviceStatus.update_at AS updateAt " +
            " FROM Device " +
            " INNER JOIN DeviceUser ON Device.serial_number = DeviceUser.serial_number " +
            " AND Device.mac_address = DeviceUser.mac_address " +
            " AND Device.device_user_id = DeviceUser.device_user_id" +
            " INNER JOIN DeviceStatus ON Device.serial_number = DeviceStatus.serial_number " +
            " AND Device.mac_address = DeviceStatus.mac_address " +
            " AND Device.device_user_id = DeviceStatus.device_user_id"
)
data class ViewManagerDevice(
    val uid: Int,
    val synNetwork: Boolean,
    val name: String,
    val type: DeviceEnum.LockType,
    val serialNumber: String,
    val macAddress: String,
    val imei: String,
    val nfc: Boolean,
    val face: Boolean,
    val seizedFingerprint: Boolean,
    val permissionSwitch: Boolean,
    val secretCode: String?,
    val deviceUserID: Pair<Int, String>,
    val administrator: Boolean,
    val authCode: String,
    val userStatus: DeviceEnum.DeviceUserStatus,
    val battery: Int,
    val createAt: Long,
    val updateAt: Long,
    val deviceDirty: Boolean,
    val deviceUserDirty: Boolean
)
