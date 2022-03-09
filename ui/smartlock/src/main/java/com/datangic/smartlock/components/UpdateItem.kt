package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR

class UpdateItem(val icon: Int, val title: Any, update: Boolean) : BaseObservable() {
    @get:Bindable
    var update = update
        set(value) {
            field = value
            notifyPropertyChanged(
                    BR.update)
        }
}