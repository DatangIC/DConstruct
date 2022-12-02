package com.datangic.data

import android.app.Application
import androidx.lifecycle.*
import com.datangic.common.file.SharePreferenceUtils
import com.datangic.common.utils.Logger
import com.datangic.data.database.AppDatabase
import com.datangic.data.database.DeviceUserWithChildUsers
import com.datangic.data.database.dao.BaseDao
import com.datangic.data.database.table.*
import com.datangic.data.database.view.ViewDeviceKey
import com.datangic.data.database.view.ViewDeviceLog
import com.datangic.data.database.view.ViewDeviceStatus
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.data.datastore.LocalDataStore
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class DatabaseRepository(val context: Application) {

    private val TAG = DatabaseRepository::class.simpleName

    private val singerThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    val mDatabase = AppDatabase.getInstance(context)

    val mDataStore = LocalDataStore.getInstance(context)

    val mViewManagerDevicesLiveData: MutableLiveData<List<ViewManagerDevice>?> = liveData(singerThread) {
        mDataStore.mUserPrivateInfoFlow.collect { user ->
            if (user.userId != 0) {
                if (user.userId != mLogUser.value?.userId || this.latestValue == null)
                    MainScope().launch(Dispatchers.IO) {
                        mDatabase.deviceDao().getManagerDevices(user.userId).collect {
                            mViewManagerDevices = it
                            emit(it)
                        }
                    }
            } else {
                emit(null)
            }
        }
    } as MutableLiveData<List<ViewManagerDevice>?>

    private val mLogUser: MutableLiveData<LogUser> = MutableLiveData<LogUser>(LogUser())

    var mViewManagerDevices: List<ViewManagerDevice> = mutableListOf()
        private set

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
        mDataStore.mSystemSettingsFlow.asLiveData()
    }


    init {
        mDataStore.mSecretCodeFlow.asLiveData(singerThread).observeForever {
            mSecretCodeMap = it
        }
    }


    fun updateLogUser(logUser: LogUser) {
        mLogUser.postValue(logUser)
    }

    fun updateLogUserStatus(status: LogStatus) {
        mLogUser.postValue(mLogUser.value?.apply {
            this.status = status
        })
    }

    fun getLogUser() = mLogUser.value

    fun setLogUserObservable(lifecycleOwner: LifecycleOwner?, observer: Observer<LogUser>) {
        if (lifecycleOwner == null) {
            mLogUser.observeForever(observer)
        } else {
            mLogUser.observe(lifecycleOwner, observer)
        }
    }

    fun setManagerDevicesViewObserver(lifecycleOwner: LifecycleOwner?, observer: Observer<List<ViewManagerDevice>?>) {
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
        val count = mDatabase.deviceDao().getCount()
        var name = context.getString(R.string.default_device_name)
        for (i in 1..count + 3) {
            if (mDatabase.deviceDao().getNameCount(name) != 0) {
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

    fun <T> update(item: T, dirty: Boolean = true) {
        when (item) {
            is Device -> {
                item.dirty = dirty
                mDatabase.deviceDao()
            }
            is DeviceUser -> {
                item.dirty = dirty
                mDatabase.deviceUserDao()
            }
            is DeviceLog -> mDatabase.deviceLogDao()
            else -> null
        }?.let {
            mDatabase.update(it as BaseDao<T>, item)
        }
    }

    fun <T> insert(item: T) {
        when (item) {
            is Device -> {
                SharePreferenceUtils.saveDefaultDevice(context, Pair(item.serialNumber, item.macAddress))
                mDatabase.deviceDao()
            }
            is DeviceUser -> mDatabase.deviceUserDao()
            is DeviceLog -> mDatabase.deviceLogDao()
            else -> null
        }?.let {
            mDatabase.insert(it as BaseDao<T>, item)
        }
    }

    fun synUpdate(serialNumber: String, macAddress: String, deviceUserId: Int, dirty: Boolean = true) {
        mDatabase.getDeviceUser(serialNumber, macAddress, deviceUserId)?.let { deviceUser -> update(deviceUser, dirty) }
        mDatabase.getDevice(serialNumber, macAddress)?.let { device -> update(device, dirty) }
    }

    //Device

    fun getDeviceByMac(macAddress: String) = mDatabase.getDeviceByMac(macAddress)

    fun getDeviceLiveData(serialNumber: String? = null, macAddress: String? = null): LiveData<Device> {
        return getMarkPair(serialNumber, macAddress)?.let {
            mDatabase.getDeviceLiveData(it.first, it.second)
        } ?: MutableLiveData()
    }

    fun updateDeviceName(newName: String, serialNumber: String? = null, macAddress: String? = null) {
        if (serialNumber != null && macAddress != null) {
            mDatabase.getDevice(
                serialNumber, macAddress
            )?.let { device ->
                device.name = newName
                mDatabase.update(mDatabase.deviceDao(), device)
            }
        } else mDefaultViewDevice?.let {
            mDatabase.getDevice(
                it.serialNumber, it.macAddress
            )?.let { device ->
                device.name = newName
                mDatabase.update(mDatabase.deviceDao(), device)
            }
        }
    }

    fun deleteDevice(serialNumber: String, macAddress: String) {
        mDatabase.deleteDevice(serialNumber, macAddress)
    }

    // Device Logs
    fun getDeviceLogsLiveData(userId: Int = 0, serialNumber: String? = null, macAddress: String? = null): LiveData<List<ViewDeviceLog>> {
        return getMarkPair(serialNumber, macAddress)?.let {
            mDatabase.getDeviceLogsLiveData(it.first, it.second, userId)
        } ?: MutableLiveData()
    }

    fun deleteDeviceLog(userId: Int, logId: Int, logState: DeviceEnum.LogState, serialNumber: String?, macAddress: String?) {
        getMarkPair(serialNumber, macAddress)?.let {
            mDatabase.deleteDeviceLog(it.first, it.second, userId, logId, logState)
        }
    }
    // User

    fun getUserWithChildUsersLiveData(
        userID: Int, serialNumber: String? = null, macAddress: String? = null
    ): LiveData<DeviceUserWithChildUsers> {
        return getMarkPair(serialNumber, macAddress)?.let {
            mDatabase.getUserWithChildUsersLiveData(it.first, it.second, userID)
        } ?: MutableLiveData()
    }

    fun getDeviceUserLiveData(userID: Int, serialNumber: String? = null, macAddress: String? = null): LiveData<DeviceUser?> {
        return getMarkPair(serialNumber, macAddress)?.let {
            mDatabase.getDeviceUserLiveData(it.first, it.second, userID)
        } ?: MutableLiveData()
    }


    fun updateDeviceUserName(newName: String, userID: Int, serialNumber: String? = null, macAddress: String? = null) {
        if (serialNumber != null && macAddress != null) {
            mDatabase.getDeviceUser(
                serialNumber, macAddress, userID
            )?.let { deviceUser ->
                deviceUser.deviceUsername = newName
                mDatabase.update(mDatabase.deviceUserDao(), deviceUser)
            }
        } else mDefaultViewDevice?.let {
            mDatabase.getDeviceUser(
                it.serialNumber, it.macAddress, userID
            )?.let { deviceUser ->
                deviceUser.deviceUsername = newName
                mDatabase.update(mDatabase.deviceUserDao(), deviceUser)
            }
        }
    }

    //View of Device Keys
    fun getViewDeviceKeysLiveData(
        userID: Int, types: List<DeviceEnum.KeyType>, serialNumber: String?, macAddress: String?
    ): LiveData<List<ViewDeviceKey>> {
        return getMarkPair(serialNumber, macAddress)?.let {
            mDatabase.getViewDeviceKeysLiveData(
                it.first, it.second, Pair(userID, it.first), types
            )
        } ?: MutableLiveData()
    }

    fun updateDeviceKeyName(userID: Int, keyId: Int, keyType: DeviceEnum.KeyType, newName: String) {
        mDefaultViewDevice?.let {
            mDatabase.getDeviceKey(
                keyType, it.serialNumber, it.macAddress, Pair(userID, it.serialNumber), keyId
            )?.let { deviceKey ->
                deviceKey.keyName = newName
                mDatabase.update(mDatabase.deviceKeyDao(), deviceKey)
            }
        }
    }

    // View of Device Status
    val mViewDevicesStatusLiveData: LiveData<ViewDeviceStatus>
        get() {
            return mDefaultViewDevice?.let {
                mDatabase.getViewDevicesStatusLiveData(it.serialNumber, it.macAddress)
            } ?: MutableLiveData()
        }
}
