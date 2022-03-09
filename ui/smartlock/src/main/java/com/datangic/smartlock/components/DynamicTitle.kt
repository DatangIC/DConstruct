package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR

class DynamicTitle(title: Any) : BaseObservable() {

    @get:Bindable
    var title: Any = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    init {
        this.title = title
    }

}
