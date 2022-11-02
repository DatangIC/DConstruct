package com.datangic.libs.base.dataSource

import android.app.Application
import androidx.compose.runtime.collectAsState
import com.blankj.utilcode.util.LogUtils
import com.datangic.api.login.LoginDataResult
import com.datangic.api.login.UpdateUser
import com.datangic.common.utils.Logger
import com.datangic.data.DatabaseRepository
import com.datangic.data.UserPrivateInfo
import com.datangic.data.database.table.User
import com.datangic.data.datastore.UserPrivateInfoDataStore
import com.datangic.network.AppExecutors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flatMapConcat

class UserSource(val application: Application, val mDatabase: DatabaseRepository, private val appExecutors: AppExecutors = AppExecutors()) {
    private val logUser: LogUser = LogUser(
        userId = 0,
        userPhone = "",
        userPassword = "",
        nickname = "",
        avatar = "",
        authorization = "",
        userEmail = ""
    )


    init {
        MainScope().launch(Dispatchers.IO) {
            LogUtils.e("init 1")
            try {
                mDatabase.dataStore.mUserPrivateInfoFlow.collect(){

                }

//        mDatabase.appDatabase.userDao().getUserWithFlow(1)
                LogUtils.e("init2  loggedUser=$logUser")
//            LogUtils.e("init3  loggedUser=$logUser")
            } catch (e: Exception) {
                LogUtils.e("init3  error=${e.message}")
            }
        }
    }

    fun deleteUser() {
        logUser.isLogin = false
    }

    fun updateUser(loginResult: LoginDataResult, password: String) {
        logUser.apply {
            isLogin = true
            userId = loginResult.userId
            userPhone = loginResult.userPhone
            userEmail = loginResult.userEmail
            roleId = loginResult.roleId
            authorization = loginResult.authorization
            userPassword = password
            nickname = loginResult.nickname
            avatar = loginResult.avatar
        }
        LogUtils.i("insert User")
        GlobalScope.launch(Dispatchers.IO) {
            LogUtils.i("insert User1111")
            val user = User(
                uid = 0,
                userId = loginResult.userId,
                roleId = loginResult.roleId,
                roleName = loginResult.roleName,
                phone = loginResult.userPhone,
                email = loginResult.userEmail,
                avatar = loginResult.avatar,
                nickname = loginResult.nickname,
                thirdId = loginResult.thirdPartyPlatformUid,
                homeIds = loginResult.homeIds
            )
            LogUtils.i("insert User2")
            mDatabase.appDatabase.userDao().insertOrUpdate(user)
            mDatabase.appDatabase.userDao().getUser(user.userId)?.let {
                LogUtils.i("insert SUCCESS ${it.toString()}")
                LogUtils.i("logedUser $logUser")
            } ?: let {
                LogUtils.i("insert ERROR")
            }
            LogUtils.i("insert User3")
            try {
                mDatabase.dataStore.setUserPrivateInfo(
                    loginResult.userId,
                    loginResult.authorization,
                    password
                )
            } catch (e: Exception) {
                Logger.e("USER updateUserInfo", "${e.message}")
            }
        }
    }

    fun updateUser(updateUser: UpdateUser) {
        logUser.apply {
            isLogin = true
            userPassword = updateUser.userPassword
            userPhone = updateUser.userPhone
            nickname = updateUser.nickname
            avatar = updateUser.avatar
        }
        appExecutors.scopeIo(Dispatchers.IO).launch {
            mDatabase.dataStore.setUserPrivateInfo(
                userID = logUser.userId,
                auth = logUser.authorization,
                password = logUser.userPassword
            )
            val user = User(
                avatar = logUser.avatar,
                nickname = logUser.nickname,
                phone = logUser.userPhone
            )

            mDatabase.appDatabase.userDao().insertOrUpdate(user)
        }
    }

    fun getUser(): LogUser = logUser
}