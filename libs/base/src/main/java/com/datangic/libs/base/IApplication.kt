package com.datangic.libs.base

import android.app.Application
import android.content.Context

interface IApplication {
    fun onCreate(application: Application)
    fun attachBaseContext(application: Application, base: Context)
    fun onLowMemory(application: Application)
    fun onTrimMemory(application: Application, level: Int)
    fun onTerminate(application: Application)
}