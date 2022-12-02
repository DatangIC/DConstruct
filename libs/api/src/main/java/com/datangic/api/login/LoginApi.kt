package com.datangic.api.login

import androidx.lifecycle.LiveData
import com.datangic.api.DataResponse
import com.datangic.network.ApiResponse
import com.datangic.network.NetworkApi
import com.datangic.network.impl.IAuthorization
import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Observable
import okhttp3.Interceptor
import retrofit2.http.*


interface LoginApi {

    companion object {
        private const val LOGIN_BASE_URL = "http://zhwl.dttsh.cn:1130/"
        private const val LOGIN_BASE_URL_T = "http://dttsh.cn:8091/mock/12/"
        fun create(vararg interceptors: Interceptor, getAuth: (() -> String)? = null) = NetworkApi.create<LoginApi>(
            baseUrl = LOGIN_BASE_URL,
            authorizationInfo = object : IAuthorization {
                override fun getAuthorization(): String {
                    return getAuth?.let { it() } ?: "app/mobile"
                }
            },
            isCookie = true,
            interceptors = interceptors
        )
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

    @GET("paas/home/api/userme")
    fun getUserInfo(
        @Header("Authorization") auth: String,
    ): Observable<DataResponse<JsonElement>>

    @POST("paas/home/api/user/login")
    fun loginRegister(
        @Body loginData: LoginData
    ): Observable<DataResponse<JsonElement>>

    @PATCH("paas/home/api/user")
    fun updateUser(
        @Body newUser: UserData
    ): Observable<DataResponse<JsonElement>>

    @GET("/paas/home/api/user/logout")
    fun logout(): Observable<DataResponse<JsonElement>>


    @GET("paas/home/api/code")
    fun getAuthCode2Live(
        @Query("userPhone") phone: String,
        @Query("type") type: String
    ): LiveData<ApiResponse<DataResponse<JsonElement>, Any?>>


    @POST("paas/home/api/user/login")
    fun loginRegister2Live(
        @Body loginData: LoginData
    ): LiveData<ApiResponse<DataResponse<JsonElement>, Any?>>


    @PATCH("paas/home/api/user")
    fun updateUser2Live(
        @Body newUser: UserData
    ): LiveData<ApiResponse<DataResponse<JsonElement>, Any?>>

    @GET("paas/home/api/userme")
    fun getUserInfo2Live(
        @Header("Authorization") auth: String,
    ): LiveData<ApiResponse<DataResponse<JsonElement>, Any?>>

    @GET("/paas/home/api/user/logout")
    fun logout2Live(): LiveData<ApiResponse<DataResponse<JsonElement>, Any?>>
}