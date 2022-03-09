package com.datangic.api.smartlock

import androidx.lifecycle.LiveData
import com.datangic.network.ApiResponse
import com.datangic.network.NetworkApi
import com.google.gson.JsonElement
import retrofit2.http.Body
import retrofit2.http.POST

sealed interface SmartLockOta {
    companion object {
        private const val BASE_URL = "https://api.dttsh.cn/"
        fun create() = NetworkApi.create<SmartLockOta>(baseUrl = BASE_URL)
    }

    @POST("/api/v1.0/firmware/update")
    fun updateFirmware(
        @Body postFirmware: UpgradeRequest.UpdateRequestData
    ): LiveData<ApiResponse<JsonElement>>
}