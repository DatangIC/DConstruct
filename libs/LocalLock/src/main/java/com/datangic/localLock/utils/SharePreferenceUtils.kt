package com.datangic.localLock.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log

object SharePreferenceUtils {

    const val SHARE_NAME = "PRIVATE"

    const val AUTH_ERROR = "ERROR_TIME"
    const val DEFAULT_COUNT = 4

    @SuppressLint("CommitPrefEdits")
    fun storeValue(context: Context, key: String, value: Any) {
        val share = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE).edit()
        when (value) {
            is String -> {
                Log.e("TAG", "String=$value")
                share.putString(key, value)
            }
            is Int -> {
                Log.e("TAG", "Int=$value")
                share.putInt(key, value)
            }
            is Boolean -> {
                Log.e("TAG", "Boolean=$value")
                share.putBoolean(key, value)
            }
            is Long -> {
                Log.e("TAG", "Long=$value")
                share.putLong(key, value)
            }
        }
        share.commit()
    }

    fun getStringValue(context: Context, key: String): String {
        val share = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
        return share.getString(key, "") ?: ""
    }

    fun getIntValue(context: Context, key: String): Int {
        val share = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
        return share.getInt(key, 0)
    }

    fun getLongValue(context: Context, key: String): Long {
        val share = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
        return share.getLong(key, 0L)
    }

    fun getBooleanValue(context: Context, key: String): Boolean {
        val share = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
        return share.getBoolean(key, false)
    }

}