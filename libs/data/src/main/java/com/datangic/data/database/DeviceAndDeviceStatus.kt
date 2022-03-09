package com.datangic.data.database

import androidx.room.Embedded
import androidx.room.Relation
import com.datangic.data.database.table.Device
import com.datangic.data.database.table.DeviceStatus

data class DeviceAndDeviceStatus(
    @Embedded val device: Device,
    @Relation(
                parentColumn = "device_user_id",
                entityColumn = "device_user_id"
        )
        val deviceStatus: DeviceStatus
)
