package com.datangic.api

import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.datangic.common.RouterList
import com.datangic.network.UNAUTHORIZED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

internal class RouteInterceptor(private val isBack: Boolean = true) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        MainScope().launch(Dispatchers.Main) {
            if (response.code == UNAUTHORIZED) {
                ARouter.getInstance()
                    .build(RouterList.LOGIN_ACTIVITY)
                    .withFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    .withObject("isBack", isBack)
                    .navigation()
            }
        }
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