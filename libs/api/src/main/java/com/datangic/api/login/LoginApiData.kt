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
    val userId: Int,
    val userPhone: String,
    val userEmail: String,
    val roleId: Int,
    val roleName: String,
    var authorization: String,
    val userPlatform: String = "lockWiFiTuYa",
    val homeIds: MutableList<Int>,
    val thirdPartyPlatformUid: String,
    val nickname: String,
    val avatar: String,
    val extraRoleIds: MutableList<Int>
) {
    constructor(
        userId: Int?,
        userPhone: String?,
        userEmail: String?,
        roleId: Int?,
        roleName: String?,
        authorization: String?,
        userPlatform: String?,
        homeIds: MutableList<Int>?,
        thirdPartyPlatformUid: String?,
        nickname: String?,
        avatar: String?,
        extraRoleIds: MutableList<Int>?
    ) : this(
        userId = userId ?: 0,
        userPhone = userPhone ?: "",
        userEmail = userEmail ?: "",
        roleId = roleId ?: 0,
        roleName = roleName ?: "",
        authorization = authorization ?: "",
        userPlatform = userPlatform ?: "lockWiFiTuYa",
        homeIds = homeIds ?: mutableListOf(),
        thirdPartyPlatformUid = thirdPartyPlatformUid ?: "",
        nickname = nickname ?: "",
        avatar = avatar ?: "",
        extraRoleIds = extraRoleIds ?: mutableListOf()
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

data class UpdateUser(
    val userPhone: String = "",
    val userPassword: String = "",
    val userId: Long = 0,
    val nickname: String = "",
    val avatar: String = ""
)

data class VerifyCodeResult(val userPhone: String, val expirationTime: Long, val smsCode: String) {
    constructor(userPhone: String?, expirationTime: Long?, smsCode: String?) : this(
        userPhone = userPhone ?: "",
        expirationTime = expirationTime ?: 0,
        smsCode = smsCode ?: ""
    )
}