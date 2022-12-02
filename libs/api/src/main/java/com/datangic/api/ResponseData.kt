package com.datangic.api

import androidx.lifecycle.LiveData
import com.datangic.network.ApiResponse
import com.datangic.network.ApiSuccessResponse
import com.datangic.network.AppExecutors
import com.datangic.network.ResponseStatus
import com.datangic.network.chainRequest.NetworkResource
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

data class DataResponse<T>(
    val content: T? = null,
    val msg: String = "",
    val code: String = ""
)


val mGson by lazy { GsonUtils.getGson() }
inline fun <reified R> response2observable(res: DataResponse<JsonElement>): ResponseStatus<R> {
    return if (res.code == ResponseCode.success) {
        ResponseStatus.success(mGson.fromJson(res.content, object : TypeToken<R>() {}.type))
    } else {
        ResponseStatus.error(res.msg)
    }
}

inline fun <reified R> response2observable(res: JsonElement): ResponseStatus<R> {
    val _code = res.asJsonObject.get("code").asString
    val msg = res.asJsonObject.get("msg").asString

    return if (_code == ResponseCode.success) {
        val content = res.asJsonObject.get("content").asJsonObject
        ResponseStatus.success(mGson.fromJson(content, object : TypeToken<R>() {}.type))
    } else {
        ResponseStatus.error(msg)
    }
}

inline fun <reified R> response2livedata(
    crossinline call: () -> LiveData<ApiResponse<DataResponse<JsonElement>, Any?>>,
    appExecutors: AppExecutors
): LiveData<ResponseStatus<R>> {
    return object : NetworkResource<DataResponse<JsonElement>, R>(appExecutors) {
        override fun processResponse(response: ApiSuccessResponse<DataResponse<JsonElement>>): ResponseStatus<R> {
            return if (response.body.code == ResponseCode.success) {
                ResponseStatus.success(mGson.fromJson(response.body.content, R::class.java))
            } else {
                ResponseStatus.error(response.body.msg)
            }
        }

        override fun createCall(index: Int): LiveData<ApiResponse<DataResponse<JsonElement>, Any?>> = call()

    }.asLiveData()
}
