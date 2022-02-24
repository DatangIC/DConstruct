package com.datangic.login.di

import com.datangic.login.LoginDataSource
import com.datangic.login.LoginRepository
import com.datangic.login.LoginViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val repositoryModule: Module = module {

    /**
     * Repository
     */
    single { LoginDataSource() }
    single { LoginRepository(get()) }
}
val viewModelModule: Module = module {
    viewModel { LoginViewModel(androidApplication(), get()) }

}