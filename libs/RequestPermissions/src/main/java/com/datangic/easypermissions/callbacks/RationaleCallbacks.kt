package com.datangic.easypermissions.callbacks

interface RationaleCallbacks {
    fun onRationaleAccepted(requestCode: Int)

    fun onRationaleDenied(requestCode: Int)
}