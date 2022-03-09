package com.datangic.data.database.table

import androidx.room.*

@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = Device::class,
                    parentColumns = arrayOf("serial_number", "mac_address"),
                    childColumns = arrayOf("serial_number", "mac_address"),
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["serial_number", "mac_address"])])
@TypeConverters(LockTypeConverters::class)
data class DeviceLog(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "serial_number", index = true) val serialNumber: String,
        @ColumnInfo(name = "mac_address", index = true) val macAddress: String,
        @ColumnInfo(name = "device_user_id", index = true) val deviceUserId: Pair<Int, String>,
        @ColumnInfo(name = "log_id") val logId: Int,
        @ColumnInfo(name = "create_at") var createAt: Long = 0,
        @ColumnInfo(name = "update_at") var updateAt: Long = 0,
        @ColumnInfo(name = "log_unlock_type") val logUnlockType: DeviceEnum.UnlockType = DeviceEnum.UnlockType.UNKNOWN,
        @ColumnInfo(name = "log_state") val logState: DeviceEnum.LogState = DeviceEnum.LogState.UNKNOWN,
        @ColumnInfo(name = "log_lock_id") val logLockId: Int = 0,
        @ColumnInfo(name = "log_create_at") val logCreateAt: Long = 0,
)
