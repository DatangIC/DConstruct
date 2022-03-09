package com.datangic.smartlock.preference

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {

    const val THEME = "Theme"

    const val LIGHT_MODE = "light"
    const val DARK_MODE = "dark"
    const val DEFAULT_MODE = "default"

    fun applyTheme(themePref: String) {
        when (themePref) {
            LIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            DARK_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }

    fun applyTheme(context: Context, themePref: String? = null) {
        themePref?.let {
            SharePreferenceUtils.storeValue(context, THEME, themePref)
            applyTheme(themePref)
        } ?: applyTheme(SharePreferenceUtils.getStringValue(context, THEME) ?: DEFAULT_MODE)

    }
}