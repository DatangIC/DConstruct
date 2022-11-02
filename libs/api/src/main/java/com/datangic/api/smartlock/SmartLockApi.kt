package com.datangic.api.smartlock

import androidx.lifecycle.LiveData
import com.datangic.api.DataResponse
import com.datangic.api.RouteInterceptor
import com.datangic.api.login.LoginApi
import com.datangic.network.ApiResponse
import com.datangic.network.NetworkApi
import com.datangic.network.impl.IAuthorization
import com.google.gson.JsonElement
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface SmartLockApi {
    companion object {
        private const val LOGIN_BASE_URL = "http://zhwl.dttsh.cn:1130/"
        fun create(getAuth: () -> String) = NetworkApi.create<LoginApi>(
            baseUrl = LOGIN_BASE_URL,
            authorizationInfo = object : IAuthorization {
                override fun getAuthorization(): String {
                    return getAuth()
                }
            },
            isCookie = true,
            RouteInterceptor()
        )
    }

    @POST("/pass/home/api/devs")
    fun updateDev(
        @Body postDevice: PostDevice
    ): LiveData<ApiResponse<DataResponse<JsonElement>>>

    @GET("/pass/home/api/devs")
    fun getDevs(
        @Body getDevice: GetDevice
    ): LiveData<ApiResponse<DataResponse<JsonElement>>>

    @POST("/pass/home/api/user")
    fun updateUser(
        @Body getDevice: GetDevice
    ): LiveData<ApiResponse<DataResponse<JsonElement>>>

    @DELETE("/pass/home/api/devs")
    fun deleteDevice(
        @Body deleteDevice: DeleteDevice
    ): LiveData<ApiResponse<DataResponse<JsonElement>>>

    @DELETE("/pass/home/api/user")
    fun deleteUser(
        @Body deleteDevUser: DeleteDevUser
    ): LiveData<ApiResponse<DataResponse<JsonElement>>>

}