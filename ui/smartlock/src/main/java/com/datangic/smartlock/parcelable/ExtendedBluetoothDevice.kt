package com.datangic.smartlock.parcelable

import android.bluetooth.BluetoothDevice
import android.os.Parcel
import android.os.Parcelable
import no.nordicsemi.android.support.v18.scanner.ScanResult

class ExtendedBluetoothDevice() : Parcelable {
    lateinit var device: BluetoothDevice
    lateinit var scanResult: ScanResult
    var name: String = "Unknown"
    var rssi = 0

    constructor(scanResult: ScanResult) : this() {
        this.scanResult = scanResult
        device = scanResult.device
        scanResult.scanRecord?.let {
            name = it.deviceName.toString()
        }
        rssi = scanResult.rssi
    }

    constructor(parcel: Parcel) : this() {
        device = parcel.readParcelable(BluetoothDevice::class.java.classLoader)!!
        scanResult = parcel.readParcelable(ScanResult::class.java.classLoader)!!
        name = parcel.readString().toString()
        rssi = parcel.readInt()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(device, flags)
        parcel.writeParcelable(scanResult, flags)
        parcel.writeString(name)
        parcel.writeInt(rssi)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExtendedBluetoothDevice> {
        override fun createFromParcel(parcel: Parcel): ExtendedBluetoothDevice {
            return ExtendedBluetoothDevice(parcel)
        }

        override fun newArray(size: Int): Array<ExtendedBluetoothDevice?> {
            return arrayOfNulls(size)
        }
    }


    // Parcelable implementation
    fun matches(scanResult: ScanResult): Boolean {
        return device.address == scanResult.device.address
    }

    override fun equals(other: Any?): Boolean {
        if (other is ExtendedBluetoothDevice) {
            val that: ExtendedBluetoothDevice = other
            return device.address == that.device.address
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = device.hashCode()
        result = 31 * result + scanResult.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + rssi
        return result
    }

    override fun toString(): String {
        return "name=${this.name} mac=${this.device.address}"
    }

}