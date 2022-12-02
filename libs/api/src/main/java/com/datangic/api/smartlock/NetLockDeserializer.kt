package com.datangic.api.smartlock

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object NetLockDeserializer {
    class NetLockDeserializer : JsonDeserializer<NetLock<JsonElement>> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NetLock<JsonElement> {
            val mJson = json.asJsonObject
//            val mSourceJson = mJson.get("productSource").asJsonObject
//            val mUserJson = mSourceJson.get("users").asJsonObject
//
//            val mNetLockUser = NetLockUser(
//                userId = mUserJson.get("userId").asInt,
//                authCode = mUserJson.get("authCode").asString ?: "",
//                phoneNumber = mUserJson.get("phoneNumber").asString ?: "",
//                isAdmin = mUserJson.get("isAdmin").asInt,
//                userName = mUserJson.get("userName").asString ?: "",
//                createTime = mUserJson.get("createTime").asLong
//            )

            return NetLock(
                devId = mJson.get("devId")?.asInt ?: 0,
                devName = mJson.get("devName")?.asString ?: "",
                devNo = mJson.get("devNo")?.asString ?: "",
                productResource = mJson.get("productResource").asJsonObject
            )
        }
    }

    class NetLockDeserializer2 : JsonDeserializer<NetLock<NetLockSource<NetLockUser>>> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NetLock<NetLockSource<NetLockUser>> {
            val mJson = json.asJsonObject
            val mSourceJson = mJson.get("productResource").asJsonObject
            val mUserJson = mSourceJson.get("users").asJsonArray

            val mNetLockUserList: MutableList<NetLockUser> = mutableListOf()
            for (uJson in mUserJson) {
                val _uJson = uJson.asJsonObject
                val mNetLockUser = NetLockUser(
                    userId = _uJson.get("userId").asInt,
                    authCode = _uJson.get("authCode").asString ?: "",
                    phoneNumber = _uJson.get("phoneNumber").asString ?: "",
                    isAdmin = _uJson.get("isAdmin").asInt,
                    userName = _uJson.get("userName").asString ?: "",
                    createTime = _uJson.get("createTime").asLong
                )
                mNetLockUserList.add(mNetLockUser)
            }
            val mNetLockSource = NetLockSource(
                secretCode = mJson.get("secretCode").asString,
                imei = mJson.get("imei").asString ?: "",
                sn = mJson.get("sn").asString ?: "",
                battery = mJson.get("battery").asInt,
                mac = mJson.get("mac").asString ?: "",
                users = mNetLockUserList,
                createTime = mJson.get("createTime").asLong
            )

            return NetLock(
                devId = mJson.get("devId")?.asInt ?: 0,
                devName = mJson.get("devName")?.asString ?: "",
                devNo = mJson.get("devNo")?.asString ?: "",
                productResource = mNetLockSource
            )
        }
    }

    class NetLockSourceDeserializer : JsonDeserializer<NetLockSource<JsonElement>> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): NetLockSource<JsonElement> {
            val mJson = json.asJsonObject
            return NetLockSource(
                secretCode = mJson.get("secretCode").asString,
                imei = mJson.get("imei").asString ?: "",
                sn = mJson.get("sn").asString ?: "",
                battery = mJson.get("battery").asInt,
                mac = mJson.get("mac").asString ?: "",
                users = Gson().fromJson(mJson.get("users"), object : TypeToken<ArrayList<JsonElement>>() {}.type),
                createTime = mJson.get("createTime").asLong
            )
        }
    }

    class NetLockUserDeserializer : JsonDeserializer<NetLockUser> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): NetLockUser {
            val mJson = json.asJsonObject
            return NetLockUser(
                userId = mJson.get("userId").asInt,
                authCode = mJson.get("authCode").asString ?: "",
                phoneNumber = mJson.get("phoneNumber").asString ?: "",
                isAdmin = mJson.get("isAdmin").asInt,
                userName = mJson.get("userName").asString ?: "",
                createTime = mJson.get("createTime").asLong
            )
        }
    }
}