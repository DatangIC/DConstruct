package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR

class LockStatusItem(val itemName: Any, itemStatus: Any) : BaseObservable() {

    @get:Bindable
    var itemStatus: Any = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.itemStatus)
        }

    init {
        this.itemStatus = itemStatus
    }

}
