package com.datangic.data.database.table

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithDevice(
    @Embedded val user: User,
    @Relation(
        parentColumn = "uid",
        entityColumn = "uid"
    )
    val devices: List<Device>
)