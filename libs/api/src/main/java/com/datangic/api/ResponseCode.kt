package com.datangic.api

import androidx.annotation.StringRes

object ResponseCode {
    const val success = "0"
    const val auth_failed = "401"
    const val noOwner = "12202"
    const val notExist = "12311"
    const val hasSheared = "12337"
}

fun getSource(code: String): Int {
    return when (code) {
        "401" -> R.string.http_err_401
        "12202" -> R.string.http_no_permission
        "12311"->R.string.http_dev_not_exist
        "12337"->R.string.http_dev_not_exist
        else -> R.string.http_no_permission
    }
}