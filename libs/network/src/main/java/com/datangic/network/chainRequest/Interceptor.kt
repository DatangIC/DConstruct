package com.datangic.network.chainRequest


import androidx.lifecycle.LiveData
import com.datangic.network.Resource
import java.io.IOException

interface Interceptor<R, T> {
    @Throws(IOException::class)
    fun intercept(chain: Interceptor.Chain<R, T>): LiveData<Resource<T>>

    interface Chain<R, T> {
        fun request(): LiveData<Resource<R>>

        @Throws(IOException::class)
        fun proceed(request: LiveData<Resource<R>>): LiveData<Resource<T>>

    }
}