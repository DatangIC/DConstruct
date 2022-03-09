package com.datangic.data.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "username") var username: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "password") val password: String?,
    @ColumnInfo(name = "avatar") val avatar: String?,
    @ColumnInfo(name = "nickname") val nickname: String?,
    @ColumnInfo(name = "third_id") val thirdId: String?,
    @ColumnInfo(name = "home_id") val homeIds: List<Int> = listOf(1)
)

