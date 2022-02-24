package com.datangic.network.chainRequest

import androidx.lifecycle.LiveData
import com.datangic.network.Resource

class RealInterceptorRequest<R, T>(
    private val index: Int,
    internal val request: LiveData<Resource<R>>,
    private val interceptors: List<Interceptor<R, T>>
) : Interceptor.Chain<R, T> {

    internal fun copy(
        index: Int,
        request: LiveData<Resource<R>>
    ) = RealInterceptorRequest(index, request, interceptors)

    override fun request(): LiveData<Resource<R>> = request

    override fun proceed(request: LiveData<Resource<R>>): LiveData<Resource<T>> {
        check(index < interceptors.size)
        val next = copy(index = index + 1, request = request)
        val interceptor = interceptors[index]
        return interceptor.intercept(next)

    }
}