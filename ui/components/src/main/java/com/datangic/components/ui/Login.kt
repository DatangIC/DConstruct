package com.datangic.components.ui

import androidx.compose.runtime.Immutable

@Immutable
data class LoginPost(
    val uid: String,
    val upwd: String,
    var ucode: String = ""
)

