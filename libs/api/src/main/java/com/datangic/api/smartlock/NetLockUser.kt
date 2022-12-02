package com.datangic.api.smartlock

data class NetLockUser(
    val authCode: String,
    val userName: String,
    val userId: Int,
    val createTime: Long,
    val isAdmin: Int,
    val phoneNumber: String
)
