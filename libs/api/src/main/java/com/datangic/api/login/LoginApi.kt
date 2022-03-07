package com.datangic.api.login

import androidx.lifecycle.LiveData
import com.datangic.api.DataResponse
import com.datangic.network.ApiResponse
import com.datangic.network.NetworkApi
import com.datangic.network.impl.IAuthorization
import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*


interface LoginApi {

    companion object {
        private const val LOGIN_BASE_URL = "https://zhwl.dttsh.cn/"
        private const val LOGIN_BASE_URL_T = "http://dttsh.cn:8091/mock/12/"
        fun create(getAuth: (() -> String)? = null) = NetworkApi.create<LoginApi>(
            baseUrl = LOGIN_BASE_URL,
            authorizationInfo = object : IAuthorization {
                override fun getAuthorization(): String {
                    return getAuth?.let { it() } ?: "app/mobile"
                }
            })
    }

    enum class LoginType(val value: String) {
        RegisterORLogin("register&login"),
        findpassword("findpassword"),
        replacephone("replacephone")
    }

    @GET("paas/home/api/code")
    fun getAuthCode(
        @Query("userPhone") phone: String,
        @Query("type") type: String
    ): Observable<DataResponse<JsonElement>>

    @POST("paas/home/api/login")
    fun loginRegister(
        @Body loginData: LoginData
    ): Observable<DataResponse<JsonElement>>

    @PATCH("paas/home/api/user")
    fun updateUser(
        @Body newUser: UserData
    ): Observable<DataResponse<JsonElement>>

    @GET("paas/home/api/logout")
    fun logout(): Observable<DataResponse<JsonElement>>


    @GET("paas/home/api/code")
    fun getAuthCode2Live(
        @Query("userPhone") phone: String,
        @Query("type") type: String
    ): LiveData<ApiResponse<DataResponse<JsonElement>>>


    @POST("paas/home/api/login")
    fun loginRegister2Live(
        @Body loginData: LoginData
    ): LiveData<ApiResponse<DataResponse<JsonElement>>>


    @PATCH("paas/home/api/user")
    fun updateUser2Live(
        @Body newUser: UserData
    ): LiveData<ApiResponse<DataResponse<JsonElement>>>

    @GET("paas/home/api/logout")
    fun logout2Live(): LiveData<ApiResponse<DataResponse<JsonElement>>>
}