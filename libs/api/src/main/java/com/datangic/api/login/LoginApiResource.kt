package com.datangic.api.login

import androidx.lifecycle.LiveData
import com.datangic.api.*
import com.datangic.network.ApiResponse
import com.datangic.network.ApiSuccessResponse
import com.datangic.network.AppExecutors
import com.datangic.network.ResponseState
import com.datangic.network.chainRequest.NetworkResource
import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Observable

class LoginApiResource(
    private val api: LoginApi,
    private val appExecutors: AppExecutors = AppExecutors()
) {

    fun getVerifyCode(phone: String, type: LoginApi.LoginType): Observable<ResponseState<VerifyCodeResult>> =
        api.getAuthCode(phone, type.value).doOnError { throw it }.map { response2observable(it) }

    fun loginOrRegister(loginData: LoginData): Observable<ResponseState<LoginDataResult>> =
        api.loginRegister(loginData).doOnError { throw it }.map { response2observable(it) }

    fun updateUser(userData: UserData): Observable<ResponseState<UpdateUser>> =
        api.updateUser(userData).doOnError { throw it }.map { response2observable(it) }

    fun loginOut(): Observable<ResponseState<String>> = api.logout().doOnError { throw it }.map { response2observable(it) }

    fun getVerifyCode2Live(phone: String, type: LoginApi.LoginType): LiveData<ResponseState<VerifyCodeResult>> =
        response2livedata({ api.getAuthCode2Live(phone, type.value) }, appExecutors)

    fun loginOrRegister2Live(loginData: LoginData): LiveData<ResponseState<LoginDataResult>> =
        response2livedata({ api.loginRegister2Live(loginData) }, appExecutors)

    fun updateUser2Live(userData: UserData): LiveData<ResponseState<UserData>> = response2livedata({ api.updateUser2Live(userData) }, appExecutors)

    fun loginOut2Live(): LiveData<ResponseState<String>> = response2livedata({ api.logout2Live() }, appExecutors)

}