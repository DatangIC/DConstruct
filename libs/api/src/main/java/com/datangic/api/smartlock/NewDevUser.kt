package com.datangic.api.smartlock

data class NewDevUser(
    val userId: Int,
    val username: String,
    val authCode: String,
    val createTime: Long = 0,
    val updateTime: Long = 0
)
