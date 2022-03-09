package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR
class SwitchItem(val itemName: Any, checked: Boolean) : BaseObservable() {

    @get:Bindable
    var checked: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.checked)
        }

    init {
        this.checked = checked
    }

}