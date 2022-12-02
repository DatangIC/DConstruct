package com.datangic.network.chainRequest

import androidx.lifecycle.LiveData
import com.datangic.network.ResponseStatus

class RealInterceptorRequest<R, T>(
    private val index: Int,
    internal val request: LiveData<ResponseStatus<R>>,
    private val interceptors: List<Interceptor<R, T>>
) : Interceptor.Chain<R, T> {

    internal fun copy(
        index: Int,
        request: LiveData<ResponseStatus<R>>
    ) = RealInterceptorRequest(index, request, interceptors)

    override fun request(): LiveData<ResponseStatus<R>> = request

    override fun proceed(request: LiveData<ResponseStatus<R>>): LiveData<ResponseStatus<T>> {
        check(index < interceptors.size)
        val next = copy(index = index + 1, request = request)
        val interceptor = interceptors[index]
        return interceptor.intercept(next)

    }
}