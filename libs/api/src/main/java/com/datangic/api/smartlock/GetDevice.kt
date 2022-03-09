package com.datangic.api.smartlock

data class GetDevice(
    val homeId: Int,
    val devMac: String,
    val devSn: String,
    val page: Int,
)
