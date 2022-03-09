package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR

class CheckRepairItem(val itemName: Any, val itemIcon: Int, correct: Boolean? = null) : BaseObservable() {

    @get:Bindable
    var correct = correct
        set(value) {
            field = value
            notifyPropertyChanged(BR.correct)
        }
}