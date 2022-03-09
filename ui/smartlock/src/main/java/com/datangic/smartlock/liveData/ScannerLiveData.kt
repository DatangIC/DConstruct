package com.datangic.smartlock.liveData

import androidx.lifecycle.LiveData
import com.datangic.smartlock.parcelable.ExtendedBluetoothDevice
import com.datangic.smartlock.utils.Logger
import no.nordicsemi.android.support.v18.scanner.ScanResult

class ScannerLiveData : LiveData<ScannerLiveData>() {
    private val TAG = ScannerLiveData::class.java.simpleName
    var mDevices: MutableList<ExtendedBluetoothDevice> = ArrayList()
    var mUpdatedDeviceIndex: Int = -1

    fun deviceDiscovered(result: ScanResult) {
        val device: ExtendedBluetoothDevice
        val index = indexOf(result)
        if (index == -1) {
            device = ExtendedBluetoothDevice(result)
            mDevices.add(device)
            mUpdatedDeviceIndex = -1
        } else {
            device = mDevices[index]
            mUpdatedDeviceIndex = index
        }
        // Update RSSI and name
        device.rssi = result.rssi
        device.name = result.scanRecord!!.deviceName.toString()
//        Logger.e(TAG, "advertisingSid=${result.advertisingSid}\n manufacturerSpecificData=${result.scanRecord?.manufacturerSpecificData}\n uuid=${result.scanRecord?.serviceUuids}\n deviceName=${result.scanRecord?.deviceName}\n type=${result.device.type}\n primaryPhy=${result.primaryPhy}\n secondaryPhy=${result.secondaryPhy}\n bondState=${result.device.bondState}\n serviceDataKey=${result.scanRecord?.serviceData?.keys} \n")
//        if (result.scanRecord?.serviceData?.values.isNullOrEmpty()) {
//            Log.e(TAG, "Value is Null")
//        } else {
//            Log.e(TAG, "SSS")
//            Log.e(TAG, "serviceDataValueSize=${((result.scanRecord?.serviceData?.values?.size))}")
//            for (i in result.scanRecord?.serviceData!!) {
//
//                Log.e(TAG, "serviceDataValue=${((i.value as ByteArray).contentToString())}")
//            }
//        }
        Logger.e(TAG, "device name=${device.name} address=${device.device.address}")
        postValue(this)
    }

    /**
     * Finds the index of existing devices on the scan results list.
     *
     * @param result scan result
     * @return index of -1 if not found
     */
    private fun indexOf(result: ScanResult): Int {
        for ((i, device) in mDevices.withIndex()) {
            if (device.matches(result)) return i
        }
        return -1
    }

    fun clear() {
        Logger.i(TAG, "Clear")
        mDevices.clear()
        mUpdatedDeviceIndex = -1
    }
}