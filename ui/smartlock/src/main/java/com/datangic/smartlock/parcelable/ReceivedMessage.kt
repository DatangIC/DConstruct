package com.datangic.smartlock.parcelable

import android.bluetooth.BluetoothDevice
import android.os.Parcel
import android.os.Parcelable

class ReceivedMessage() : Parcelable {
    lateinit var device: BluetoothDevice
    var tag: Byte = 0
    var code: Int = 0
    var errorCode: Int = 0


    constructor(parcel: Parcel) : this() {
        device = parcel.readParcelable(BluetoothDevice::class.java.classLoader)!!
        tag = parcel.readByte()
        code = parcel.readInt()
        errorCode = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(device, flags)
        parcel.writeByte(tag)
        code = parcel.readInt()
        parcel.writeInt(errorCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReceivedMessage> {
        override fun createFromParcel(parcel: Parcel): ReceivedMessage {
            return ReceivedMessage(parcel)
        }

        override fun newArray(size: Int): Array<ReceivedMessage?> {
            return arrayOfNulls(size)
        }
    }
}