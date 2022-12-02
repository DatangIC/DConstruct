package com.datangic.smartlock.viewModels

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.datangic.common.utils.Logger
import com.datangic.easypermissions.models.PermissionRequest
import com.datangic.smartlock.R
import com.datangic.smartlock.dialog.DialogEnableBluetooth
import com.datangic.smartlock.respositorys.ScannerHelper
import com.datangic.smartlock.utils.*
import com.datangic.easypermissions.EasyPermissions
import com.datangic.easypermissions.callbacks.PermissionCallbacks
import com.datangic.easypermissions.dialogs.SettingsDialog
import no.nordicsemi.android.support.v18.scanner.ScanFilter

class ScannerRepository(context: Context) : ScannerHelper(context) {

    private val TAG = ScannerRepository::class.java.simpleName
    private var mFragment: Fragment? = null
    private val mHandler = Handler(Looper.myLooper()!!)

    init {
        registerBroadcastReceivers()
    }

    val mPermissionCallbacks = object : PermissionCallbacks {
        override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
            Logger.e(TAG, "onPermissionsGranted")
            stopScan(true)
            Thread.sleep(100)
            mFragment?.let { startScan(it) }
        }

        override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
            Logger.e(TAG, "onPermissionsDenied")
            mFragment?.let {
                if (EasyPermissions.somePermissionPermanentlyDenied(it, perms)) {
                    it.context?.let { it1 ->
                        if (perms.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            SettingsDialog.Builder(it1)
                                .theme(R.style.AppTheme_MaterialDialog)
                                .positiveButtonText(R.string.permission_settings)
                                .negativeButtonText(R.string.cancel)
                                .title(R.string.location_permission_title)
                                .icon(R.drawable.ic_location_off)
                                .rationale(R.string.location_permission_info)
                                .build()
                                .show()
                        }
                    }
                }
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            Logger.e(TAG, "onRequestPermissionsResult")
        }

    }

    fun startScan(fragment: Fragment, filters: MutableList<ScanFilter>? = null) {
        Logger.v(TAG, "Start Scan")
        this.mFragment = fragment
        RequestPermissions.requestPermissions(fragment) {
            startBLEScan(context = fragment.requireContext(), filters)
            mHandler.postDelayed({
                if (!mScannerStateLiveData.isScanning()) {
                    startBLEScan(context = fragment.requireContext(), filters)
                }
            }, 2 * 1000)
        }

    }
}
