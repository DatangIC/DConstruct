package com.datangic.localLock

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

class LocalLockExtra(
        @LockAction var action: Int,
        @LockType var type: Int,
        var password: String,
        var isBackPressed: Boolean = false,
        @LockType var secondType: Int = 0,
        var errorCount: Int = 4


) : Parcelable {
    constructor(parcel: Parcel) : this(
            action = parcel.readInt(),
            type = parcel.readInt(),
            password = parcel.readString() ?: "",
            isBackPressed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                parcel.readBoolean()
            } else {
                parcel.readByte() == 1.toByte()
            },
            secondType = parcel.readInt(),
            errorCount = parcel.readInt()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(action)
        parcel.writeInt(type)
        parcel.writeString(password)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            parcel.writeBoolean(isBackPressed)
        else {
            parcel.writeByte(if (isBackPressed) 1 else 0)
        }
        parcel.writeInt(secondType)
        parcel.writeInt(errorCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalLockExtra> {
        override fun createFromParcel(parcel: Parcel): LocalLockExtra {
            return LocalLockExtra(parcel)
        }

        override fun newArray(size: Int): Array<LocalLockExtra?> {
            return arrayOfNulls(size)
        }
    }
}