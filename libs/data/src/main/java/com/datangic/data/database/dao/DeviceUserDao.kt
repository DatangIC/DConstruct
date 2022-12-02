package com.datangic.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.datangic.data.database.DeviceUserWithChildUsers
import com.datangic.data.database.table.DeviceUser

@Dao
interface DeviceUserDao : BaseDao<DeviceUser> {

    @Query("SELECT * FROM DeviceUser WHERE serial_number=:serialNumber and mac_address = (:macAddress) and device_user_id=:userID LIMIT 1")
    fun getDeviceUser(serialNumber: String, macAddress: String, userID: Pair<Int, String>): DeviceUser?

    @Query("SELECT * FROM DeviceUser WHERE serial_number=:serialNumber and mac_address = (:macAddress) and device_user_id=:userID and parent_user_id= :parentId LIMIT 1")
    fun getChildDeviceUser(serialNumber: String, macAddress: String, userID: Pair<Int, String>, parentId: Pair<Int, String>): DeviceUser?

    @Query("SELECT * FROM DeviceUser WHERE serial_number=:serialNumber and mac_address = (:macAddress) and device_user_id=:userID LIMIT 1")
    fun getDeviceUserLiveData(serialNumber: String, macAddress: String, userID: Pair<Int, String>): LiveData<DeviceUser?>

    @Transaction
    @Query("SELECT * FROM DeviceUser WHERE serial_number=:serialNumber and mac_address = (:macAddress) and device_user_id=:userID LIMIT 1")
    fun getDeviceUserWithChild(serialNumber: String, macAddress: String, userID: Pair<Int, String>): DeviceUserWithChildUsers?

    @Transaction
    @Query("SELECT * FROM DeviceUser WHERE serial_number=:serialNumber and mac_address = (:macAddress) and device_user_id=:userID ORDER BY device_user_id")
    fun getDeviceUserWithChildAsLiveData(serialNumber: String, macAddress: String, userID: Pair<Int, String>): LiveData<DeviceUserWithChildUsers>

    fun updateOrInsertUser(deviceUser: DeviceUser, dirty: Boolean = true) {
        getChildDeviceUser(deviceUser.serialNumber, deviceUser.macAddress, deviceUser.deviceUserId, deviceUser.parentUserId)?.let { user ->
            user.userStatus = deviceUser.userStatus
            user.dirty = dirty
            update(user)
        } ?: insert(deviceUser)
    }
}