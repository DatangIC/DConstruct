package com.datangic.smartlock.respositorys

import android.content.Context
import androidx.lifecycle.*
import com.datangic.smartlock.R
import com.datangic.data.SecretCodeMap
import com.datangic.data.SystemSettings
import com.datangic.data.database.AppDatabase
import com.datangic.data.database.DeviceUserWithChildUsers
import com.datangic.data.database.dao.BaseDao
import com.datangic.data.database.table.Device
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.table.DeviceLog
import com.datangic.data.database.table.DeviceUser
import com.datangic.data.database.view.ViewDeviceKey
import com.datangic.data.database.view.ViewDeviceLog
import com.datangic.data.database.view.ViewDeviceStatus
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.data.datastore.LocalDataStore
import com.datangic.smartlock.preference.SharePreferenceUtils
import com.datangic.smartlock.utils.AppExecutors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ObsoleteCoroutinesApi
class DatabaseRepository(val context: Context) {

    private val TAG = DatabaseRepository::class.simpleName

    @ObsoleteCoroutinesApi
    private val singerThread = newSingleThreadContext("Database")

    val appDatabase = AppDatabase.getInstance(context)

    val dataStore = LocalDataStore.getInstance(context)

    var mViewManagerDevices: List<ViewManagerDevice> = mutableListOf()
        private set

    val mViewManagerDevicesLiveData: LiveData<List<ViewManagerDevice>> by lazy {
        appDatabase.deviceDao().getManagerDevices().apply {
            GlobalScope.launch(Dispatchers.IO) {
                this@apply.collect {
                    mViewManagerDevices = it
                }
            }
        }.asLiveData()
    }

    val mDefaultViewDevice: ViewManagerDevice?
        get() {
            var mTemp: ViewManagerDevice? = null
            SharePreferenceUtils.getDefaultDevice(context)?.let { pair ->
                mViewManagerDevices.forEach {
                    if (pair.first == it.serialNumber) {
                        mTemp = it
                        return@let
                    }
                }
                if (mTemp == null && mViewManagerDevices.isNotEmpty()) mTemp = mViewManagerDevices[0]
            }
            return mTemp
        }

    var mSecretCodeMap: SecretCodeMap = SecretCodeMap.getDefaultInstance()
        private set

    val mDefaultSecretCode: String
        get() = mSecretCodeMap.secretCodeMap[mSecretCodeMap.default] ?: ""

    val mSystemSettingsLiveData: LiveData<SystemSettings> by lazy {
        dataStore.mSystemSettingsFlow.asLiveData()
    }


    init {
        dataStore.mSecretCodeFlow.asLiveData(singerThread).observeForever {
            mSecretCodeMap = it
        }
    }

    fun setManagerDevicesViewObserver(lifecycleOwner: LifecycleOwner?, observer: Observer<List<ViewManagerDevice>>) {
        if (lifecycleOwner == null) {
            mViewManagerDevicesLiveData.observeForever(observer)
        } else {
            mViewManagerDevicesLiveData.observe(lifecycleOwner, observer)
        }
    }

    fun isDeviceRepeat(macAddress: String): Boolean {
        mViewManagerDevices.forEach { device ->
            return device.macAddress == macAddress
        }
        return false
    }


    suspend fun getNewDeviceName(): String {
        val count = appDatabase.deviceDao().getCount()
        var name = context.getString(R.string.default_device_name)
        for (i in 1..count + 3) {
            if (appDatabase.deviceDao().getNameCount(name) != 0) {
                name = context.getString(R.string.default_device_name) + i
            } else {
                break
            }
        }
        return name
    }

    fun getNewUserName(userID: Int, userType: Byte = 0.toByte()): String {
        return if (userType == 0.toByte()) {
            when (userID) {
                in 0..5 -> context.getString(R.string.default_admin_name) + userID
                else -> context.getString(R.string.default_user_name) + userID
            }
        } else {
            when (userType) {
                1.toByte() -> context.getString(R.string.default_admin_name) + userID
                else -> context.getString(R.string.default_user_name) + userID
            }
        }
    }

    fun getDeviceKeyName(type: DeviceEnum.KeyType, id: Int = 0): String {
        return when (type) {
            DeviceEnum.KeyType.PASSWORD -> context.getString(R.string.default_key_password_name)
            DeviceEnum.KeyType.TEMPORARY_PASSWORD -> context.getString(R.string.default_key_temp_password_name) + if (id != 0) id else ""
            DeviceEnum.KeyType.FINGERPRINT -> context.getString(R.string.default_key_fingerprint_name) + if (id != 0) id else ""
            DeviceEnum.KeyType.NFC -> context.getString(R.string.default_key_nfc_name)
            DeviceEnum.KeyType.FACE -> context.getString(R.string.default_key_face_name)
            DeviceEnum.KeyType.UNKNOWN -> context.getString(R.string.default_key_password_name)
            DeviceEnum.KeyType.SEIZED_FINGERPRINT -> context.getString(R.string.default_key_fingerprint_name) + if (id != 0) id else ""
        }
    }
    /**
     * Default Device
     */
    /**================Shared Default Device=======**/
    fun setDefaultDeviceInfo(serialNumber: String, macAddress: String) {
        if (mDefaultDeviceInfo?.first == serialNumber && mDefaultDeviceInfo?.second == macAddress) return
        SharePreferenceUtils.saveDefaultDevice(context, Pair(serialNumber, macAddress))
        mDefaultDeviceInfoLiveData.postValue(Pair(serialNumber, macAddress))
    }

    /**
     * first Serial Number
     * Second Mac Address
     */
    val mDefaultDeviceInfo
        get() = SharePreferenceUtils.getDefaultDevice(context)

    val mDefaultDeviceInfoLiveData: MutableLiveData<Pair<String, String>> = MutableLiveData(mDefaultDeviceInfo)

    /**============= Database ==============**/

    private fun getMarkPair(serialNumber: String?, macAddress: String?): Pair<String, String>? {
        return if (serialNumber != null && macAddress != null) {
            Pair(serialNumber, macAddress)
        } else {
            mDefaultViewDevice?.let {
                Pair(it.serialNumber, it.macAddress)
            } ?: let {
                null
            }
        }
    }

    fun <T> update(item: T) {
        when (item) {
            is Device -> appDatabase.deviceDao()
            is DeviceUser -> appDatabase.deviceUserDao()
            is DeviceLog -> appDatabase.deviceLogDao()
            else -> null
        }?.let {
            appDatabase.update(it as BaseDao<T>, item)
        }
    }

    fun <T> insert(item: T) {
        when (item) {
            is Device -> {
                SharePreferenceUtils.saveDefaultDevice(context, Pair(item.serialNumber, item.macAddress))
                appDatabase.deviceDao()
            }
            is DeviceUser -> appDatabase.deviceUserDao()
            is DeviceLog -> appDatabase.deviceLogDao()
            else -> null
        }?.let {
            appDatabase.insert(it as BaseDao<T>, item)
        }
    }

    //Device

    fun getDeviceByMac(macAddress: String) = appDatabase.getDeviceByMac(macAddress)

    fun getDeviceLiveData(serialNumber: String? = null, macAddress: String? = null): LiveData<Device> {
        return getMarkPair(serialNumber, macAddress)?.let {
            appDatabase.getDeviceLiveData(it.first, it.second)
        } ?: MutableLiveData()
    }

    fun updateDeviceName(newName: String, serialNumber: String? = null, macAddress: String? = null) {
        if (serialNumber != null && macAddress != null) {
            appDatabase.getDevice(
                serialNumber, macAddress
            )?.let { device ->
                device.name = newName
                appDatabase.update(appDatabase.deviceDao(), device)
            }
        } else
            mDefaultViewDevice?.let {
                appDatabase.getDevice(
                    it.serialNumber, it.macAddress
                )?.let { device ->
                    device.name = newName
                    appDatabase.update(appDatabase.deviceDao(), device)
                }
            }
    }

    fun deleteDevice(serialNumber: String, macAddress: String) {
        appDatabase.deleteDevice(serialNumber, macAddress)
    }

    // Device Logs
    fun getDeviceLogsLiveData(userId: Int = 0, serialNumber: String? = null, macAddress: String? = null): LiveData<List<ViewDeviceLog>> {
        return getMarkPair(serialNumber, macAddress)?.let {
            appDatabase.getDeviceLogsLiveData(it.first, it.second, userId)
        } ?: MutableLiveData()
    }

    fun deleteDeviceLog(userId: Int, logId: Int, logState: DeviceEnum.LogState, serialNumber: String?, macAddress: String?) {
        getMarkPair(serialNumber, macAddress)?.let {
            appDatabase.deleteDeviceLog(it.first, it.second, userId, logId, logState)
        }
    }
    // User

    fun getUserWithChildUsersLiveData(userID: Int, serialNumber: String? = null, macAddress: String? = null): LiveData<DeviceUserWithChildUsers> {
        return getMarkPair(serialNumber, macAddress)?.let {
            appDatabase.getUserWithChildUsersLiveData(it.first, it.second, userID)
        } ?: MutableLiveData()
    }

    fun getDeviceUserLiveData(userID: Int, serialNumber: String? = null, macAddress: String? = null): LiveData<DeviceUser?> {
        return getMarkPair(serialNumber, macAddress)?.let {
            appDatabase.getDeviceUserLiveData(it.first, it.second, userID)
        } ?: MutableLiveData()
    }


    fun updateDeviceUserName(newName: String, userID: Int, serialNumber: String? = null, macAddress: String? = null) {
        if (serialNumber != null && macAddress != null) {
            appDatabase.getDeviceUser(
                serialNumber, macAddress, userID
            )?.let { deviceUser ->
                deviceUser.deviceUsername = newName
                appDatabase.update(appDatabase.deviceUserDao(), deviceUser)
            }
        } else
            mDefaultViewDevice?.let {
                appDatabase.getDeviceUser(
                    it.serialNumber, it.macAddress, userID
                )?.let { deviceUser ->
                    deviceUser.deviceUsername = newName
                    appDatabase.update(appDatabase.deviceUserDao(), deviceUser)
                }
            }
    }

    //View of Device Keys
    fun getViewDeviceKeysLiveData(
        userID: Int,
        types: List<DeviceEnum.KeyType>,
        serialNumber: String?,
        macAddress: String?
    ): LiveData<List<ViewDeviceKey>> {
        return getMarkPair(serialNumber, macAddress)?.let {
            appDatabase.getViewDeviceKeysLiveData(
                it.first, it.second,
                Pair(userID, it.first),
                types
            )
        } ?: MutableLiveData()
    }

    fun updateDeviceKeyName(userID: Int, keyId: Int, keyType: DeviceEnum.KeyType, newName: String) {
        mDefaultViewDevice?.let {
            appDatabase.getDeviceKey(
                keyType, it.serialNumber, it.macAddress,
                Pair(userID, it.serialNumber),
                keyId
            )?.let { deviceKey ->
                deviceKey.keyName = newName
                appDatabase.update(appDatabase.deviceKeyDao(), deviceKey)
            }
        }
    }

    // View of Device Status
    val mViewDevicesStatusLiveData: LiveData<ViewDeviceStatus>
        get() {
            return mDefaultViewDevice?.let {
                appDatabase.getViewDevicesStatusLiveData(it.serialNumber, it.macAddress)
            } ?: MutableLiveData()
        }
}
