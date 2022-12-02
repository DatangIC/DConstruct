package com.datangic.api.login

import androidx.lifecycle.LiveData
import com.datangic.api.*
import com.datangic.network.AppExecutors
import com.datangic.network.ResponseStatus
import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginApiResource(
    private val api: LoginApi,
    private val appExecutors: AppExecutors = AppExecutors()
) {

    fun getVerifyCode(phone: String, type: LoginApi.LoginType): LockApiObservable<JsonElement> =
        LockApiObservable(api.getAuthCode(phone, type.value))
//        .doOnError { throw it }.map { response2observable(it) }

    fun loginOrRegister(loginData: LoginData): LockApiObservable<JsonElement> = LockApiObservable(api.loginRegister(loginData))

    fun updateUser(userData: UserData): Observable<ResponseStatus<UpdateUser>> =
        api.updateUser(userData).doOnError { throw it }.map { response2observable(it) }

    fun loginOut(): LockApiObservable<JsonElement> = LockApiObservable(api.logout())

    fun getUserInfo(auth: String): LockApiObservable<JsonElement> = LockApiObservable(api.getUserInfo(auth))

    fun getVerifyCode2Live(phone: String, type: LoginApi.LoginType): LiveData<ResponseStatus<VerifyCodeResult>> =
        response2livedata({ api.getAuthCode2Live(phone, type.value) }, appExecutors)

    fun loginOrRegister2Live(loginData: LoginData): LiveData<ResponseStatus<LoginDataResult>> =
        response2livedata({ api.loginRegister2Live(loginData) }, appExecutors)

    fun updateUser2Live(userData: UserData): LiveData<ResponseStatus<UserData>> =
        response2livedata({ api.updateUser2Live(userData) }, appExecutors)

    fun loginOut2Live(): LiveData<ResponseStatus<String>> = response2livedata({ api.logout2Live() }, appExecutors)

    fun getUserInfo2Live(auth: String): LiveData<ResponseStatus<LoginDataResult>> = response2livedata({ api.getUserInfo2Live(auth) }, appExecutors)

}