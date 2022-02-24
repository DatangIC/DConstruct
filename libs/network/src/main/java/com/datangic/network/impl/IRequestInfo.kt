package com.datangic.network.impl

interface IRequestInfo {
    fun getVersionName(): String
    fun getVersionCode(): String
    fun isDebug(): Boolean
}