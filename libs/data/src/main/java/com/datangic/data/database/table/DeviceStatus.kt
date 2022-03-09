package com.datangic.data.database.table

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Device::class,
            parentColumns = arrayOf("serial_number", "mac_address"),
            childColumns = arrayOf("serial_number", "mac_address"),
            onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
        )],
    indices = [Index(value = ["serial_number", "mac_address"], unique = true)]
)
@TypeConverters(LockTypeConverters::class)
data class DeviceStatus(
    @PrimaryKey(autoGenerate = true) val sid: Int = 0,
    @ColumnInfo(name = "device_user_id") val deviceUserID: Pair<Int, String>,
    @ColumnInfo(name = "serial_number", index = true) val serialNumber: String,
    @ColumnInfo(name = "mac_address", index = true) val macAddress: String,
    @ColumnInfo(name = "create_at") val createAt: Long = 0,
    @ColumnInfo(name = "update_at") var updateAt: Long = 0,
    @ColumnInfo(name = "battery") var battery: Int = 0,
    @ColumnInfo(name = "unlock_period") var unlockPeriod: Int = 0,
    @ColumnInfo(name = "volume") var volume: Int = 1,
    @ColumnInfo(name = "opening") var opening: Boolean = false,
    @ColumnInfo(name = "language") var language: DeviceEnum.LockLanguage = DeviceEnum.LockLanguage.CHINESE_ONLY,
    @ColumnInfo(name = "enable_nfc_type") var enableNfcType: DeviceEnum.NfcType = DeviceEnum.NfcType.NORMAL,
    @ColumnInfo(name = "enable_lock_cylinder") var enableLockCylinder: Boolean = false,
    @ColumnInfo(name = "enable_anti_prizing_alarm") var enableAntiPrizingAlarm: Boolean = false,
    @ColumnInfo(name = "enable_combination_lock") var enableCombinationLock: Boolean = false,
    @ColumnInfo(name = "enable_automatic_closing") var enableAutomaticClosing: Boolean = false,
    @ColumnInfo(name = "enable_infrared") var enableInfrared: Boolean = false,
    @ColumnInfo(name = "enable_magic_number") var enableMagicNumber: Boolean = false,
    @ColumnInfo(name = "enable_temporary_password") var enableTemporaryPassword: Boolean = false,
    @ColumnInfo(name = "enable_voice") var enableVoice: Boolean = false,
    @ColumnInfo(name = "enable_doorbell") var enableDoorbell: Boolean = false,
    @ColumnInfo(name = "enable_wifi") var enableWifi: Boolean = false,
    @ColumnInfo(name = "enable_bluetooth_keep_on") var enableBluetoothKeepOn: Boolean = false,
    @ColumnInfo(name = "enable_lock_keep_open") var enableLockKeepOpen: Boolean = false,
    @ColumnInfo(name = "power_saving_start_at") var powerSavingStartAt: Int = 0,
    @ColumnInfo(name = "power_saving_end_at") var powerSavingEndAt: Int = 0,
    @ColumnInfo(name = "wifi_rssi") var wifiRssi: Int = 0,
    @ColumnInfo(name = "wifi_status") var wifiStatus: DeviceEnum.WifiStatus? = DeviceEnum.WifiStatus.NOT_SETTING,
)