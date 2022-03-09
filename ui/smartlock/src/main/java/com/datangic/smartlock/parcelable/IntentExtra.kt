package com.datangic.smartlock.parcelable

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

class IntentExtra(
    var macAddress: String,
    var serialNumber: String,
    var userID: Int = 0,
    var selectedType: Int = 0,
    var hasNFC: Boolean = true,
    var hasFace: Boolean = false,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        macAddress = parcel.readString() ?: "",
        serialNumber = parcel.readString() ?: "",
        userID = parcel.readInt(),
        selectedType = parcel.readInt(),
        hasNFC = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.readBoolean()
        } else {
            parcel.readByte() == 1.toByte()
        },
        hasFace = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.readBoolean()
        } else {
            parcel.readByte() == 1.toByte()
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(macAddress)
        parcel.writeString(serialNumber)
        parcel.writeInt(userID)
        parcel.writeInt(selectedType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            parcel.writeBoolean(hasNFC)
        else {
            parcel.writeByte(if (hasNFC) 1 else 0)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            parcel.writeBoolean(hasFace)
        else {
            parcel.writeByte(if (hasFace) 1 else 0)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IntentExtra> {
        override fun createFromParcel(parcel: Parcel): IntentExtra {
            return IntentExtra(parcel)
        }

        override fun newArray(size: Int): Array<IntentExtra?> {
            return arrayOfNulls(size)
        }
    }
}