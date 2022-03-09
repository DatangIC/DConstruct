package com.datangic.api.smartlock

import androidx.lifecycle.LiveData
import com.datangic.api.GsonUtils
import com.datangic.network.ApiResponse
import com.datangic.network.ApiSuccessResponse
import com.datangic.network.AppExecutors
import com.datangic.network.ResponseState
import com.datangic.network.chainRequest.NetworkResource
import com.google.gson.JsonElement

class SmartLockOtaRepository(
    private val api: SmartLockOta,
    private val appExecutors: AppExecutors = AppExecutors()
) {
    val mGson = GsonUtils.getGson()
    fun updateFirmware(postFirmware: UpgradeRequest.UpdateRequestData): LiveData<ResponseState<UpgradeRequest.Response>> {
        return object : NetworkResource<JsonElement, UpgradeRequest.Response>(appExecutors) {
            override fun processResponse(response: ApiSuccessResponse<JsonElement>): ResponseState<UpgradeRequest.Response> {
                return if (response.body.asJsonObject.get("respCode").asString == "successful") {
                    val mResponse = mGson.fromJson(response.body, UpgradeRequest.Response::class.java)
                    ResponseState.success(mResponse)
                } else {
                    ResponseState.error(response.body.asJsonObject.get("respCode").asString, UpgradeRequest.Response())
                }
            }

            override fun createCall(index: Int): LiveData<ApiResponse<JsonElement>> = api.updateFirmware(postFirmware)

        }.asLiveData()
    }
}