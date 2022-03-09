package com.datangic.data.database

import androidx.room.Dao
import com.datangic.data.database.TemporaryPassword
import com.datangic.data.database.dao.BaseDao

@Dao
interface TemporaryPasswordDao : BaseDao<TemporaryPassword>
