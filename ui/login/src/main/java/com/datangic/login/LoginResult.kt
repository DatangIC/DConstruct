package com.datangic.login

import com.datangic.login.data.LoggedInUser

data class LoginResult(
    val success: LoggedInUser? = null,
    val error: Int? = null
)
