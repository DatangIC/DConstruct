package com.datangic.api.smartlock

import androidx.lifecycle.LiveData
import com.datangic.api.DataResponse
import com.datangic.api.RouteInterceptor
import com.datangic.network.ApiResponse
import com.datangic.network.NetworkApi
import com.datangic.network.impl.IAuthorization
import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface SmartLockApi {
    companion object {
        private const val LOGIN_BASE_URL = "http://zhwl.dttsh.cn:1130/"
        fun create(getAuth: () -> String) = NetworkApi.create<SmartLockApi>(
            baseUrl = LOGIN_BASE_URL,
            authorizationInfo = object : IAuthorization {
                override fun getAuthorization(): String = getAuth()
            },
            isCookie = true,
            RouteInterceptor()
        )
    }

    data class QDevice(
        val productName: String = "LockWiFiTuYaMT1586"
    )

    @POST("/paas/home/api/dev")
    fun updateDev(
        @Body postNetDevice: PostNetDevice<NetLockSource<NetLockUser>>
    ): Observable<DataResponse<JsonElement>>

    @POST("/paas/home/api/devquery")
    fun getDevs(@Body getDevice: QDevice = QDevice()): Observable<DataResponse<JsonElement>>

    @POST("/pass/home/api/user")
    fun updateUser(
        @Body getDevice: GetDevice
    ): LiveData<ApiResponse<DataResponse<JsonElement>, Any?>>

    @DELETE("/paas/home/api/dev/LockWiFiTuYaMT1586/{devNo}")
    fun deleteDevice(
        @Path("devNo") devNo: String,
    ): Observable<DataResponse<JsonElement>>

    @POST("/paas/home/api/dev/productPartResource/delusers")
    fun deleteUser(
        @Body deleteDevUser: DeleteDevUser
    ): Observable<DataResponse<JsonElement>>

//    fun getDeviceLog(@Body,)

}