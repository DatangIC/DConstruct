package com.datangic.network.chainRequest

import androidx.lifecycle.LiveData
import com.datangic.network.ResponseState

class RealInterceptorRequest<R, T>(
    private val index: Int,
    internal val request: LiveData<ResponseState<R>>,
    private val interceptors: List<Interceptor<R, T>>
) : Interceptor.Chain<R, T> {

    internal fun copy(
        index: Int,
        request: LiveData<ResponseState<R>>
    ) = RealInterceptorRequest(index, request, interceptors)

    override fun request(): LiveData<ResponseState<R>> = request

    override fun proceed(request: LiveData<ResponseState<R>>): LiveData<ResponseState<T>> {
        check(index < interceptors.size)
        val next = copy(index = index + 1, request = request)
        val interceptor = interceptors[index]
        return interceptor.intercept(next)

    }
}