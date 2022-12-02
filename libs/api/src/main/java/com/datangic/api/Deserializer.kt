package com.datangic.api

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

inline fun <reified T> JsonElement.asArrayList(): ArrayList<T> {
    val arrayList = ArrayList<T>()
    val jsonArray = this.asJsonArray

    for (i in jsonArray) {
        arrayList.add(Gson().fromJson(i, object : TypeToken<T>() {}.type))
    }
    return arrayList
}



