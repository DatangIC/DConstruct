package com.datangic.network.networkState

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.datangic.network.NetworkApi

internal class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {
    private val TAG = "Network State"
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        NetworkApi.logger?.log(TAG, "available")
        NetworkState.isAvailable = true
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        NetworkApi.logger?.log(TAG, "lost")
        NetworkState.networkType = -1
        NetworkState.isAvailable = false
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            NetworkState.isAvailable = true
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    NetworkApi.logger?.log(TAG, "transport wifi")
                    NetworkState.onNetworkChange(NetworkType.TRANSPORT_WIFI)
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    NetworkApi.logger?.log(TAG, "transport cellular")
                    NetworkState.onNetworkChange(NetworkType.TRANSPORT_CELLULAR)
                }
                else -> {
                    NetworkState.networkType = 4
                    NetworkApi.logger?.log(TAG, "transport other")
                }
            }
        }

    }
}