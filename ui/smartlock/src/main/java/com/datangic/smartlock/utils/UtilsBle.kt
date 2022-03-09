package com.datangic.smartlock.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

const val EXTRA_DEVICE = "EXTRA_DEVICE"

const val PREFS_LOCATION_NOT_REQUIRED = "location_not_required"
const val EXTRA_DATA_PROVISIONING_SERVICE = "EXTRA_DATA_PROVISIONING_SERVICE"
const val PREFS_PERMISSION_REQUESTED = "permission_requested"

class UtilsBle {
    companion object {
        fun String.checkMac(): String {
            return when (this.length) {
                12 -> {
                    var temp = ""
                    for (i in this.indices) {
                        temp += this[i]
                        if (i % 2 == 1 && i != 11) {
                            temp += ":"
                        }
                    }
                    temp
                }
                else -> this
            }
        }

        /**
         * Checks whether Bluetooth is enabled.
         *
         * @return true if Bluetooth is enabled, false otherwise.
         */
        fun isBleEnabled(): Boolean {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            return adapter != null && adapter.isEnabled
        }

        /**
         * On some devices running Android Marshmallow or newer location services must be enabled in order to scan for Bluetooth LE devices.
         * This method returns whether the Location has been enabled or not.
         *
         * @return true on Android 6.0+ if location mode is different than LOCATION_MODE_OFF. It always returns true on Android versions prior to Marshmallow.
         */
        fun isLocationEnabled(context: Context): Boolean {
            if (isMarshmallowOrAbove()) {
                var locationMode = Settings.Secure.LOCATION_MODE_OFF
                try {
                    locationMode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                } catch (e: Settings.SettingNotFoundException) {
                    // do nothing
                }
                return locationMode != Settings.Secure.LOCATION_MODE_OFF
            }
            return true
        }

        fun isMarshmallowOrAbove(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }

        /**
         * Location enabled is required on some phones running Android Marshmallow or newer (for example on Nexus and Pixel devices).
         *
         * @return false if it is known that location is not required, true otherwise
         */


        /**
         * When a Bluetooth LE packet is received while Location is disabled it means that Location
         * is not required on this device in order to scan for LE devices. This is a case of Samsung phones, for example.
         * Save this information for the future to keep the Location info hidden.
         *
         * @param context the context
         */
//        fun markLocationNotRequired(context: Context?) {
//            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//            preferences.edit().putBoolean(PREFS_LOCATION_NOT_REQUIRED, false).apply()
//        }
//
//        fun getServiceData(result: ScanResult,
//                           serviceUuid: UUID): ByteArray {
//            val scanRecord = result.scanRecord
//            return scanRecord!!.getServiceData(ParcelUuid(serviceUuid))!!
//        }

        /**
         * Checks for required permissions.
         *
         * @return true if permissions are already granted, false otherwise.
         */
        fun isLocationPermissionsGranted(context: Context?): Boolean {
            return ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * Returns true if location permission has been requested at least twice and
         * user denied it, and checked 'Don't ask again'.
         *
         * @param activity the activity
         * @return true if permission has been denied and the popup will not come up any more, false otherwise
         */
        fun isLocationPermissionDeniedForever(activity: Activity, value: Boolean): Boolean {
//            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            return (!isLocationPermissionsGranted(activity) // Location permission must be denied
                    && value // Permission must have been requested before
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) // This method should return false
        }

        /**
         * The first time an app requests a permission there is no 'Don't ask again' checkbox and
         * [ActivityCompat.shouldShowRequestPermissionRationale] returns false.
         * This situation is similar to a permission being denied forever, so to distinguish both cases
         * a flag needs to be saved.
         *
         * @param context the context
         */
//        fun markLocationPermissionRequested(context: Context?) {
//            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//            preferences.edit().putBoolean(PREFS_PERMISSION_REQUESTED, true).apply()
//        }

    }
}