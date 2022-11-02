package com.datangic.login.di

import android.app.Application
import android.content.Context
import com.datangic.libs.base.IApplication
import kotlinx.coroutines.*
import org.koin.core.context.loadKoinModules

class LoginApplication : IApplication {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(application: Application) {
        GlobalScope.launch(Dispatchers.Default) {
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