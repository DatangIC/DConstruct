package com.datangic.libs.base.services

import android.app.Service
import android.content.Intent
import android.os.*
import com.datangic.common.utils.Logger
import com.datangic.libs.base.dataSource.DeviceSource
import com.datangic.libs.base.dataSource.UserSource
import com.datangic.network.ResponseStatus
import com.datangic.network.networkState.NetworkState
import com.datangic.network.networkState.NetworkType
import com.google.gson.JsonElement
import org.koin.android.ext.android.inject
import java.util.concurrent.Executors

class NetworkDataService : Service() {

    private val binder = NetworkBinder()
    private val singleExecutors = Executors.newSingleThreadExecutor()
    private val serviceExecutors = Executors.newCachedThreadPool()
    private var serviceHandler: ServiceHandler? = null
    private val mUseSource: UserSource by inject()
    private val mDeviceSource: DeviceSource by inject()
    private var mNetworkStatus: Pair<Boolean, NetworkType> = Pair(false, NetworkType.UNKNOWN)

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            stopSelf(msg.arg1)
        }
    }

    inner class NetworkBinder : Binder() {
        fun getService(): NetworkDataService = this@NetworkDataService
    }

    override fun onCreate() {
        NetworkState.observerNetworkStatus(null) {
            mNetworkStatus = it
        }
        mUseSource.mDatabase.getLogUser()
        mDeviceSource
    }

    fun logout(action: (ResponseStatus<JsonElement>) -> Unit) = mUseSource.loginOut(action)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Logger.e("Service", " DESTORY ThreadName =${Thread.currentThread().name}")
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }
}