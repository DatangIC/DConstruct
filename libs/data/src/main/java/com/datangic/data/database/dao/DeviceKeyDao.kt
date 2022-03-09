package com.datangic.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.view.ViewDeviceKey
import com.datangic.data.database.table.DeviceKey
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceKeyDao : BaseDao<DeviceKey> {

    @Query("SELECT * FROM ViewDeviceKey WHERE serialNumber = (:serialNumber) AND macAddress=(:macAddress) AND deviceUserID=(:deviceUserId) AND keyType in (:types)")
    fun getViewDeviceKeys(
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        types: List<DeviceEnum.KeyType>
    ): LiveData<List<ViewDeviceKey>>

    @Query("SELECT * FROM ViewDeviceKey WHERE serialNumber = (:serialNumber) AND macAddress=(:macAddress) AND deviceUserID=(:deviceUserId) AND keyType=:type ORDER BY createAt DESC")
    fun getViewDeviceTempKeys(
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        type: DeviceEnum.KeyType =
            DeviceEnum.KeyType.TEMPORARY_PASSWORD
    ): Flow<List<ViewDeviceKey>>


    @Query("SELECT * from DeviceKey WHERE serial_number= :serialNumber and mac_address=:macAddress and device_user_id=:deviceUserId and key_type=:keyType and key_lock_id=:keyId LIMIT 1")
    fun getDeviceKey(
        keyType: DeviceEnum.KeyType,
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        keyId: Int
    ): DeviceKey?

    @Query("SELECT * from DeviceKey WHERE serial_number= :serialNumber and mac_address=:macAddress and device_user_id=:deviceUserId and key_type in  (:keyType1,:keyType2) ")
    fun getDeviceFPKeys(
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        keyType1: DeviceEnum.KeyType = DeviceEnum.KeyType.FINGERPRINT,
        keyType2: DeviceEnum.KeyType = DeviceEnum.KeyType.SEIZED_FINGERPRINT,
    ): List<DeviceKey>?

    @Query("DELETE from DeviceKey WHERE serial_number= :serialNumber and mac_address=:macAddress and device_user_id=:deviceUserId and key_type=:keyType ")
    fun deleteByType(
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        keyType: DeviceEnum.KeyType,
    )

    @Query("DELETE from DeviceKey WHERE serial_number= :serialNumber and mac_address=:macAddress and device_user_id=:deviceUserId and key_type=:keyType and key_lock_id = :keyLockId ")
    fun deleteTempPwd(
        serialNumber: String,
        macAddress: String,
        deviceUserId: Pair<Int, String>,
        keyLockId: Int,
        keyType: DeviceEnum.KeyType = DeviceEnum.KeyType.TEMPORARY_PASSWORD
    )


    fun insertOrUpdate(item: DeviceKey, keyName: String) {
        val itemDB = getDeviceKey(item.keyType, item.serialNumber, item.macAddress, item.deviceUserId, item.keyLockId)
        if (itemDB == null) {
            item.keyName = keyName
            item.createAt = System.currentTimeMillis() / 1000
            item.updateAt = System.currentTimeMillis() / 1000
            insert(item)
        } else {
            with(itemDB) {
                this.keyName = this.keyName ?: itemDB.keyName
            }
            item.updateAt = System.currentTimeMillis() / 1000
            update(itemDB)
        }
    }

}