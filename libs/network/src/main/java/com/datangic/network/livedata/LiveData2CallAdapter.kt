package com.datangic.network.livedata

import androidx.lifecycle.LiveData
import com.datangic.network.ApiResponse
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

internal class LiveData2CallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, LiveData<ApiResponse<T, Any?>>> {
    override fun responseType() = responseType

    override fun adapt(call: Call<T>): LiveData<ApiResponse<T, Any?>> {
        return object : LiveData<ApiResponse<T, Any?>>() {
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<T> {
                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            postValue(ApiResponse.create(response))
                        }

                        override fun onFailure(call: Call<T>, t: Throwable) {
                            postValue(ApiResponse.create(t))
                        }
                    })
                }
            }
        }
    }
}