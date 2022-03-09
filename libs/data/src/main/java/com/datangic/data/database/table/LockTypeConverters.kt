package com.datangic.data.database.table

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class LockTypeConverters {
    /**
     * Device
     */
    @TypeConverter
    fun getLockTypeFromInt(value: Int): DeviceEnum.LockType {
        return DeviceEnum.getLockType(value)
    }

    @TypeConverter
    fun storeLockTypeToInt(type: DeviceEnum.LockType): Int {
        return DeviceEnum.setLockType(type)
    }

    @TypeConverter
    fun getFaceVersionFromString(value: String): Map<DeviceEnum.FaceVersion, String> {
        val addedAppKeys: Type = object : TypeToken<Map<DeviceEnum.FaceVersion, String>>() {}.type
        return Gson().fromJson(value, addedAppKeys)
    }

    @TypeConverter
    fun storeFaceVersionToString(value: Map<DeviceEnum.FaceVersion, String>): String {
        return Gson().toJson(value)
    }

    /**
     * DeviceStatus
     */
    @TypeConverter
    fun getWifiStatusFromInt(value: Int): DeviceEnum.WifiStatus {
        return DeviceEnum.getWifiStatus(value)
    }

    @TypeConverter
    fun storeWifiStatusToInt(type: DeviceEnum.WifiStatus): Int {
        return DeviceEnum.setWifiStatus(type)
    }

    @TypeConverter
    fun getLockLanguageTypeFromInt(value: Int): DeviceEnum.LockLanguage {
        return DeviceEnum.getLockLanguage(value)
    }

    @TypeConverter
    fun storeLockLanguageTypeToInt(type: DeviceEnum.LockLanguage): Int {
        return DeviceEnum.setLockLanguage(type)
    }

    @TypeConverter
    fun getEnableNfcTypeFromInt(value: Int): DeviceEnum.NfcType {
        return DeviceEnum.getNfcType(value)
    }

    @TypeConverter
    fun storeEnableNfcTypeToInt(type: DeviceEnum.NfcType): Int {
        return DeviceEnum.setNfcType(type)
    }

    /**
     * Device Key
     */
    @TypeConverter
    fun getKeyTypeFromInt(value: Int): DeviceEnum.KeyType {
        return DeviceEnum.getKeyType(value)
    }

    @TypeConverter
    fun storeKeyTypeToInt(type: DeviceEnum.KeyType): Int {
        return DeviceEnum.setKeyType(type)
    }

    /**
     * DeviceLog
     */
    @TypeConverter
    fun getLogUnlockTypeFromInt(value: Int): DeviceEnum.UnlockType {
        return DeviceEnum.getUnlockLogType(value)
    }

    @TypeConverter
    fun storeLogUnlockTypeToInt(type: DeviceEnum.UnlockType): Int {
        return DeviceEnum.setUnlockLogType(type)
    }

    @TypeConverter
    fun getLogStateFromInt(value: Int): DeviceEnum.LogState {
        return DeviceEnum.getLogState(value)
    }

    @TypeConverter
    fun storeLogStateToInt(type: DeviceEnum.LogState): Int {
        return DeviceEnum.setLogState(type)
    }

    /**
     * DeviceUser
     */
    @TypeConverter
    fun getUserStatusFromInt(value: Int): DeviceEnum.DeviceUserStatus {
        return DeviceEnum.getDeviceUserStatus(value)
    }

    @TypeConverter
    fun storeUserStatusToInt(status: DeviceEnum.DeviceUserStatus): Int {
        return DeviceEnum.setDeviceUserStatus(status)
    }


    @TypeConverter
    fun getListString(value: String): List<Int> {
        val list: Type = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, list)
    }

    @TypeConverter
    fun storeListToString(value: List<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun getAllUserMapFromString(value: String): Map<Int, Int> {
        val allUserMap: Type = object : TypeToken<Map<Int, Int>>() {}.type
        return Gson().fromJson(value, allUserMap)
    }

    @TypeConverter
    fun storeAllUserMapToString(value: Map<Int, Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun getPairFromString(value: String): Pair<Int, String> {
        val pair: Type = object : TypeToken<Pair<Int, String>>() {}.type
        return Gson().fromJson(value, pair)
    }

    @TypeConverter
    fun storePairString(value: Pair<Int, String>): String {
        return Gson().toJson(value)
    }

}