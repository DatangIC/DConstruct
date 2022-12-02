package com.datangic.network.chainRequest

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.datangic.network.*

abstract class NetworkResource<ResponseType, ResultType>
@MainThread constructor(private val appExecutors: AppExecutors) {
    private val result = MediatorLiveData<ResponseStatus<ResultType>>()
    private var index = 0

    init {
        result.postValue(ResponseStatus.loading(null))
        setValue(ResponseStatus.loading(null))
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: ResponseStatus<ResultType>) {
        if (result.value != newValue) {
            result.postValue(newValue)
        }
    }

    private fun fetchFromNetwork() {
        val apiResponse = createCall(index)
        index++
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            when (response) {
                is ApiSuccessResponse -> {
                    appExecutors.singleIO().execute {
                        val data = processResponse(response)
                        appExecutors.fixedIO().execute {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            if (hasNextCall(index)) {
                                fetchFromNetwork()
                                setValue(ResponseStatus.loading(data.data))
                            } else {
                                setValue(data)
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    setValue(ResponseStatus.empty("Empty", null))
                }
                is ApiErrorResponse -> {
                    setValue(ResponseStatus.error(response.errorMessage, null))
                }
            }
        }
    }

    fun asLiveData() = result as LiveData<ResponseStatus<ResultType>>

    /**
     * 根据成功返回的数据，自定义ResultType输出，
     */
    @WorkerThread
    protected abstract fun processResponse(response: ApiSuccessResponse<ResponseType>): ResponseStatus<ResultType>

    /**
     * 检测是否继续请求，默认只请求一次
     */
    @WorkerThread
    protected open fun hasNextCall(index: Int): Boolean = false

    @WorkerThread
    protected abstract fun createCall(index: Int): LiveData<ApiResponse<ResponseType, Any?>>
}