package com.datangic.login

import androidx.lifecycle.LiveData
import com.datangic.api.login.*
import com.datangic.network.ResponseState
import io.reactivex.rxjava3.core.Observable

class LoginRepository(private val dataSource: LoginDataSource) {

    private val mLoginApiResource = LoginApiResource(api = LoginApi.create())

    fun getVerifyCode(
        phone: String,
        type: LoginApi.LoginType = LoginApi.LoginType.RegisterORLogin
    ): Observable<ResponseState<VerifyCodeResult>> = mLoginApiResource.getVerifyCode(phone, type)

    fun getVerifyCode2(
        phone: String,
        type: LoginApi.LoginType = LoginApi.LoginType.RegisterORLogin
    ): LiveData<ResponseState<VerifyCodeResult>> = mLoginApiResource.getVerifyCode2Live(phone, type)


    // 登陆&注册
    fun login(
        email: String = "",
        username: String = "",
        password: String = "",
        code: String = ""
    ): Observable<ResponseState<LoginDataResult>> {

        return mLoginApiResource.loginOrRegister(
            LoginData(
                email = email,
                userPhone = username,
                userPassword = password,
                code = code
            )
        ).map {
            return@map it
        }
    }

    // updateUser
    fun updateUser(userData: UserData): Observable<ResponseState<UserData>> = mLoginApiResource.updateUser(userData)

    // LoginOut
    fun loginOut(): Observable<ResponseState<String>> = mLoginApiResource.loginOut()


}