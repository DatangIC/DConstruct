package com.datangic.data.database.table

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.datangic.data.database.table.Device
import com.datangic.data.database.table.DeviceEnum

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Device::class,
            parentColumns = arrayOf("serial_number", "mac_address"),
            childColumns = arrayOf("serial_number", "mac_address"),
            onUpdate = CASCADE, onDelete = CASCADE
        )],
    indices = [Index(value = ["serial_number", "mac_address"])]
)
data class DeviceUser(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "parent_user_id") var parentUserId: Pair<Int, String> = Pair(0, ""),
    @ColumnInfo(name = "serial_number", index = true) val serialNumber: String,
    @ColumnInfo(name = "mac_address", index = true) val macAddress: String,
    @ColumnInfo(name = "device_user_id") val deviceUserId: Pair<Int, String>,
    @ColumnInfo(name = "all_user_id") var allUserId: MutableMap<Int, Int> = HashMap(),
    @ColumnInfo(name = "active_at") var activeAt: Int = 0,
    @ColumnInfo(name = "create_at") val createAt: Long = System.currentTimeMillis() / 1000,
    @ColumnInfo(name = "update_at") var updateAt: Long = System.currentTimeMillis() / 1000,
    @ColumnInfo(name = "device_username") var deviceUsername: String,
    @ColumnInfo(name = "administrator") val administrator: Boolean = false,
    @ColumnInfo(name = "user_status") var userStatus: DeviceEnum.DeviceUserStatus = DeviceEnum.DeviceUserStatus.UNKNOWN,
    @ColumnInfo(name = "auth_code") var authCode: String? = null,
    @ColumnInfo(name = "lifecycle_start") var lifecycleStart: Int = 0,
    @ColumnInfo(name = "lifecycle_end") var lifecycleEnd: Int = 0,
    @ColumnInfo(name = "enable_period_start") var enablePeriodStart: List<Int> = listOf(0, 0, 0),
    @ColumnInfo(name = "enable_period_end") var enablePeriodEnd: List<Int> = listOf(0, 0, 0),
    @ColumnInfo(name = "share_code_picture_path") val shareCodePicturePath: String? = null,
    @ColumnInfo(name = "dirty") var dirty: Boolean = true
)