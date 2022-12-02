package com.datangic.smartlock.utils

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.datangic.easypermissions.EasyPermissions
import com.datangic.easypermissions.models.PermissionRequest
import com.datangic.smartlock.R
import com.datangic.smartlock.dialog.DialogEnableBluetooth

object RequestPermissions {

    fun requestPermissions(fragment: Fragment, action: () -> Unit) {
        if (EasyPermissions.hasPermissions(
                fragment.requireContext(),
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        ) {
            if (UtilsBle.isBleEnabled()) {
                action()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val request = PermissionRequest.Builder(fragment.context)
                        .code(REQUEST_CODE_CAMERA_STORAGE_PERMISSION)
                        .theme(R.style.AppTheme_MaterialDialog)
                        .positiveButtonText(R.string.grant_permission)
                        .negativeButtonText(R.string.cancel)
                        .title(R.string.location_permission_title)
                        .icon(R.drawable.ic_location_off)
                        .rationale(R.string.location_permission_info)
                        .perms(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                        .build()
                    EasyPermissions.requestPermissions(fragment, request)
                }
            } else {
                DialogEnableBluetooth.newInstance().show(fragment.childFragmentManager, DIALOG_ENABLE_BLUETOOTH_PERMISSION)
            }
        } else {
            val request = PermissionRequest.Builder(fragment.context)
                .code(REQUEST_CODE_CAMERA_STORAGE_PERMISSION)
                .theme(R.style.AppTheme_MaterialDialog)
                .positiveButtonText(R.string.grant_permission)
                .negativeButtonText(R.string.cancel)
                .title(R.string.bluetooth_disabled_title)
                .icon(R.drawable.ic_bluetooth_disabled)
                .rationale(R.string.bluetooth_disabled_info)
                .perms(arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN))
                .build()
            EasyPermissions.requestPermissions(fragment, request)
        }
    }

    fun requestPermissions(activity: AppCompatActivity, action: () -> Unit) {
        if (EasyPermissions.hasPermissions(
                activity,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        ) {
            if (UtilsBle.isBleEnabled()) {
                action()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val request = PermissionRequest.Builder(activity)
                        .code(REQUEST_CODE_CAMERA_STORAGE_PERMISSION)
                        .theme(R.style.AppTheme_MaterialDialog)
                        .positiveButtonText(R.string.grant_permission)
                        .negativeButtonText(R.string.cancel)
                        .title(R.string.location_permission_title)
                        .icon(R.drawable.ic_location_off)
                        .rationale(R.string.location_permission_info)
                        .perms(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                        .build()
                    EasyPermissions.requestPermissions(activity, request)
                }
            } else {
                DialogEnableBluetooth.newInstance().show(activity.supportFragmentManager, DIALOG_ENABLE_BLUETOOTH_PERMISSION)
            }
        } else {
            val request = PermissionRequest.Builder(activity)
                .code(REQUEST_CODE_CAMERA_STORAGE_PERMISSION)
                .theme(R.style.AppTheme_MaterialDialog)
                .positiveButtonText(R.string.grant_permission)
                .negativeButtonText(R.string.cancel)
                .title(R.string.bluetooth_disabled_title)
                .icon(R.drawable.ic_bluetooth_disabled)
                .rationale(R.string.bluetooth_disabled_info)
                .perms(arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN))
                .build()
            EasyPermissions.requestPermissions(activity, request)
        }
    }
}