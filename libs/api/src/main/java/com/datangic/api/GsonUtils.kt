package com.datangic.api

import com.datangic.api.login.LoginApiDataDeserializer
import com.datangic.api.login.LoginDataResult
import com.datangic.api.login.UserData
import com.datangic.api.smartlock.NetLock
import com.datangic.api.smartlock.NetLockDeserializer
import com.datangic.api.smartlock.NetLockSource
import com.datangic.api.smartlock.NetLockUser
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object GsonUtils {

    private val mGson: Gson by lazy { initGson() }

    class PageDeserializer<T> : JsonDeserializer<Page<T>> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Page<T> {
            val mJson = json.asJsonObject
            return Page<T>(
                total = mJson.get("total").asInt,
                pageSize = mJson.get("pageSize").asInt,
                pageNum = mJson.get("pageNum").asInt,
                nextPage = mJson.get("nextPage").asInt,
                pages = mJson.get("pages").asInt,
                prePage = mJson.get("prePage").asInt,
                size = mJson.get("size").asInt,
                startRow = mJson.get("startRow").asInt,
                endRow = mJson.get("endRow").asInt,
                hasPreviousPage = mJson.get("hasPreviousPage").asBoolean,
                hasNextPage = mJson.get("hasNextPage").asBoolean,
                isFirstPage = mJson.get("isFirstPage").asBoolean,
                isLastPage = mJson.get("isLastPage").asBoolean,
                list = Gson().fromJson(mJson.get("list"), object : TypeToken<ArrayList<T>>() {}.type),
                navigateFirstPage = mJson.get("navigateFirstPage").asInt,
                navigateLastPage = mJson.get("navigateLastPage").asInt,
                navigatePages = mJson.get("navigatePages").asInt,
                navigatepageNums = mJson.get("navigatepageNums").asArrayList<Int>(),
            )
        }

    }

    private fun initGson(): Gson {
        return GsonBuilder()
//            .registerTypeAdapter(
//                object : TypeToken<VerifyCodeResult>() {}.type,
//                LoginApiDataDeserializer.VerifyCodeResultDeserializer()
//            )
            .registerTypeAdapter(
                object : TypeToken<LoginDataResult>() {}.type,
                LoginApiDataDeserializer.LoginDataResultDeserializer()
            )
            .registerTypeAdapter(
                object : TypeToken<UserData>() {}.type,
                LoginApiDataDeserializer.UserDataDeserializer()
            )
//            .registerTypeAdapter(
//                object : TypeToken<NetLock<NetLockSource<NetLockUser>>>() {}.type,
//                NetLockDeserializer.NetLockDeserializer2()
//            )
            .registerTypeAdapter(
                object : TypeToken<NetLock<JsonElement>>() {}.type,
                NetLockDeserializer.NetLockDeserializer()
            )
            .registerTypeAdapter(
                object : TypeToken<NetLockSource<JsonElement>>() {}.type,
                NetLockDeserializer.NetLockSourceDeserializer()
            )
            .registerTypeAdapter(
                object : TypeToken<NetLockUser>() {}.type,
                NetLockDeserializer.NetLockUserDeserializer()
            )
            .registerTypeAdapter(
                object : TypeToken<Page<JsonElement>>() {}.type,
                PageDeserializer<JsonElement>()
            )
            .create()
    }

    fun getGson(): Gson = mGson
}