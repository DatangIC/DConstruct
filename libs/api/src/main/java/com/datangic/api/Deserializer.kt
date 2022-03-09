package com.datangic.api

import com.google.gson.Gson
import com.google.gson.JsonElement

inline fun <reified T> JsonElement.asArrayList(): ArrayList<T> {
    val arrayList = ArrayList<T>()
    val jsonArray = this.asJsonArray

    for (i in jsonArray) {
        arrayList.add(Gson().fromJson(i, T::class.java))
    }
    return arrayList
}



