package com.datangic.smartlock.parcelable

import android.os.Parcel
import android.os.Parcelable

class SendMessage() : Parcelable {
    var tag: Byte = 0
    var key1: Any = ""
    var errorCode: Int = 0


    constructor(parcel: Parcel) : this() {
        tag = parcel.readByte()
        errorCode = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(tag)
//        parcel.writeMap()
        parcel.writeInt(errorCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SendMessage> {
        override fun createFromParcel(parcel: Parcel): SendMessage {
            return SendMessage(parcel)
        }

        override fun newArray(size: Int): Array<SendMessage?> {
            return arrayOfNulls(size)
        }
    }
}