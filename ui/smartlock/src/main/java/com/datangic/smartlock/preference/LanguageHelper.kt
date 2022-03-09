package com.datangic.smartlock.preference


import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import android.util.DisplayMetrics
import androidx.core.app.ActivityCompat.recreate
import com.datangic.smartlock.utils.Logger
import java.util.*
import java.util.Locale.CHINESE
import java.util.Locale.ENGLISH

object LanguageHelper {
    private val TAG = LanguageHelper::class.simpleName

    const val DefaultLanguage = "Language"
    const val LanguageChanged = "LanguageChange"

    object Language {
        const val ENGLISH = "English"
        const val CHINESE = "Chinese"
    }

    private val mAllLanguages = hashMapOf<String, Locale>(
            Language.ENGLISH to ENGLISH,
            Language.CHINESE to CHINESE
    )

    fun applyLanguage(context: Context, language: String? = null): Context {
        language?.let {
            SharePreferenceUtils.storeValue(context, DefaultLanguage, language)
            SharePreferenceUtils.storeValue(context, LanguageChanged, true)
            changeAppLanguage(context, mAllLanguages[language] ?: getDefaultLanguage(context))
            recreate(context as Activity)
            return context
        } ?: let {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (context is Application) {
                    context
                } else {
                    attachBaseContext(context, mAllLanguages[language]
                            ?: getDefaultLanguage(context))
                }

            } else {
                changeAppLanguage(context, mAllLanguages[language] ?: getDefaultLanguage(context))
                return context
            }
        }
    }

    private fun changeAppLanguage(context: Context, newLanguage: Locale) {
        val res: Resources = context.resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val defaultList = LocaleList.forLanguageTags(newLanguage.language)
            LocaleList.setDefault(defaultList)
            conf.setLocales(defaultList)
        } else {
            conf.locale = newLanguage
        }
        conf.locale = newLanguage
        conf.setLocale(newLanguage)
        context.createConfigurationContext(conf)
        res.updateConfiguration(conf, dm)
    }


    fun reSetLanguage(activity: Activity) {
        if (SharePreferenceUtils.getBooleanValue(activity, LanguageChanged)) {
            SharePreferenceUtils.storeValue(activity, LanguageChanged, false)
            activity.recreate()
        }
    }

    private fun isSupportLanguage(language: String?): Boolean {
        return mAllLanguages.containsKey(language)
    }

    fun getSupportLanguage(language: String? = null): String? {
        if (isSupportLanguage(language)) {
            return language
        }

        if (null == language) {//为空则表示首次安装或未选择过语言，获取系统默认语言
            val locale = Locale.getDefault()
            for (key in mAllLanguages.keys) {
                if (TextUtils.equals(mAllLanguages[key]?.language, locale.language)) {
                    return locale.language
                }
            }
        }
        return Language.ENGLISH
    }

    private fun getDefaultLanguage(context: Context): Locale {
        return getLocaleByLanguage(SharePreferenceUtils.getStringValue(context, DefaultLanguage)
                ?: Language.CHINESE)
    }

    /**
     * 获取指定语言的locale信息，如果指定语言不存在{@link #mAllLanguages}，返回本机语言，如果本机语言不是语言集合中的一种{@link #mAllLanguages}，返回英语
     *
     * @param language language
     * @return
     */
    private fun getLocaleByLanguage(language: String): Locale {
        if (isSupportLanguage(language)) {
            return mAllLanguages[language] ?: CHINESE
        } else {
            val locale = Locale.getDefault()
            for (key in mAllLanguages.keys) {
                if (TextUtils.equals(mAllLanguages[key]?.language, locale.language)) {
                    return locale
                }
            }
        }
        return CHINESE
    }


    private fun attachBaseContext(context: Context, language: Locale): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            context
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    fun updateResources(context: Context, language: Locale): Context {
        Logger.e(TAG, "updateResources=$language")
        val resources = context.resources

        val configuration = resources.configuration
        configuration.setLocale(language)
        configuration.setLocales(LocaleList(language))
        return context.createConfigurationContext(configuration)
    }

}