package com.datangic.network

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import okhttp3.Cookie

internal object SharePrefer {
    val KEY_DOMAIN = "domain"
    var sharePreferences: SharedPreferences? = null
    var application: Application? = null
    fun init(application: Application) {
        sharePreferences = application.getSharedPreferences("cookies", Context.MODE_PRIVATE)
    }

    fun put(key: String, value: String) {
        sharePreferences?.edit()?.putString(key, value)?.apply()
    }

    fun put(key: String, value: Boolean) {
        sharePreferences?.edit()?.putBoolean(key, value)?.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharePreferences?.getBoolean(key, false) ?: false
    }

    fun getString(key: String): String {
        return sharePreferences?.getString(key, "") ?: ""
    }

    fun saveCookies(domain: String?, cookies: List<Cookie>) {
        sharePreferences?.edit()?.let { editor ->
            val cookie = StringBuilder()
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookies.forEach {
                editor.putString(it.name, it.value)
                cookie.append(it.name).append("=").append(it.value).append(";")
                cookieManager.setCookie(it.domain, "${it.name}=${it.value}")
            }
            editor.apply()
            put(KEY_DOMAIN, domain ?: "www.datangic.com")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                application?.let {
                    CookieSyncManager.createInstance(it)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.flush()
            }
        }
    }

    fun getCookies(): ArrayList<Cookie> {
        sharePreferences?.let { sp ->
            try {
                val domain = getString(KEY_DOMAIN)
                return ArrayList<Cookie>().apply {
                    val names = sp.all.keys
                    names.forEach {
                        add(
                            Cookie.Builder()
                                .domain(domain)
                                .name(it)
                                .value(sp.getString(it, "") ?: "")
                                .build()
                        )
                    }
                }
            } catch (e: Exception) {
                NetworkApi.logger?.log("getCookies", "e=${e.message}")
            }
        }
        return arrayListOf(
            Cookie.Builder().domain("datangic.com").name("").value("").build()
        )
    }
}