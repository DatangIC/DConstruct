package com.datangic.data.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["uid"], unique = true)],
)
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "user_id") var userId: Long = 0,
    @ColumnInfo(name = "role_name") val roleName: String? = null,
    @ColumnInfo(name = "role_id") val roleId: Int = 0,
    @ColumnInfo(name = "phone") var phone: String? = null,
    @ColumnInfo(name = "email") var email: String? = null,
    @ColumnInfo(name = "avatar") var avatar: String? = null,
    @ColumnInfo(name = "nickname") var nickname: String? = null,
    @ColumnInfo(name = "third_id") val thirdId: String? = null,
    @ColumnInfo(name = "home_id") val homeIds: List<Int> = listOf(1)
)

