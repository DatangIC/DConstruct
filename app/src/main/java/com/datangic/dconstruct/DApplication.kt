package com.datangic.dconstruct

import com.alibaba.android.arouter.launcher.ARouter
import com.datangic.libs.base.ApplicationProvider
import com.datangic.login.di.LoginApplication
import com.datangic.smartlock.di.LockApplication

class DApplication : ApplicationProvider() {
    init {
        moduleList.addAll(
            linkedSetOf(
                LoginApplication(),
                LockApplication()
            )
        )
    }

    override fun onCreate() {
        super.onCreate()
//        ARouter.debuggable()
        ARouter.openLog();     // 打印日志
        ARouter.openDebug();
        ARouter.init(this)
    }
}