package com.datangic.data.database

import androidx.room.*
import com.datangic.data.database.table.Device

@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = Device::class,
                    parentColumns = arrayOf("serial_number", "mac_address"),
                    childColumns = arrayOf("serial_number", "mac_address"),
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["serial_number", "mac_address"])])
data class TemporaryPassword(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "serial_number", index = true) val serialNumber: String,
        @ColumnInfo(name = "mac_address", index = true) val macAddress: String,
        @ColumnInfo(name = "password") val password: String,
        @ColumnInfo(name = "random_number") val randomNumber: Int,
        @ColumnInfo(name = "create_at") val createAt: Int,
        @ColumnInfo(name = "deadline") val deadline: Int,
)
