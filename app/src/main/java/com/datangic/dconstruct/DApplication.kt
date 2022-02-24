package com.datangic.dconstruct

import com.alibaba.android.arouter.launcher.ARouter
import com.datangic.libs.base.ApplicationProvider

class DApplication : ApplicationProvider() {

    override fun onCreate() {
        super.onCreate()
//        ARouter.debuggable()
        ARouter.openLog();     // 打印日志
        ARouter.openDebug();
        ARouter.init(this)
    }
}