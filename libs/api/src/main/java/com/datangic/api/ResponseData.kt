package com.datangic.api

import androidx.lifecycle.LiveData
import com.datangic.network.ApiResponse
import com.datangic.network.ApiSuccessResponse
import com.datangic.network.AppExecutors
import com.datangic.network.ResponseState
import com.datangic.network.chainRequest.NetworkResource
import com.google.gson.JsonElement

data class DataResponse<T>(
    val content: T? = null,
    val msg: String = "",
    val code: String = ""
)

object ResponseCode {
    const val success = "0"
    const val noOwner = "12202"
    const val notExist = "12311"
    const val hasSheared = "12337"
}

val mGson by lazy { GsonUtils.getGson() }
inline fun <reified R> response2observable(res: DataResponse<JsonElement>): ResponseState<R> {
    return if (res.code == ResponseCode.success) {
        ResponseState.success(mGson.fromJson(res.content, R::class.java))
    } else {
        ResponseState.error(res.msg)
    }
}

inline fun <reified R> response2livedata(
    crossinline call: () -> LiveData<ApiResponse<DataResponse<JsonElement>>>,
    appExecutors: AppExecutors
): LiveData<ResponseState<R>> {
    return object : NetworkResource<DataResponse<JsonElement>, R>(appExecutors) {
        override fun processResponse(response: ApiSuccessResponse<DataResponse<JsonElement>>): ResponseState<R> {
            return if (response.body.code == ResponseCode.success) {
                ResponseState.success(mGson.fromJson(response.body.content, R::class.java))
            } else {
                ResponseState.error(response.body.msg)
            }
        }

        override fun createCall(index: Int): LiveData<ApiResponse<DataResponse<JsonElement>>> = call()

    }.asLiveData()
}
