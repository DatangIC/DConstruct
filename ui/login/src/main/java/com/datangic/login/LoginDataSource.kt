package com.datangic.login

import com.datangic.login.data.LoggedInUser
import java.io.IOException
import java.util.*

class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {

            val fakeUser = LoggedInUser(UUID.randomUUID().toString(), "Jane Doe")
            return Result.success(fakeUser)
        } catch (e: Throwable) {
            return Result.failure(IOException("Error logging in", e))
        }
    }

    fun logout() {

    }
}