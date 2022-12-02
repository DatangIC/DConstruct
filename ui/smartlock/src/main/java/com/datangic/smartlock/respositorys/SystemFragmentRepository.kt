package com.datangic.smartlock.respositorys

import com.datangic.data.DatabaseRepository
import com.datangic.smartlock.R
import com.datangic.smartlock.components.LockStatusItem
import com.datangic.smartlock.components.SwitchItem
import com.datangic.smartlock.preference.LanguageHelper
import com.datangic.smartlock.preference.LanguageHelper.DefaultLanguage
import com.datangic.common.file.SharePreferenceUtils
import com.datangic.smartlock.preference.ThemeHelper
import com.datangic.smartlock.preference.ThemeHelper.THEME

class SystemFragmentRepository(val mDatabase: DatabaseRepository) {
    val systemItem by lazy {
        arrayListOf<Any>(
            LockStatusItem(R.string.theme, getThemeStatus()),
            LockStatusItem(R.string.language, getLanguageStatus()),
            R.string.system_logout
        )
    }
    val hasPassword by lazy {
        arrayListOf(
            R.string.system_password_change,
            SwitchItem(R.string.system_fingerprint, checked = false)
        )
    }
    val noPassword by lazy {
        arrayListOf(
            R.string.system_password
        )
    }
    val testItem by lazy {
        arrayListOf(
            1F,
            R.string.set_info,
            R.string.set_secret_code,
            R.string.select_secret_code,
            SwitchItem(R.string.debug_ota, checked = false),
            SwitchItem(R.string.debug, checked = false)
        )
    }

    fun getThemeStatus() = when (SharePreferenceUtils.getStringValue(mDatabase.context, THEME)
        ?: ThemeHelper.DEFAULT_MODE) {
        ThemeHelper.DARK_MODE -> R.string.theme_dark
        ThemeHelper.LIGHT_MODE -> R.string.theme_light
        else -> R.string.theme_system
    }

    fun getLanguageStatus() = when (SharePreferenceUtils.getStringValue(mDatabase.context, DefaultLanguage)
        ?: LanguageHelper.Language.CHINESE) {
        LanguageHelper.Language.CHINESE -> R.string.language_chinese
        LanguageHelper.Language.ENGLISH -> R.string.language_english
        else -> R.string.language_chinese
    }
}