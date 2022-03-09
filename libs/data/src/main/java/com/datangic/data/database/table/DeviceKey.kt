package com.datangic.data.database.table

import androidx.room.*
import com.datangic.data.database.table.Device
import com.datangic.data.database.table.DeviceEnum

@Entity(foreignKeys = [
    ForeignKey(
            entity = Device::class,
            parentColumns = arrayOf("serial_number", "mac_address"),
            childColumns = arrayOf("serial_number", "mac_address"),
            onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["serial_number", "mac_address"])])
data class DeviceKey(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "serial_number") val serialNumber: String,
        @ColumnInfo(name = "mac_address") val macAddress: String,
        @ColumnInfo(name = "device_user_id") val deviceUserId: Pair<Int, String>,
        @ColumnInfo(name = "create_at") var createAt: Long = 0,
        @ColumnInfo(name = "update_at") var updateAt: Long = 0,
        @ColumnInfo(name = "dead_time") var deadTime: Long = 0,
        @ColumnInfo(name = "key_type") var keyType: DeviceEnum.KeyType = DeviceEnum.KeyType.UNKNOWN,
        @ColumnInfo(name = "key_name") var keyName: String? = null,
        @ColumnInfo(name = "key_value") var keyValue: String? = null,
        @ColumnInfo(name = "key_lock_id") var keyLockId: Int = 0,
)
