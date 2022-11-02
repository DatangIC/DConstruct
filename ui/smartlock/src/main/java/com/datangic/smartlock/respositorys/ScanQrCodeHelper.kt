package com.datangic.smartlock.respositorys

import android.Manifest
import android.app.Activity
import android.content.Intent
import com.datangic.easypermissions.EasyPermissions
import com.datangic.easypermissions.annotations.AfterPermissionGranted
import com.datangic.easypermissions.callbacks.PermissionCallbacks
import com.datangic.easypermissions.dialogs.SettingsDialog
import com.datangic.easypermissions.models.PermissionRequest
import com.datangic.smartlock.R
import com.datangic.common.utils.Logger
import com.datangic.smartlock.utils.REQUEST_CODE_CAMERA_STORAGE_PERMISSION
import com.datangic.smartlock.utils.REQUEST_SCAN_QRCODE_CODE
import com.datangic.zxing.ScanActivity as CaptureActivity

object ScanQrCodeHelper {
    private val TAG = "ScanQrCodeHelper"

    @AfterPermissionGranted(REQUEST_CODE_CAMERA_STORAGE_PERMISSION)
    fun onScanQrCode(activity: Activity, requestCode: Int = REQUEST_SCAN_QRCODE_CODE) {
        if (EasyPermissions.hasPermissions(activity, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Logger.e(TAG, "CameraScan  requestCode2=$REQUEST_SCAN_QRCODE_CODE")
            activity.startActivityForResult(Intent(activity, CaptureActivity::class.java), requestCode)
            Logger.e(TAG, "CameraScan  requestCode=$REQUEST_SCAN_QRCODE_CODE")
        } else {
            val request = PermissionRequest.Builder(activity)
                    .code(REQUEST_CODE_CAMERA_STORAGE_PERMISSION)
                    .theme(R.style.AppTheme_MaterialDialog)
                    .positiveButtonText(R.string.grant_permission)
                    .negativeButtonText(R.string.cancel)
                    .title(R.string.camera_and_storage_permission_title)
                    .icon(R.drawable.ic_permission)
                    .rationale(R.string.camera_and_storage_permission_info)
                    .perms(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    .build()
            if (EasyPermissions.hasPermissions(activity, Manifest.permission.CAMERA)) {
                request.apply {
                    title = R.string.storage_permission_title
                    icon = R.drawable.ic_file
                    rationale = R.string.storage_permission_info
                    perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else if (EasyPermissions.hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                request.apply {
                    title = R.string.camera_permission_title
                    icon = R.drawable.ic_camera
                    rationale = R.string.camera_permission_info
                    perms = arrayOf(Manifest.permission.CAMERA)
                }

            }
            EasyPermissions.requestPermissions(activity, request)
        }
    }


    fun getPermissionCallbacks(activity: Activity) = object : PermissionCallbacks {
        override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
            onScanQrCode(activity)
        }

        override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
            activity.let {
                if (EasyPermissions.somePermissionPermanentlyDenied(it, perms)) {

                    activity.let { it1 ->
                        val settingsDialog = SettingsDialog.Builder(it1)
                                .theme(R.style.AppTheme_MaterialDialog)
                                .positiveButtonText(R.string.permission_settings)
                                .negativeButtonText(R.string.cancel)
                                .build()
                        when {
                            perms.containsAll(listOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) -> {
                                settingsDialog.apply {
                                    icon = R.drawable.ic_permission
                                    title = R.string.camera_and_storage_permission_title
                                    rationale = R.string.camera_and_storage_permission_info
                                }
                            }
                            perms.containsAll(listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)) or
                                    perms.containsAll(listOf(Manifest.permission.READ_EXTERNAL_STORAGE)) -> {
                                settingsDialog.apply {
                                    icon = R.drawable.ic_file
                                    title = R.string.storage_permission_title
                                    rationale = R.string.storage_permission_info
                                }
                            }
                            else -> {
                                settingsDialog.apply {
                                    icon = R.drawable.ic_camera
                                    title = R.string.camera_permission_title
                                    rationale = R.string.camera_permission_info
                                }
                            }
                        }
                        settingsDialog.show()
                    }
                }
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            Logger.e("onRequestPermissionsResult")
        }

    }

}