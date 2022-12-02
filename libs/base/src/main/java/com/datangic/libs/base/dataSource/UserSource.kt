package com.datangic.libs.base.dataSource

import android.app.Application
import com.datangic.api.GsonUtils
import com.datangic.api.login.*
import com.datangic.common.utils.Logger
import com.datangic.data.DatabaseRepository
import com.datangic.data.LogStatus
import com.datangic.data.LogUser
import com.datangic.data.database.table.User
import com.datangic.network.AppExecutors
import com.datangic.network.RequestStatus
import com.datangic.network.ResponseStatus
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch

class UserSource(val application: Application, val mDatabase: DatabaseRepository, private val appExecutors: AppExecutors = AppExecutors()) {

    var mLoginApi: LoginApi? = null
    private val mGson by lazy { GsonUtils.getGson() }
    private var mGetUserDone = true
    fun getUserInfo() {
        MainScope().launch(appExecutors.singleIO().asCoroutineDispatcher()) {
            mGetUserDone = false
            mDatabase.mDataStore.mUserPrivateInfoFlow.collect { user ->
                if (mGetUserDone) return@collect
                mLoginApi = if (user.authentication.isNotEmpty()) {
                    LoginApi.create() { return@create user.authentication }.also { loginApi ->
                        LoginApiResource(loginApi, appExecutors).getUserInfo(user.authentication).subscribe { res ->
                            when (res.requestStatus) {
                                RequestStatus.LOADING -> mDatabase.updateLogUserStatus(LogStatus.LOGGING)
                                RequestStatus.ERROR -> mDatabase.updateLogUserStatus(LogStatus.NOT_LOGIN)
                                RequestStatus.SUCCESS -> {
                                    res.data?.let { json ->
                                        val loginResult = mGson.fromJson<LoginDataResult>(json, object : TypeToken<LoginDataResult>() {}.type)
                                        loginResult.authorization = user.authentication
                                        updateUser(loginResult, user.password)
                                    }
                                }
                                else -> mDatabase.updateLogUserStatus(LogStatus.NOT_AUTH)
                            }
                        }
                    }
                } else {
                    mDatabase.updateLogUserStatus(LogStatus.NOT_LOGIN)
                    LoginApi.create()
                }
            }
        }
    }

    fun getVerifyCode(
        phone: String, type: LoginApi.LoginType = LoginApi.LoginType.RegisterORLogin, action: (ResponseStatus<VerifyCodeResult>) -> Unit
    ) {
        action(ResponseStatus.loading())
        mLoginApi?.let { api ->
            LoginApiResource(api, appExecutors).getVerifyCode(phone, type).subscribeOn(Schedulers.io()).subscribe { res ->
                when (res.requestStatus) {
                    RequestStatus.ERROR -> action(ResponseStatus.error(res.message, res.resId))
                    RequestStatus.SUCCESS -> {
                        res.data?.let { data ->
                            action(ResponseStatus.success(mGson.fromJson(data, object : TypeToken<VerifyCodeResult>() {}.type)))
                        }
                    }
                    else -> action(ResponseStatus.loading())
                }
            }
        } ?: action(ResponseStatus.error(""))
    }

    fun loginOrRegister(
        email: String = "", username: String = "", password: String = "", code: String = "", action: (ResponseStatus<LoginDataResult>) -> Unit
    ) {
        action(ResponseStatus.loading())
        mLoginApi?.let { api ->
            LoginApiResource(api, appExecutors).loginOrRegister(
                LoginData(
                    email = email, userPhone = username, userPassword = password, code = code
                )
            ).subscribe({ res ->
                when (res.requestStatus) {
                    RequestStatus.LOADING -> {
                        mDatabase.updateLogUserStatus(LogStatus.LOGGING)
                        action(ResponseStatus.loading())
                    }
                    RequestStatus.ERROR -> {
                        mDatabase.updateLogUserStatus(LogStatus.NOT_LOGIN)
                        action(ResponseStatus.error(res.message))
                    }
                    RequestStatus.SUCCESS -> {
                        try {
                            res.data?.let { json ->
                                val loginResult = mGson.fromJson<LoginDataResult>(json, object : TypeToken<LoginDataResult>() {}.type)
                                updateUser(loginResult, password)
                                action(ResponseStatus.success(loginResult))
                            }
                        } catch (e: Exception) {
                            Logger.e("UserSource", "Error=${e}")
                        }
                    }
                    else -> {}
                }
            }) {
                Logger.e("UserSource", "Error=333${it}")
            }
        }
    }


    fun loginOut(action: (ResponseStatus<JsonElement>) -> Unit) {
        action(ResponseStatus.loading())
        mLoginApi?.let { api ->
            LoginApiResource(api, appExecutors).loginOut().subscribeOn(Schedulers.io()).subscribe { res ->
                when (res.requestStatus) {
                    RequestStatus.LOADING -> {
                        action(ResponseStatus.loading())
                    }
                    RequestStatus.ERROR -> {
                        action(ResponseStatus.error(res.message))
                    }
                    RequestStatus.SUCCESS -> {
                        res.data?.let { json ->
                            mDatabase.updateLogUserStatus(LogStatus.NOT_LOGIN)
                            action(ResponseStatus.success(json))
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateUser(loginResult: LoginDataResult, password: String?) {
        val logUser = LogUser(
            status = LogStatus.LOGGED,
            userId = loginResult.userId,
            userPhone = loginResult.userPhone,
            userEmail = loginResult.userEmail,
            roleId = loginResult.roleId,
            roleName = loginResult.roleName,
            thirdPartyPlatformUid = loginResult.thirdPartyPlatformUid,
            authorization = loginResult.authorization,
            userPassword = password,
            nickname = loginResult.nickname,
            avatar = loginResult.avatar
        )
        MainScope().launch(Dispatchers.IO) {
            Logger.e("Usersource", "LogUser=$logUser")
            mDatabase.updateLogUser(logUser)
            try {
                mDatabase.mDataStore.setUserPrivateInfo(
                    logUser.userId, logUser.authorization, logUser.userPassword
                )
            } catch (e: Exception) {
                Logger.e("USER updateUserInfo", "message=${e.message}")
            }
            val user = User(
                userId = logUser.userId,
                roleId = logUser.roleId,
                roleName = logUser.roleName,
                phone = logUser.userPhone,
                email = logUser.userEmail,
                avatar = logUser.avatar,
                nickname = logUser.nickname,
                thirdId = logUser.thirdPartyPlatformUid
            )
            mDatabase.mDatabase.userDao().insertOrUpdate(user)
        }
    }

    private fun updateUser(updateUser: UpdateUser) {
        appExecutors.scopeIo(Dispatchers.IO).launch {
            mDatabase.getLogUser()?.let { logUser ->
                logUser.apply {
                    userPassword = updateUser.userPassword
                    userPhone = updateUser.userPhone
                    nickname = updateUser.nickname
                    avatar = updateUser.avatar
                }
                mDatabase.mDataStore.setUserPrivateInfo(
                    userID = logUser.userId, auth = logUser.authorization, password = logUser.userPassword
                )
                val user = User(
                    avatar = logUser.avatar, nickname = logUser.nickname, phone = logUser.userPhone
                )
                mDatabase.mDatabase.userDao().insertOrUpdate(user)
            }
        }
    }
}