package com.datangic.libs.base.dataSource

data class LogUser(
    var isLogin: Boolean = false,
    var roleId: Int = 0,
    var userId: Long,
    var userPhone: String? = null,
    var userEmail: String? = null,
    var nickname: String? = null,
    var avatar: String? = null,
    var authorization: String? = null,
    var userPassword: String? = null
)
