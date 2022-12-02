package com.datangic.api.smartlock

data class DeleteDevUser(
    val devNo: String,
    val productName: String = "LockWiFiTuYaMT1586",
    val productResource: DeviceUserId
)

data class DeviceUserId(val userId: List<Int>) {
    constructor(vararg userId: Int) : this(userId.asList())
}
