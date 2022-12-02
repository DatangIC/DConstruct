package com.datangic.libs.base

import com.datangic.data.DatabaseRepository
import com.datangic.libs.base.dataSource.DeviceSource
import com.datangic.libs.base.dataSource.UserSource
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

object BaseModule {

    val mRepositoryModule = module {
        single { DatabaseRepository(androidApplication()) }
        single { UserSource(androidApplication(), get()) }
        single { DeviceSource(androidApplication(), get()) }
    }
}