package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR

class ToolbarProgress() : BaseObservable() {
    @get:Bindable
    var isProgress: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.progress)
        }

    @get:Bindable
    var title: Any = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    @get:Bindable
    var subTitle: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.subTitle)
        }

    constructor(title: Any, subTitle: Int = 0, isProgress: Boolean = false) : this() {
        this.title = title
        this.isProgress = isProgress
        this.subTitle = subTitle
    }


}