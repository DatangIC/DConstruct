package com.datangic.localLock

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

class LocalLockResult(
        @LockAction var action: Int,
        @LockType var type: Int,
        var password: String,
        var success: Boolean = false

) : Parcelable {
    constructor(parcel: Parcel) : this(
            action = parcel.readInt(),
            type = parcel.readInt(),
            password = parcel.readString() ?: "",
            success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                parcel.readBoolean()
            } else {
                parcel.readByte() == 1.toByte()
            }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(action)
        parcel.writeInt(type)
        parcel.writeString(password)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            parcel.writeBoolean(success)
        else {
            parcel.writeByte(if (success) 1 else 0)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalLockResult> {
        override fun createFromParcel(parcel: Parcel): LocalLockResult {
            return LocalLockResult(parcel)
        }

        override fun newArray(size: Int): Array<LocalLockResult?> {
            return arrayOfNulls(size)
        }
    }
}