package com.datangic.network.interceptor

import android.app.Application
import android.widget.Toast
import com.datangic.network.NetworkApi
import com.datangic.network.R
import okhttp3.Interceptor
import okhttp3.Response

class ResponseInterceptor(val application: Application) : Interceptor {
    private val TAG = ResponseInterceptor::class.simpleName
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestTime = System.currentTimeMillis()
        val response = chain.proceed(chain.request())
        NetworkApi.logger?.log(TAG, "Request time = ${System.currentTimeMillis() - requestTime}")
        NetworkApi.logger?.log(TAG, "Request code = ${response.code}")
        if (response.code != 200)
            when (response.code) {
                500 -> NetworkApi.runInMainThread {
                    Toast.makeText(application, "500:Internal Server Error", Toast.LENGTH_SHORT).show()
                }
            }
        return response
    }
}