package com.datangic.smartlock.di

import android.app.Application
import android.content.Context
import com.datangic.libs.base.IApplication
import com.datangic.smartlock.preference.ThemeHelper
import com.datangic.smartlock.request.ApiVolley
import com.datangic.smartlock.utils.CrashHandler
import com.datangic.smartlock.utils.LockFile
import com.datangic.smartlock.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules

class LockApplication : IApplication {
    override fun onCreate(application: Application) {
        GlobalScope.launch(Dispatchers.Default) {
            // 主题
            ThemeHelper.applyTheme(application)
            // 语言
//            LanguageHelper.applyLanguage(this@LockApplication)
            // File 初始化
            LockFile.init(application)
            // Log 初始化
            Logger.init(application)
            // Crash 打印
            CrashHandler.getInstance().init(application)
            //Volley
            ApiVolley.init(application)

            loadKoinModules(listOf(repositoryModule, viewModelModule))
        }
    }

    override fun attachBaseContext(application: Application, base: Context) {
    }

    override fun onLowMemory(application: Application) {
    }

    override fun onTrimMemory(application: Application, level: Int) {
    }

    override fun onTerminate(application: Application) {
    }
}