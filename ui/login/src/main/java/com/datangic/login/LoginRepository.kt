package com.datangic.login

import android.util.Log
import com.datangic.login.data.LoggedInUser

class LoginRepository(private val dataSource: LoginDataSource) {

    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        val result = dataSource.login(username, password)
        Log.e("TAG", "username=$username\t password=$password")
        if (result.isSuccess) {
            setLoggedInUser(result.getOrDefault(LoggedInUser("", "")))
        }
        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

}