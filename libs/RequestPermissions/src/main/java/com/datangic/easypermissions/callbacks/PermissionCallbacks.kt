package com.datangic.easypermissions.callbacks

import androidx.core.app.ActivityCompat

interface PermissionCallbacks: ActivityCompat.OnRequestPermissionsResultCallback {
    fun onPermissionsGranted(requestCode: Int, perms: List<String>)

    fun onPermissionsDenied(requestCode: Int, perms: List<String>)
}