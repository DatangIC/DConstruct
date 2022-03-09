package com.datangic.smartlock.liveData

import androidx.lifecycle.LiveData

class ScannerStateLiveData : LiveData<ScannerStateLiveData>() {
    private var isScanning = false

    enum class State {
        SCANNING, BLE_UNABLE, DEVICE_FOUND, BLE_ENABLE, SCAN_STOP, SCAN_TIMEOUT, LOCATION_ENABLE, LOCATION_UNABLE
    }

    var state: State = State.SCAN_STOP


    fun scanningStarted() {
        isScanning = true
        state = State.SCANNING
        postValue(this)
    }

    fun scanStopped() {
        isScanning = false
        state = State.SCAN_STOP
        postValue(this)
    }

    fun bluetoothEnabled() {
        state = State.BLE_ENABLE
        postValue(this)
    }

    fun bluetoothDisabled() {
        state = State.BLE_UNABLE
        postValue(this)
    }


    fun locationEnabled() {
        state = State.LOCATION_ENABLE
        postValue(this)
    }

    fun locationDisabled() {
        state = State.LOCATION_UNABLE
        postValue(this)
    }

    fun deviceFound() {
        state = State.DEVICE_FOUND
        postValue(this)

    }

    fun scanTimeout() {
        isScanning = false
        state = State.SCAN_TIMEOUT
        postValue(this)

    }

    /**
     * Returns whether scanning is in progress.
     */
    fun isScanning(): Boolean {
        return isScanning
    }

    fun setLocationEnabled(enabled: Boolean) {
        state = if (enabled) State.BLE_ENABLE else State.BLE_UNABLE
        postValue(this)
    }

}