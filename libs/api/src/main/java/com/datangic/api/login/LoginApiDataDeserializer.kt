package com.datangic.api.login

import com.datangic.api.asArrayList
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object LoginApiDataDeserializer {
    class VerifyCodeResultDeserializer : JsonDeserializer<VerifyCodeResult> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): VerifyCodeResult {
            val mJson = json.asJsonObject
            return VerifyCodeResult(
                mJson.get("userPhone")?.asString,
                mJson.get("expirationTime")?.asLong
            )
        }
    }

    class LoginDataResultDeserializer : JsonDeserializer<LoginDataResult> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LoginDataResult {
            val mJson = json.asJsonObject
            return LoginDataResult(
                userId = mJson.get("userId")?.asLong,
                userPhone = mJson.get("userPhone")?.asString,
                userEmail = mJson.get("userEmail")?.asString,
                roleId = mJson.get("roleId")?.asInt,
                roleName = mJson.get("roleName")?.asString,
                authorization = mJson.get("authorization")?.asString,
                userPlatform = mJson.get("userPlatform")?.asString,
                homeIds = mJson.get("homeIds")?.asArrayList(),
                thirdPartyPlatformUid = mJson.get("thirdPartyPlatformUid")?.asString,
                nickname = mJson.get("nickname")?.asString,
                avatar = mJson.get("avatar")?.asString
            )
        }
    }

    class UserDataDeserializer : JsonDeserializer<UserData> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): UserData {
            val mJson = json.asJsonObject
            return UserData(
                userPhone = mJson.get("userPhone")?.asString,
                code = mJson.get("code")?.asString,
                nickname = mJson.get("nickname")?.asString,
                userPassword = mJson.get("userPassword")?.asString,
                avatar = mJson.get("avatar")?.asString
            )
        }
    }
}