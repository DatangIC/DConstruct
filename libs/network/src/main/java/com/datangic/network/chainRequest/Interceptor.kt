package com.datangic.network.chainRequest


import androidx.lifecycle.LiveData
import com.datangic.network.ResponseState
import java.io.IOException

interface Interceptor<R, T> {
    @Throws(IOException::class)
    fun intercept(chain: Interceptor.Chain<R, T>): LiveData<ResponseState<T>>

    interface Chain<R, T> {
        fun request(): LiveData<ResponseState<R>>

        @Throws(IOException::class)
        fun proceed(request: LiveData<ResponseState<R>>): LiveData<ResponseState<T>>

    }
}