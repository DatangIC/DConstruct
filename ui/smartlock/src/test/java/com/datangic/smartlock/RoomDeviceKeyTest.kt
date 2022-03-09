package com.datangic.smartlock

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.datangic.common.database.AppDatabase
import com.datangic.common.database.dao.DeviceKeyDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException


class RoomDeviceKeyTest {
    private lateinit var deviceKeyDao: DeviceKeyDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java).build()
        deviceKeyDao = db.deviceKeyDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
    }

}