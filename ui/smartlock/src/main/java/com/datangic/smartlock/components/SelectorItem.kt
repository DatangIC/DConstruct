package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR

class SelectorItem(val itemName: Any, selected: Boolean = false) : BaseObservable() {

    @get:Bindable
    var selected: Boolean = selected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}