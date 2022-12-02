package com.datangic.login

import androidx.lifecycle.LiveData
import com.datangic.api.login.*
import com.datangic.libs.base.dataSource.UserSource
import com.datangic.network.ResponseStatus
import io.reactivex.rxjava3.core.Observable

class LoginRepository(val dataSource: UserSource) {
//
//    private val mLoginApiResource = LoginApiResource(api = LoginApi.create())
//    private val TAG = LoginRepository::class.java.simpleName
//
//    fun getVerifyCode(
//        phone: String,
//        type: LoginApi.LoginType = LoginApi.LoginType.RegisterORLogin
//    ): Observable<ResponseStatus<VerifyCodeResult>> = mLoginApiResource.getVerifyCode(phone, type)
//
//    fun getVerifyCode2(
//        phone: String,
//        type: LoginApi.LoginType = LoginApi.LoginType.RegisterORLogin
//    ): LiveData<ResponseStatus<VerifyCodeResult>> = mLoginApiResource.getVerifyCode2Live(phone, type)
//
//
//    // 登陆&注册
//    fun login(
//        email: String = "",
//        username: String = "",
//        password: String = "",
//        code: String = ""
//    ): Observable<ResponseStatus<LoginDataResult>> {
//
//        return mLoginApiResource.loginOrRegister(
//            LoginData(
//                email = email,
//                userPhone = username,
//                userPassword = password,
//                code = code
//            )
//        ).map {
//            return@map it
//        }
//    }
//
//    // updateUser
////    fun updateUser(userData: UserData): Observable<ResponseState<UpdateUser>> {
////        return mLoginApiResource.updateUser(userData).map { result ->
////            result.data?.let { dataSource.updateUser(it) }
////            return@map result
////        }
////    }
//
//    // LoginOut
//    fun loginOut(): Observable<ResponseStatus<String>> {
//        return mLoginApiResource.loginOut().map {
////            dataSource.deleteUser()
//            return@map it
//        }
//    }
//
}