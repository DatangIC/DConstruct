package com.datangic.data.database.view

import androidx.room.DatabaseView
import com.datangic.data.database.table.DeviceEnum

@DatabaseView(
    "SELECT " +
            "DeviceStatus.mac_address AS macAddress," +
            "DeviceStatus.serial_number AS serialNumber," +
            "DeviceStatus.opening AS isOpening," +
            "DeviceStatus.language AS language," +
            "DeviceStatus.unlock_period AS unlockPeriod," +
            "DeviceStatus.volume AS volume," +
            "DeviceStatus.wifi_rssi AS wifiRSSI," +
            "DeviceStatus.wifi_status AS wifiStatus," +
            "DeviceStatus.enable_nfc_type AS nfcType," +
            "DeviceStatus.enable_lock_cylinder AS enableLockCylinder," +
            "DeviceStatus.enable_anti_prizing_alarm AS enableAntiPrizingAlarm," +
            "DeviceStatus.enable_combination_lock AS enableCombinationLock," +
            "DeviceStatus.enable_automatic_closing AS enableAutomaticClosing," +
            "DeviceStatus.enable_infrared AS enableInfrared," +
            "DeviceStatus.enable_magic_number AS enableMagicNumber," +
            "DeviceStatus.enable_temporary_password AS enableTemporaryPassword," +
            "DeviceStatus.enable_voice AS enableVoice," +
            "DeviceStatus.enable_doorbell AS enableDoorbell," +
            "DeviceStatus.enable_wifi AS enableWifi," +
            "DeviceStatus.enable_bluetooth_keep_on AS enableBluetoothKeepOn," +
            "DeviceStatus.enable_lock_keep_open AS enableLockKeepOpen," +
            "DeviceStatus.power_saving_start_at AS powerSavingStartAt," +
            "DeviceStatus.power_saving_end_at AS powerSavingEndAt," +
            "Device.status_query AS hasStatusQuery," +
            "Device.nfc AS hasNFC," +
            "Device.face AS hasFace," +
            "Device.wifi AS hasWifi," +
            "Device.secret_code AS secretCode," +
            "Device.back_panel_ota AS backPanelOTA," +
            "Device.infrared AS hasInfrared," +
            "Device.automatic_lock AS hasAutomaticLock," +
            "Device.self_eject_lock AS hasSelfEjectLock," +
            "Device.permission_switch AS hasPermissionSwitch," +
            "Device.magic_number AS hasMagicNumber," +
            "Device.seized_fingerprint AS hasSeizedFingerprint," +
            "Device.volume_adjustment AS hasVolumeAdjustment," +
            "Device.follow_doorbell AS hasFollowDoorbell," +
            "Device.temporary_password AS hasTemporaryPassword," +
            "Device.lock_keep_open AS hasLockKeepOpen," +
            "Device.lock_cylinder AS hasLockCylinder," +
            "Device.language_switch AS hasLanguageSwitch," +
            "Device.variable_password AS hasVariablePassword," +
            "Device.min_password_length AS hasMinPasswordLen," +
            "Device.max_password_length AS hasMaxPasswordLen," +
            "DeviceUser.device_user_id AS deviceUserID," +
            "DeviceUser.administrator AS isAdministrator," +
            "DeviceUser.user_status AS userStatus " +
            " FROM DeviceStatus " +
            " INNER JOIN Device ON DeviceStatus.serial_number = Device.serial_number " +
            " AND DeviceStatus.mac_address = Device.mac_address " +
            " AND DeviceStatus.device_user_id = Device.device_user_id" +
            " INNER JOIN DeviceUser ON DeviceStatus.serial_number = DeviceUser.serial_number " +
            " AND DeviceStatus.mac_address = DeviceUser.mac_address " +
            " AND DeviceStatus.device_user_id = DeviceUser.device_user_id"
)
data class ViewDeviceStatus(
    val deviceUserID: Pair<Int, String>,
    val userStatus: DeviceEnum.DeviceUserStatus,
    val isAdministrator: Boolean,
    val serialNumber: String,
    val macAddress: String,
    val isOpening: Boolean,
    val language: DeviceEnum.LockLanguage,
    val unlockPeriod: Int,
    val volume: Int,
    val nfcType: DeviceEnum.NfcType,
    val wifiRSSI: Int,
    val wifiStatus: DeviceEnum.WifiStatus,
    val enableLockCylinder: Boolean,
    val enableAntiPrizingAlarm: Boolean,
    val enableCombinationLock: Boolean,
    val enableAutomaticClosing: Boolean,
    val enableInfrared: Boolean,
    val enableMagicNumber: Boolean,
    val enableTemporaryPassword: Boolean,
    val enableVoice: Boolean,
    val enableDoorbell: Boolean,
    val enableWifi: Boolean,
    val enableBluetoothKeepOn: Boolean,
    val enableLockKeepOpen: Boolean,
    val powerSavingStartAt: Int,
    val powerSavingEndAt: Int,
    val hasStatusQuery: Boolean,
    val hasNFC: Boolean,
    val hasFace: Boolean,
    val hasWifi: Boolean,
    val secretCode: String?,
    val backPanelOTA: Boolean,
    val hasInfrared: Boolean,
    val hasSeizedFingerprint: Boolean,
    val hasAutomaticLock: Boolean,
    val hasSelfEjectLock: Boolean,
    val hasPermissionSwitch: Boolean,
    val hasMagicNumber: Boolean,
    val hasVolumeAdjustment: Boolean,
    val hasFollowDoorbell: Boolean,
    val hasTemporaryPassword: Boolean,
    val hasLockKeepOpen: Boolean,
    val hasLockCylinder: Boolean,
    val hasLanguageSwitch: Boolean,
    val hasVariablePassword: Boolean,
    val hasMinPasswordLen: Int,
    val hasMaxPasswordLen: Int
)