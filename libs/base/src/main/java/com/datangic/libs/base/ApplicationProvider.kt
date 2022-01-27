package com.datangic.libs.base


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle


abstract class ApplicationProvider : Application() {

    private val moduleList = mutableListOf<IApplication>()

    @SuppressLint("StaticFieldLeak")
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mActivity: Activity
        public fun Application.getCurrentActivity(): Activity {
            return mActivity
        }
    }

    override fun onCreate() {
        super.onCreate()
        moduleList.forEach {
            it.onCreate(this)
        }
        registerActivityLifecycleCallbacks(mCallback)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        moduleList.forEach {
            it.attachBaseContext(this, base)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        moduleList.forEach {
            it.onLowMemory(this)
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        moduleList.forEach {
            it.onTrimMemory(this, level)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        moduleList.forEach { it.onTerminate(this) }
    }

    private val mCallback = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            mActivity = activity
        }

        override fun onActivityStarted(activity: Activity) {
            mActivity = activity
        }

        override fun onActivityResumed(activity: Activity) {
            mActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {
            mActivity = activity
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

    }
}
