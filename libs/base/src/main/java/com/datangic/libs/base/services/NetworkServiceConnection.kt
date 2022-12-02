package com.datangic.libs.base.services

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

object NetworkServiceConnection : ServiceConnection {
    var mBound: Boolean = false
    lateinit var mNetworkDataService: NetworkDataService
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val mBinder = service as NetworkDataService.NetworkBinder
        mNetworkDataService = mBinder.getService()
        mBound = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mBound = false
    }
}