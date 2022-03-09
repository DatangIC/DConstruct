package com.datangic.api.smartlock

data class PostUser(
    val homeId:Int,
    val devSn:String,
    val devUser:NewDevUser
)
