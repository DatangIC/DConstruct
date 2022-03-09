package com.datangic.smartlock.parcelable

import android.os.Parcel
import android.os.Parcelable

class UpdateFace(
        var macAddress: String = "",
        var serialNumber: String = "",
        var currentMainVersion: String = "",
        var mainVersion: String = "",
        var updateDate: Int = 0,
        var releaseNotes: String? = null,
        var nCpuFilename: String? = null,
        var nCpuVersion: String = "",
        var nCpuPath: String = "",
        var sCpuFilename: String? = null,
        var sCpuVersion: String = "",
        var sCpuPath: String = "",
        var modelFilename: String? = null,
        var modelVersion: String = "",
        var modelPath: String = "",
        var modelFwFilename: String = "",
        var modelFwPath: String = "",
        var uiFilename: String? = null,
        var uiVersion: String = "",
        var uiPath: String = "",
) : Parcelable {


    constructor(parcel: Parcel) : this() {
        macAddress = parcel.readString().toString()
        serialNumber = parcel.readString().toString()
        currentMainVersion = parcel.readString().toString()
        mainVersion = parcel.readString().toString()
        updateDate = parcel.readInt()
        releaseNotes = parcel.readString().toString()
        nCpuFilename = parcel.readString().toString()
        nCpuVersion = parcel.readString().toString()
        nCpuPath = parcel.readString().toString()

        sCpuFilename = parcel.readString().toString()
        sCpuVersion = parcel.readString().toString()
        sCpuPath = parcel.readString().toString()

        modelFilename = parcel.readString().toString()
        modelVersion = parcel.readString().toString()
        modelPath = parcel.readString().toString()
        modelFwFilename = parcel.readString().toString()
        modelFwPath = parcel.readString().toString()

        uiFilename = parcel.readString().toString()
        uiVersion = parcel.readString().toString()
        uiPath = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(macAddress)
        parcel.writeString(serialNumber)
        parcel.writeString(currentMainVersion)
        parcel.writeString(mainVersion)
        parcel.writeInt(updateDate)
        parcel.writeString(releaseNotes)
        parcel.writeString(nCpuFilename)
        parcel.writeString(nCpuVersion)
        parcel.writeString(nCpuPath)
        parcel.writeString(sCpuFilename)
        parcel.writeString(sCpuVersion)
        parcel.writeString(sCpuPath)
        parcel.writeString(modelFilename)
        parcel.writeString(modelVersion)
        parcel.writeString(modelPath)
        parcel.writeString(modelFwFilename)
        parcel.writeString(modelFwPath)
        parcel.writeString(uiFilename)
        parcel.writeString(uiVersion)
        parcel.writeString(uiPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpdateFace> {
        override fun createFromParcel(parcel: Parcel): UpdateFace {
            return UpdateFace(parcel)
        }

        override fun newArray(size: Int): Array<UpdateFace?> {
            return arrayOfNulls(size)
        }
    }
}