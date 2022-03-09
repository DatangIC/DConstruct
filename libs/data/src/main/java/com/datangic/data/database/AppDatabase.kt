package com.datangic.data.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.datangic.data.database.view.ViewDeviceKey
import com.datangic.data.database.view.ViewDeviceLog
import com.datangic.data.database.view.ViewDeviceStatus
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.data.database.dao.*
import com.datangic.data.database.table.*
import java.util.concurrent.Executors

@Database(
    entities = [
        User::class,
        Device::class,
        DeviceStatus::class,
        DeviceLog::class,
        DeviceKey::class,
        DeviceUser::class,
        TemporaryPassword::class],
    views = [
        ViewManagerDevice::class,
        ViewDeviceStatus::class,
        ViewDeviceKey::class,
        ViewDeviceLog::class],
    version = 1
)
@TypeConverters(LockTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceStatusDao(): DeviceStatusDao
    abstract fun deviceLogDao(): DeviceLogDao
    abstract fun deviceKeyDao(): DeviceKeyDao
    abstract fun deviceUserDao(): DeviceUserDao
    abstract fun temporaryPassword(): TemporaryPasswordDao


    private val databaseExecutor = Executors.newFixedThreadPool(5)

    companion object {
        private val TAG = AppDatabase::class.simpleName
        const val DATABASE_NAME = "datang"

        // For Singleton instantiation
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }


    // Device
    fun getDeviceLiveData(serialNumber: String, macAddress: String): LiveData<Device> {
        return databaseExecutor.submit<LiveData<Device>> {
            deviceDao().getDeviceAsLiveData(serialNumber, macAddress)
        }.get()
    }

    fun getDeviceByMac(macAddress: String): Device? {
        return databaseExecutor.submit<Device?> { deviceDao().getDeviceByMac(macAddress) }.get()
    }

    fun getDevice(serialNumber: String, macAddress: String): Device? {
        return databaseExecutor.submit<Device?> { deviceDao().getDevice(serialNumber, macAddress) }.get()
    }

    fun deleteDevice(serialNumber: String, macAddress: String) {
        databaseExecutor.submit { deviceDao().deleteDevice(serialNumber, macAddress) }
    }

    // Device Status
    fun getViewDevicesStatusLiveData(serialNumber: String, macAddress: String): LiveData<ViewDeviceStatus> {
        return databaseExecutor.submit<LiveData<ViewDeviceStatus>> { deviceStatusDao().getViewDeviceStatusLiveData(serialNumber, macAddress) }
            .get()
    }

    // Device Keys
    fun getViewDeviceKeysLiveData(
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        types: List<DeviceEnum.KeyType>
    ): LiveData<List<ViewDeviceKey>> {
        return databaseExecutor.submit<LiveData<List<ViewDeviceKey>>> {
            deviceKeyDao().getViewDeviceKeys(
                serialNumber,
                macAddress,
                deviceUserId,
                types
            )
        }.get()
    }

    fun getDeviceKey(
        keyType: DeviceEnum.KeyType,
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        keyId: Int
    ): DeviceKey? {
        return databaseExecutor.submit<DeviceKey> {
            deviceKeyDao().getDeviceKey(keyType, serialNumber, macAddress, deviceUserId, keyId)
        }.get()
    }

    // Device Logs

    fun getDeviceLogsLiveData(serialNumber: String, macAddress: String, userId: Int = 0): LiveData<List<ViewDeviceLog>> {
        return if (userId == 0) {
            databaseExecutor.submit<LiveData<List<ViewDeviceLog>>> {
                deviceLogDao().getViewDeviceLogsAsLiveData(serialNumber, macAddress)
            }.get()
        } else {
            databaseExecutor.submit<LiveData<List<ViewDeviceLog>>> {
                deviceLogDao().getViewDeviceLogsAsLiveData(serialNumber, macAddress, Pair(userId, serialNumber))
            }.get()
        }
    }

    fun deleteDeviceLog(serialNumber: String, macAddress: String, userId: Int, logId: Int, logState: DeviceEnum.LogState) {
        databaseExecutor.submit {
            deviceLogDao().deleteDeviceLog(serialNumber, macAddress, Pair(userId, serialNumber), logId, logState)
        }
    }

    // User

    fun getUserWithChildUsersLiveData(serialNumber: String, macAddress: String, userId: Int): LiveData<DeviceUserWithChildUsers> {
        return databaseExecutor.submit<LiveData<DeviceUserWithChildUsers>> {
            deviceUserDao().getDeviceUserWithChildAsLiveData(
                serialNumber,
                macAddress,
                Pair(userId, serialNumber)
            )
        }.get()
    }

    fun getDeviceUser(serialNumber: String, macAddress: String, userId: Int): DeviceUser? {
        return databaseExecutor.submit<DeviceUser> {
            deviceUserDao().getDeviceUser(
                serialNumber, macAddress, Pair(userId, serialNumber)
            )
        }.get()
    }

    fun getDeviceUserLiveData(serialNumber: String, macAddress: String, userId: Int): LiveData<DeviceUser?> {
        return databaseExecutor.submit<LiveData<DeviceUser?>> {
            deviceUserDao().getDeviceUserLiveData(serialNumber, macAddress, Pair(userId, serialNumber))
        }.get()
    }

    fun <T> update(dao: BaseDao<T>, key: T) {
        databaseExecutor.submit { dao.update(key) }
    }

    fun <T> insert(dao: BaseDao<T>, key: T) {
        databaseExecutor.submit { dao.insert(key) }
    }

    fun <T> delete(dao: BaseDao<T>, key: T) {
        databaseExecutor.submit { dao.delete(key) }
    }

}
