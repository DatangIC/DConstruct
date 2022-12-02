package com.datangic.network.networkState

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.Flow

object NetworkState {
    internal var isAvailable: Boolean = false
    internal var networkType: Int = -2
    internal fun register(context: Context) {
        val request = NetworkRequest.Builder().build()
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        if (manager is ConnectivityManager) {
            manager.registerNetworkCallback(request, NetworkCallbackImpl())
        }
    }

    private val mNetworkStatus = MutableLiveData(Pair(false, NetworkType.UNKNOWN))

    fun getNetworkStatus() = mNetworkStatus.value

    fun observerNetworkStatus(owner: LifecycleOwner?, observer: Observer<Pair<Boolean, NetworkType>>) {
        if (owner == null)
            mNetworkStatus.observeForever(observer)
        else
            mNetworkStatus.observe(owner, observer)
    }

    internal fun onNetworkChange(networkType: NetworkType) {
        isAvailable = !(networkType == NetworkType.UNKNOWN || networkType == NetworkType.LOST)
        MainScope().launch(Dispatchers.Main) {
            mNetworkStatus.value = Pair(isAvailable, networkType)
        }
    }

}