package com.datangic.common

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.datangic.common.utils.Logger
import java.util.UUID

const val BASE_URL = "https://api.dttsh.cn"
const val UPDATE = "/api/v1.0/firmware/update"

object Config {

    const val FOLDER_LOG = "Log"

    const val SHARE_NAME = "Preference"

    const val DATASTORE_NAME = "Preference"

    const val FOLDER_FIRMWARE = "Firmware"

    const val FOLDER_SHARE_BITMAP = "ShareBitMap"

    const val PACKAGE = "com.datangic.smartlock"

    const val TEMP_PASSWORD_LIMIT = 20

    private val TAG: String = Config::class.simpleName ?: "Config"

    /**
     * BLE Filter
     */
    val FILTER_BLE_NAME = listOf("DTLOCKER", "AILock", "DTWRITER")

    val FILTER_BLE_NAME_WRITE = listOf("DTWRITER")

    val FILTER_BLE_SERVER_DATA_UUID = mapOf(
        UUID.fromString("0000109B-0000-1000-8000-00805F9B34FB") to byteArrayOf(1, 89, 0, 0, 1, 0)
    )

    val FINGERPRINT_CALLBACK = listOf("HXZHJJHPL1")
    val HAS_SERVER_IP = listOf("HXZHJJHPL1")

    // BLE Scanning Time
    val SCAN_DURATION: Long by lazy { 45 * 1000 }

    /**
     * @Description getVersionName
     * @return Version_name
     */
    fun getVersionName(context: Context): String? {
        return try {
            val version = context.packageManager.getPackageInfo(context.packageName, 0)
            version.versionName
        } catch (e: Exception) {
            Logger.e(TAG, "Exception $e")
            null
        }
    }

    /**
     * @Description getVersionCode
     * @Return Version Code
     */
    fun getVersionCode(context: Context): Long? {
        return try {
            val version = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                version.longVersionCode
            } else
                version.versionCode.toLong()
        } catch (e: Exception) {
            Logger.e(TAG, "Exception $e")
            null
        }
    }

    /**
     * @Description 是否是测试版本
     * @return Boolean  Debug
     */
    fun isDebug(context: Context): Boolean {
        return try {
            (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            Logger.e(TAG, "Exception $e")
            false
        }
    }

    /**
     * @Description 是否是usb调试模式
     * @return true 开启调试，false 未开启调试
     */
    fun isUsbDebug(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) > 0
    }

    fun isRelease(context: Context): Boolean {
        return try {
            return context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            ).metaData.getBoolean("isRelease", false)
        } catch (e: Exception) {
            Logger.e(TAG, "Exception $e")
            false
        }
    }
}