package com.datangic.api.login


data class LoginData(
    val email: String? = null,
    val userPhone: String? = null,
    val userPassword: String? = null,
    val code: String? = null,
    val userPlatform: String = "lockWiFiTuYa"
)

//@JsonAdapter(LoginApiDataDeserializer.LoginDataResultDeserializer::class)
data class LoginDataResult(
    val userId: Long,
    val userPhone: String,
    val roleId: Int,
    val roleName: String,
    val authorization: String,
    val userPlatform: String = "lockWiFiTuYa",
    val homeIds: MutableList<Int>,
    val thirdPartyPlatformUid: String,
    val nickname: String,
    val avatar: String
) {
    constructor(
        userId: Long?,
        userPhone: String?,
        roleId: Int?,
        roleName: String?,
        authorization: String?,
        userPlatform: String?,
        homeIds: MutableList<Int>?,
        thirdPartyPlatformUid: String?,
        nickname: String?,
        avatar: String?
    ) : this(
        userId = userId ?: 0,
        userPhone = userPhone ?: "",
        roleId = roleId ?: 0,
        roleName = roleName ?: "",
        authorization = authorization ?: "",
        userPlatform = userPlatform ?: "lockWiFiTuYa",
        homeIds = homeIds ?: mutableListOf(),
        thirdPartyPlatformUid = thirdPartyPlatformUid ?: "",
        nickname = nickname ?: "",
        avatar = avatar ?: ""
    )
}


data class UserData(
    val userPhone: String,
    val oldPassword: String = "",
    val code: String = "",
    val newPassword: String = "",
    val nickname: String = "",
    val userPassword: String = "",
    val avatar: String = ""
) {
    constructor(
        userPhone: String?,
        code: String?,
        nickname: String?,
        userPassword: String?,
        avatar: String?
    ) : this(
        userPhone = userPhone ?: "",
        code = code ?: "",
        nickname = nickname ?: "",
        userPassword = userPassword ?: "",
        avatar = avatar ?: ""
    )
}

data class VerifyCodeResult(val userPhone: String, val expirationTime: Long) {
    constructor(userPhone: String?, expirationTime: Long?) : this(
        userPhone = userPhone ?: "",
        expirationTime = expirationTime ?: 0
    )
}