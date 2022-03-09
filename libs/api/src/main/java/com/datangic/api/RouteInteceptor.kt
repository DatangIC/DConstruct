package com.datangic.api

import okhttp3.Interceptor
import okhttp3.Response

internal class RouteInterceptor(private val isBack: Boolean = true) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null) false
        else
            this::class.java == other::class.java
    }

    override fun hashCode(): Int {
        return isBack.hashCode()
    }
}