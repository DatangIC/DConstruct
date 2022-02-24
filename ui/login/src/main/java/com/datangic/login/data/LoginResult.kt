package com.datangic.login.data

data class LoginResult(
    val success: LoggedInUser? = null,
    val error: Int? = null
)
