package com.datangic.smartlock.preference

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.datangic.smartlock.utils.AppExecutors
import com.datangic.smartlock.utils.Logger

object SharePreferenceUtils {
    const val SHARE_NAME = "Preference"
    private val mAppExecutors = AppExecutors()

    @SuppressLint("CommitPrefEdits")
    fun storeValue(context: Context, key: String, value: Any) {
        val share = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE).edit()
        when (value) {
            is String -> {
                share.putString(key, value)
            }
            is Int -> {
                share.putInt(key, value)
            }
            is Boolean -> {
                share.putBoolean(key, value)
            }
            is Long -> {
                share.putLong(key, value)
            }
        }
        share.commit()
    }

    fun getStringValue(context: Context, key: String): String? {
        val share = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
        return share.getString(key, null)
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

    private val localPassword: Triple<String, String, String> = Triple("type", "password", "isBiometric")

    fun saveLocalPassword(context: Context, password: Pair<Int, String>): Boolean {
        context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)?.let { sharedPreferences ->
            sharedPreferences.edit()?.let { editor ->
                editor.putInt(localPassword.first, password.first)
                editor.putString(localPassword.second, password.second)
                editor.apply()
                return true
            }
        }
        return false
    }

    fun saveLocalPassword(context: Context, biometric: Boolean): Boolean {
        context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)?.let { sharedPreferences ->
            sharedPreferences.edit()?.let { editor ->
                editor.putBoolean(localPassword.third, biometric)
                editor.apply()
                return true
            }
        }
        return false
    }

    fun getLocalPassword(context: Context): Triple<Int, String, Boolean>? {
        return mAppExecutors.fixedIO().submit<Triple<Int, String, Boolean>?> {
            try {
                context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)?.let { sharedPreferences ->
                    val type: Int = sharedPreferences.getInt(localPassword.first, 0)
                    val password: String = sharedPreferences.getString(localPassword.second, "") ?: ""
                    val biometric: Boolean = sharedPreferences.getBoolean(localPassword.third, false)

                    return@submit if (password == "") {
                        null
                    } else {
                        Triple(type, password, biometric)
                    }
                }
            } catch (e: Exception) {
                Logger.e("Shared Local Password", "e=${e.message}")
            }
            return@submit null
        }.get()
    }


    private val defaultDevice: Pair<String, String> = Pair("serialNumber", "macAddress")

    @SuppressLint("CommitPrefEdits")
    fun saveDefaultDevice(context: Context, device: Pair<String, String>): Boolean {
        context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)?.let { sharedPreferences ->
            sharedPreferences.edit()?.let { editor ->
                editor.putString(defaultDevice.first, device.first)
                editor.putString(defaultDevice.second, device.second)
                editor.apply()
                return true
            }
        }
        return false
    }

    fun getDefaultDevice(context: Context): Pair<String, String>? {
        return mAppExecutors.fixedIO().submit<Pair<String, String>?> {
            try {
                context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)?.let { sharedPreferences ->
                    val serialNumber: String = sharedPreferences.getString(defaultDevice.first, "") ?: ""
                    val macAddress: String = sharedPreferences.getString(defaultDevice.second, "") ?: ""

                    return@submit if (serialNumber == "" || macAddress == "") {
                        null
                    } else {
                        Pair(serialNumber, macAddress)
                    }
                }
            } catch (e: Exception) {
                Logger.e("Shared", "e=${e.message}")
            }
            return@submit null
        }.get()
    }

}