package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR
import com.datangic.smartlock.R

class CardSetting(
    val macAddress: String,
    lockName: Any = R.string.management_name,
    battery: Int = 50,
    syncAt: String = "",
    bleStatus: Int = R.string.ble_disconnected,
    open: Boolean = false
) : BaseObservable() {

    @get:Bindable
    var lockName: Any = lockName
        set(value) {
            field = value
            notifyPropertyChanged(BR.lockName)
        }

    @get:Bindable
    var battery: Int = battery
        set(value) {
            field = value
            notifyPropertyChanged(BR.battery)
        }

    @get:Bindable
    var syncAt: String = syncAt
        set(value) {
            field = value
            notifyPropertyChanged(BR.syncAt)
        }

    @get:Bindable
    var bleStatus: Any = bleStatus
        set(value) {
            field = value
            notifyPropertyChanged(BR.bleStatus)
        }

    @get:Bindable
    var open: Boolean = open
        set(value) {
            field = value
            notifyPropertyChanged(BR.open)
        }

}
