package com.datangic.api.smartlock

data class NetLockSource<U>(
    val secretCode: String? = null,
    val imei: String,
    val sn: String,
    val mac: String,
    val createTime: Long,
    val battery: Int,
    val users: List<U>
)
