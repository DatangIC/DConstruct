package com.datangic.network.networkState

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest

internal object NetworkState {
    internal var isAvailable: Boolean = false
    internal var networkType: Int = -2
    internal fun register(context: Context) {
        val request = NetworkRequest.Builder().build()
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        if (manager is ConnectivityManager) {
            manager.registerNetworkCallback(request, NetworkCallbackImpl())
        }
    }
}