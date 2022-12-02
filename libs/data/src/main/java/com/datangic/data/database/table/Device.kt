package com.datangic.data.database.table

import androidx.room.*
import java.util.*

@Entity(
    indices = [Index(value = ["serial_number", "mac_address"], unique = true)]
)
data class Device(
    @PrimaryKey(autoGenerate = true) val did: Int = 0,
    @ColumnInfo(name = "uid") val uid: Int,
    @ColumnInfo(name = "syn_network") val synNetwork: Boolean = false,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "type") var type: DeviceEnum.LockType = DeviceEnum.LockType.NORMAL,
    @ColumnInfo(name = "serial_number") val serialNumber: String,
    @ColumnInfo(name = "mac_address") val macAddress: String,
    @ColumnInfo(name = "device_user_id") val deviceUserID: Pair<Int, String>,
    @ColumnInfo(name = "create_at") val createAt: Long = 0,
    @ColumnInfo(name = "update_at") var updateAt: Long = 0,
    @ColumnInfo(name = "imei") val imei: String,
    @ColumnInfo(name = "secret_code") var secretCode: String?,
    @ColumnInfo(name = "nfc") var nfc: Boolean = false,
    @ColumnInfo(name = "face") var face: Boolean = false,
    @ColumnInfo(name = "wifi") var wifi: Boolean = false,
    @ColumnInfo(name = "infrared") var infrared: Boolean = false,
    @ColumnInfo(name = "variable_password") var variablePassword: Boolean = false,
    @ColumnInfo(name = "automatic_lock") var automaticLock: Boolean = false,
    @ColumnInfo(name = "self_eject_lock") var selfEjectLock: Boolean = false,
    @ColumnInfo(name = "permission_switch") var permissionSwitch: Boolean = false,
    @ColumnInfo(name = "magic_number") var magicNumber: Boolean = false,
    @ColumnInfo(name = "temporary_password") var temporaryPassword: Boolean = false,
    @ColumnInfo(name = "status_query") var statusQuery: Boolean = false,
    @ColumnInfo(name = "seized_fingerprint") var seizedFingerprint: Boolean = false,
    @ColumnInfo(name = "volume_adjustment") var volumeAdjustment: Boolean = false,
    @ColumnInfo(name = "follow_doorbell") var followDoorbell: Boolean = false,
    @ColumnInfo(name = "language_switch") var languageSwitch: Boolean = false,
    @ColumnInfo(name = "back_panel_ota") var backPanelOta: Boolean = false,
    @ColumnInfo(name = "temp_password_without_asterisk") var temporaryPasswordWithoutAsterisk: Boolean = false,
    @ColumnInfo(name = "lock_keep_open") var lockKeepOpen: Boolean = false,
    @ColumnInfo(name = "lock_cylinder") var lockCylinder: Boolean = false,
    @ColumnInfo(name = "software_version") var softwareVersion: String? = null,
    @ColumnInfo(name = "hardware_version") var hardwareVersion: String? = null,
    @ColumnInfo(name = "wifi_software_version") var wifiSoftwareVersion: String? = null,
    @ColumnInfo(name = "back_panel_software_version") var backPanelSoftwareVersion: String? = null,
    @ColumnInfo(name = "fingerprint_software_version") var fingerprintSoftwareVersion: String? = null,
    @ColumnInfo(name = "face_software_version") var faceSoftwareVersion: MutableMap<DeviceEnum.FaceVersion, String> = EnumMap(DeviceEnum.FaceVersion::class.java),
    @ColumnInfo(name = "temporary_password_secret_code") var temporaryPasswordSecretCode: String? = null,
    @ColumnInfo(name = "min_password_length") val minPasswordLength: Int = 6,
    @ColumnInfo(name = "max_password_length") val maxPasswordLength: Int = 6,
    @ColumnInfo(name = "dirty") var dirty: Boolean = true
)
