package com.datangic.libs.base


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.datangic.common.Config
import com.datangic.libs.base.services.NetworkDataService
import com.datangic.network.NetworkApi
import com.datangic.network.impl.IRequestInfo
import kotlinx.coroutines.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin


abstract class ApplicationProvider : Application() {

    protected val moduleList = mutableListOf<IApplication>()
    private var mActivitiesCount = 0;

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
        registerActivityLifecycleCallbacks(mCallback)
        NetworkApi.init(
            this@ApplicationProvider,
            infoI = RequestInfo(this@ApplicationProvider)
        )
        startKoin {
            androidContext(this@ApplicationProvider)
            androidFileProperties()
            modules()
        }
        // 主要提供数据(Database,Network)等数据等初始化
        loadKoinModules(listOf(BaseModule.mRepositoryModule))
        startService(Intent(this@ApplicationProvider, NetworkDataService::class.java))
        MainScope().launch(Dispatchers.IO) {
            moduleList.forEach {
                it.onCreate(this@ApplicationProvider)
            }
        }
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
            mActivitiesCount++
            mActivity = activity
            Log.e("Application Crated", "mCount=$mActivitiesCount")
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
            mActivitiesCount--
            if (mActivitiesCount == 0)
                stopService(Intent(this@ApplicationProvider, NetworkDataService::class.java))
        }
    }

    inner class RequestInfo(val context: Context) : IRequestInfo {
        private val mVersionName: String = Config.getVersionName(context) ?: "0"
        private val mVersionCode: String = Config.getVersionCode(context).toString()
        private val isDebug: Boolean = Config.isDebug(context)
        override fun getVersionName(): String {
            return mVersionName
        }

        override fun getVersionCode(): String {
            return mVersionCode
        }

        override fun isDebug(): Boolean {
            return isDebug
        }
    }

}
