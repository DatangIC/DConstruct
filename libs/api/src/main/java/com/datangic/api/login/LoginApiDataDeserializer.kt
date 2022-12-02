package com.datangic.api.login

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object LoginApiDataDeserializer {
    class VerifyCodeResultDeserializer : JsonDeserializer<VerifyCodeResult> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): VerifyCodeResult {
            val mJson = json.asJsonObject
            return VerifyCodeResult(
                mJson.get("userPhone")?.asString,
                mJson.get("expirationTime")?.asLong,
                mJson.get("msmCode")?.asString
            )
        }
    }

    class LoginDataResultDeserializer : JsonDeserializer<LoginDataResult> {
        override fun deserialize(json: JsonElement, typeOf: Type, context: JsonDeserializationContext): LoginDataResult {
            val mJson = json.asJsonObject
            var _homeIds: MutableList<Int> = mutableListOf<Int>()
            var _extraRoleIds: MutableList<Int> = mutableListOf<Int>()
            val type: Type = object : TypeToken<MutableList<Int>>() {}.type
            mJson.get("homeIds")?.let {
                _homeIds = Gson().fromJson(it, type)
            }
            mJson.get("extraRoleIds")?.let {
                _extraRoleIds = Gson().fromJson(it, type)
            }
            return LoginDataResult(
                userId = mJson.get("userId")?.asInt,
                userPhone = mJson.get("userPhone")?.asString,
                userEmail = mJson.get("userEmail")?.asString,
                roleId = mJson.get("roleId")?.asInt,
                roleName = mJson.get("roleName")?.asString,
                authorization = mJson.get("authorization")?.asString,
                userPlatform = mJson.get("userPlatform")?.asString,
                homeIds = _homeIds,
                thirdPartyPlatformUid = mJson.get("thirdPartyPlatformUid")?.asString,
                nickname = mJson.get("nickname")?.asString,
                avatar = mJson.get("avatar")?.asString,
                extraRoleIds = _extraRoleIds
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