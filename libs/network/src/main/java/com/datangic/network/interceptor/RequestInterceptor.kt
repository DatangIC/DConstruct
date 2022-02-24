package com.datangic.network.interceptor

import com.datangic.network.NetworkApi.logger
import com.datangic.network.impl.IAuthorization
import com.datangic.network.impl.IRequestInfo
import com.datangic.network.utils.UtilsFormat
import okhttp3.Interceptor
import okhttp3.Response

open class RequestInterceptor(
    private val requestInfo: IRequestInfo? = null,
    private val authorization: IAuthorization? = null
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("os", "Android${android.os.Build.VERSION.RELEASE}")
        builder.addHeader("Date", UtilsFormat.getGMTDate())
        builder.addHeader("Content-Type", "application/json")
        requestInfo?.let {
            builder.addHeader("appVersion", it.getVersionCode())
        }
        authorization?.let {
            builder.addHeader("Authorization", it.getAuthorization())
            logger?.log("RequestInterceptor", "auth=${it.getAuthorization()}")
        }
        return chain.proceed(builder.build())
    }
}