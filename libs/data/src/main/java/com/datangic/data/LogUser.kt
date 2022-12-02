package com.datangic.data

data class LogUser(
    var roleId: Int = 0,
    var userId: Int = 0,
    var roleName: String? = null,
    var userPhone: String? = null,
    var userEmail: String? = null,
    var nickname: String? = null,
    var avatar: String? = null,
    var authorization: String? = null,
    var userPassword: String? = null,
    var thirdPartyPlatformUid: String? = null,
    var status: LogStatus = LogStatus.INIT
)

enum class LogStatus {
    INIT,
    NOT_LOGIN, //未登录
    LOGGING, //登录中
    LOGGED,// 已登录
    NO_NET,// 无网络
    NOT_AUTH,// 未验证
}