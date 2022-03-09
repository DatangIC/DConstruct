package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR

class WifiItem(val itemName: String, val macAddress: String, rssi: Int = 0) : BaseObservable() {

    @get:Bindable
    var rssi = rssi
        set(value) {
            field = value
            notifyPropertyChanged(BR.rssi)
        }
}
