package com.datangic.login.di

import com.datangic.libs.base.ApplicationProvider
import com.datangic.network.NetworkApi

class LoginApplicationForDebug : ApplicationProvider() {
    init {
        moduleList.add(LoginApplication())
    }
    override fun onCreate() {
        super.onCreate()
//        ARouter.debuggable()
//        ARouter.openLog();     // 打印日志
//        ARouter.openDebug();
//        ARouter.init(this)
        NetworkApi.init(this)
    }
}