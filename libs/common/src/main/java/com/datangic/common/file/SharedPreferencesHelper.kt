package com.datangic.common.file

import android.content.Context
import android.content.SharedPreferences
import com.datangic.common.SHARED_PREFERENCES_NAME


object SharedPreferencesHelper {

    private var sharePreferences: SharedPreferences? = null

    fun getInstance(context: Context, sharedName: String? = null): SharedPreferences {
        return if (sharedName == null) {
            sharePreferences ?: let {
                sharePreferences = context.getSharedPreferences(
                    sharedName ?: SHARED_PREFERENCES_NAME,
                    Context.MODE_PRIVATE
                )
                sharePreferences!!
            }
        } else {
            context.getSharedPreferences(
                sharedName,
                Context.MODE_PRIVATE
            )
        }
    }


    fun <T> SharedPreferences.put(key: String, value: T): Boolean {

        val editor = this.edit().apply() {
            when (value) {
                is String -> this.putString(key, value)
                is Boolean -> this.putBoolean(key, value)
                is Int -> this.putInt(key, value)
                is Float -> this.putFloat(key, value)
                is Long -> this.putLong(key, value)
            }
        }
        return editor.commit()
    }


    fun SharedPreferences.putStringSet(key: String, value: MutableSet<String>): Boolean {
        return this.edit().putStringSet(key, value).commit()
    }

    fun SharedPreferences.getBoolean(key: String): Boolean {
        return this.getBoolean(key, false)
    }

    fun SharedPreferences.getString(key: String): String {
        return this.getString(key, "") ?: ""
    }

    fun SharedPreferences.getInt(key: String): Int {
        return this.getInt(key, -1)
    }

    fun SharedPreferences.getLong(key: String): Long {
        return this.getLong(key, -1)
    }

    fun SharedPreferences.getFloat(key: String): Float {
        return this.getFloat(key, -1F)
    }

    fun SharedPreferences.getStringSet(key: String): MutableSet<String> {
        return this.getStringSet(key, mutableSetOf()) ?: mutableSetOf()
    }

}


