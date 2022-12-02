package com.datangic.api.smartlock

data class PostNetDevice<T>(
    val devName: String,
    val devNo: String,
    val productName: String = "LockWiFiTuYaMT1586",
    val productResource: T
)
