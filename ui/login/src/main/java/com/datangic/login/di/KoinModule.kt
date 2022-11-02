package com.datangic.login.di

import com.datangic.data.DatabaseRepository
import com.datangic.libs.base.dataSource.UserSource
import com.datangic.login.LoginRepository
import com.datangic.login.LoginViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.single

val repositoryModule: Module = module {

    /**
     * Repository
     */
    single { DatabaseRepository(androidApplication()) }
    single { UserSource(androidApplication(), get()) }
    single { LoginRepository(get()) }
}
val viewModelModule: Module = module {
    viewModel { LoginViewModel(androidApplication(), get()) }

}