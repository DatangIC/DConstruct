package com.datangic.smartlock.parcelable

import android.os.Parcel
import android.os.Parcelable

class UpdateFile(

        var type: String = "",
        var macAddress: String = "",
        var serialNumber: String = "",
        var filename: String = "",
        var currentVersion: String = "",
        var version: String = "",
        var md5: String = "",
        var sha1: String = "",
        var zone: String = "",
        var updateDate: Int = 0,
        var releaseNotes: String = "",
        var path: String = "",
) : Parcelable {


    constructor(parcel: Parcel) : this() {
        type = parcel.readString().toString()
        macAddress = parcel.readString().toString()
        serialNumber = parcel.readString().toString()
        filename = parcel.readString().toString()
        currentVersion = parcel.readString().toString()
        version = parcel.readString().toString()
        md5 = parcel.readString().toString()
        sha1 = parcel.readString().toString()
        zone = parcel.readString().toString()
        updateDate = parcel.readInt()
        releaseNotes = parcel.readString().toString()
        path = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(macAddress)
        parcel.writeString(serialNumber)
        parcel.writeString(filename)
        parcel.writeString(currentVersion)
        parcel.writeString(version)
        parcel.writeString(md5)
        parcel.writeString(sha1)
        parcel.writeString(zone)
        parcel.writeInt(updateDate)
        parcel.writeString(releaseNotes)
        parcel.writeString(path)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpdateFile> {
        override fun createFromParcel(parcel: Parcel): UpdateFile {
            return UpdateFile(parcel)
        }

        override fun newArray(size: Int): Array<UpdateFile?> {
            return arrayOfNulls(size)
        }
    }
}