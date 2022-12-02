package com.datangic.data.database.table

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithDevice(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "uid"
    )
    val devices: List<Device>
)