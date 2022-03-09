package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR

class DeviceWithBluetooth(val deviceName: String, val serialNumber: String, val macAddress: String, connect: Boolean, val isSelected: Boolean = false) : BaseObservable() {
    @get:Bindable
    var connect: Boolean = connect
        set(value) {
            field = value
            notifyPropertyChanged(BR.connect)
        }


}
