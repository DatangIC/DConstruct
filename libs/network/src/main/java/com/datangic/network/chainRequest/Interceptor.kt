package com.datangic.network.chainRequest


import androidx.lifecycle.LiveData
import com.datangic.network.ResponseStatus
import java.io.IOException

interface Interceptor<R, T> {
    @Throws(IOException::class)
    fun intercept(chain: Interceptor.Chain<R, T>): LiveData<ResponseStatus<T>>

    interface Chain<R, T> {
        fun request(): LiveData<ResponseStatus<R>>

        @Throws(IOException::class)
        fun proceed(request: LiveData<ResponseStatus<R>>): LiveData<ResponseStatus<T>>

    }
}