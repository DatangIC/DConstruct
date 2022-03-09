package com.datangic.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.datangic.data.database.table.User
import com.datangic.data.database.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao : BaseDao<User> {

    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM User LIMIT 1")
    fun getUser(): Flow<User?>

}
