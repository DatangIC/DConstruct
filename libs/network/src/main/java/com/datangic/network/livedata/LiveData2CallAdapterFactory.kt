package com.datangic.network.livedata

import androidx.lifecycle.LiveData
import com.datangic.network.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveData2CallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) return null
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawType = getRawType(observableType)
        if (rawType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("Resource must be parameterized")
        }
        val bodyType = getParameterUpperBound(0, observableType)
        return LiveData2CallAdapter<Any>(bodyType)
    }

}