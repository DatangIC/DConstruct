package com.datangic.smartlock.di

import com.datangic.smartlock.respositorys.*
import com.datangic.smartlock.viewModels.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.koin.androidApplication
//import com.datangic.smartlock.viewModels.FragmentScanningViewModel
//import com.datangic.smartlock.viewModels.FragmentVerifyingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module


@ObsoleteCoroutinesApi
val repositoryModule: Module = module {

    single { DatabaseRepository(androidContext()) }

    factory { ToolbarRepository() }

    single { LocalPasswordRepository(get()) }

    factory { ScannerRepository(androidContext()) }

    factory { (macAddress: String, serialNumber: String) -> UpdateSoftwareRepository(macAddress, serialNumber) }

    // Ble
    single { BleManagerApiRepository(androidContext(), get(), get()) }

    factory { HomeFragmentRepository() }
    factory { MeFragmentRepository() }

    factory { SystemFragmentRepository(get()) }

}
val viewModelModule: Module = module {
    viewModel { FragmentHomeViewModel(androidApplication(), get(), get()) }
    viewModel { FragmentMeViewModel(androidApplication(), get(), get()) }
    viewModel { (macAddress: String, serialNumber: String) -> FragmentSettingViewModel(androidApplication(), macAddress, serialNumber, get()) }
    viewModel { (macAddress: String, serialNumber: String) -> FragmentCheckRepairViewModel(androidApplication(), macAddress, serialNumber, get()) }
    viewModel { (macAddress: String, serialNumber: String) -> FragmentVersionInfoViewModel(androidApplication(), macAddress, serialNumber, get()) }
    viewModel { (macAddress: String, serialNumber: String) -> FragmentUpdateViewModel(androidApplication(), macAddress, serialNumber, get()) }
    viewModel { (macAddress: String, serialNumber: String) -> FragmentUpdateLockViewModel(androidApplication(), macAddress, serialNumber, get()) }
    viewModel { (macAddress: String, serialNumber: String) -> FragmentUpdateFaceViewModel(androidApplication(), macAddress, serialNumber, get()) }
    viewModel { (macAddress: String, serialNumber: String) -> FragmentWifiViewModel(androidApplication(), macAddress, serialNumber, get()) }


    viewModel { FragmentScanningViewModel(androidApplication(), get()) }
    viewModel { FragmentVerifyingViewModel(androidApplication(), get()) }

    viewModel { (macAddress: String, serialNumber: String, userID: Int, hasNFC: Boolean, hasFace: Boolean) ->
        FragmentManagerKeysViewModel(androidApplication(), macAddress, serialNumber, userID, hasNFC, hasFace, get())
    }
    viewModel { (macAddress: String, serialNumber: String, userID: Int) ->
        FragmentManagerTempPwdViewModel(androidApplication(), macAddress, serialNumber, userID, get())
    }
    viewModel { (macAddress: String, serialNumber: String, userID: Int) ->
        FragmentRecordViewModel(androidApplication(), macAddress, serialNumber, userID, get())
    }
    viewModel { (macAddress: String, serialNumber: String, userID: Int) ->
        FragmentManagerUserViewModel(androidApplication(), macAddress, serialNumber, userID, get())
    }
    viewModel { (macAddress: String, serialNumber: String, userID: Int) ->
        FragmentLifecycleViewModel(androidApplication(), macAddress, serialNumber, userID, get())
    }


    viewModel { FragmentDeviceViewModel(androidApplication(), get()) }
    viewModel { FragmentSystemViewModel(androidApplication(), get(), get()) }

}
