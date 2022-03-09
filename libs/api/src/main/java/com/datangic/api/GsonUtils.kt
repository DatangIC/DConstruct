package com.datangic.api

import com.datangic.api.login.LoginApiDataDeserializer
import com.datangic.api.login.UserData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object GsonUtils {

    private val mGson: Gson by lazy { initGson() }

    private fun initGson(): Gson {
        return GsonBuilder()
//            .registerTypeAdapter(
//                object : TypeToken<VerifyCodeResult>() {}.type,
//                LoginApiDataDeserializer.VerifyCodeResultDeserializer()
//            )
//            .registerTypeAdapter(
//                object : TypeToken<LoginDataResult>() {}.type,
//                LoginApiDataDeserializer.LoginDataResultDeserializer()
//            )
            .registerTypeAdapter(
                object : TypeToken<UserData>() {}.type,
                LoginApiDataDeserializer.UserDataDeserializer()
            ).create()
    }

    fun getGson(): Gson = mGson
}