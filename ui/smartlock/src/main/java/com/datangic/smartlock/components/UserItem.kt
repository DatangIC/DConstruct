package com.datangic.smartlock.components

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.datangic.smartlock.BR
import com.datangic.data.database.table.DeviceEnum

class UserItem(
    userName: String,
    val userId: Int,
    userStatus: DeviceEnum.DeviceUserStatus
) : BaseObservable() {
    @get:Bindable
    var userName: String = userName
        set(value) {
            field = value
            notifyPropertyChanged(BR.userName)
        }

    @get:Bindable
    var userStatus = userStatus
        set(value) {
            field = value
            notifyPropertyChanged(BR.userStatus)
        }

    override fun equals(other: Any?): Boolean {
        return if (other is UserItem) {
            other.userName == this.userName && other.userStatus == this.userStatus && other.userId == this.userId
        } else false
    }
}