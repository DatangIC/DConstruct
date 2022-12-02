package com.datangic.data.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["user_id"], unique = true)],
)
data class User(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "user_id") var userId: Int = 0,
    @ColumnInfo(name = "role_name") var roleName: String? = null,
    @ColumnInfo(name = "role_id") var roleId: Int = 0,
    @ColumnInfo(name = "phone") var phone: String? = null,
    @ColumnInfo(name = "email") var email: String? = null,
    @ColumnInfo(name = "avatar") var avatar: String? = null,
    @ColumnInfo(name = "nickname") var nickname: String? = null,
    @ColumnInfo(name = "third_id") var thirdId: String? = null
) {
    @Ignore
    constructor() : this(0)
}