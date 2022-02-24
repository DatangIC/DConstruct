package com.datangic.network.impl

import com.datangic.network.BuildConfig

class DefaultRequestInfo : IRequestInfo {
    override fun getVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun getVersionCode(): String {
        return BuildConfig.VERSION_CODE.toString()
    }

    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}