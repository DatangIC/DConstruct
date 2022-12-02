package com.datangic.api.reqresIn

import androidx.lifecycle.LiveData
import com.datangic.network.ApiResponse
import com.datangic.network.NetworkApi
import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Path

interface ReqresIn {
    companion object {
        private const val REQRE_BASE_URL = "https://reqres.in/"
        fun create() = NetworkApi.create<ReqresIn>(
            baseUrl = REQRE_BASE_URL
        )
    }

    @GET("/api/user/{id}")
    fun getSingerUser(
        @Path("id") id: Int = 1
    ): LiveData<ApiResponse<JsonElement, Any?>>
}