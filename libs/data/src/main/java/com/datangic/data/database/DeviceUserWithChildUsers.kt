package com.datangic.data.database

import androidx.room.Embedded
import androidx.room.Relation
import com.datangic.data.database.table.DeviceUser

data class DeviceUserWithChildUsers(
    @Embedded val user: DeviceUser,
    @Relation(
                parentColumn = "device_user_id",
                entityColumn = "parent_user_id",
        )
        val childUsers: List<DeviceUser>
)
