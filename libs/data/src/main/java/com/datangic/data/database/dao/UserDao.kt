package com.datangic.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.datangic.common.utils.Logger
import com.datangic.data.database.table.User
import com.datangic.data.database.dao.BaseDao
import com.datangic.data.database.table.DeviceKey
import com.datangic.data.database.table.UserWithDevice
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao : BaseDao<User> {

    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Transaction
    @Query("SELECT * FROM User WHERE user_id=(:userId) LIMIT 1")
    fun getUserWithDevice(userId: Long): Flow<UserWithDevice>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM User WHERE user_id =(:userId)LIMIT 1")
    fun getUser(userId: Long = 0): User?

    @Query("SELECT * FROM User WHERE user_id =(:userId)LIMIT 1")
    fun getUserWithFlow(userId: Long = 0): Flow<User>?

    fun insertOrUpdate(item: User) {
        val itemDB = getUser(item.userId)
        if (itemDB == null) {
            insert(item)
        } else {
            itemDB.apply {
                this.avatar = item.avatar
                this.phone = item.phone
                this.nickname = item.nickname
            }
            update(itemDB)
        }
    }

}
