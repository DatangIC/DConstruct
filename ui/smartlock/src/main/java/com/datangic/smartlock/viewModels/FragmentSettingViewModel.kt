package com.datangic.smartlock.viewModels

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.BottomSelectorAdapter
import com.datangic.smartlock.adapter.SettingItemAdapter
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.components.LockStatusItem
import com.datangic.smartlock.components.SwitchItem
import com.datangic.smartlock.components.SelectorItem
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.view.ViewDeviceStatus
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.ui.setting.SettingFragmentDirections
import com.datangic.smartlock.utils.*
import com.datangic.smartlock.utils.UtilsFormat.toTimeString

import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentSettingViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    bleManagerApiRepository: BleManagerApiRepository
) : BaseViewModel(application, bleManagerApiRepository) {

    private val TAG = FragmentSettingViewModel::class.simpleName

    private val mViewDeviceStatusLiveDate: LiveData<ViewDeviceStatus> = mBleManagerApi.mViewDevicesStatusLiveData

    var mBackPress: (() -> Unit)? = null

    private var mResetDialog: AlertDialog? = null

    private val mDialogClick = object : (SelectorItem) -> Unit {
        override fun invoke(selectorItem: SelectorItem) {
            if (!isConnectedWithDialog(macAddress)) return
            if (!selectorItem.selected) {
                when (selectorItem.itemName) {
                    R.string.bottom_dialog_volume_mute -> CreateMessage.createMessage49(macAddress, MSG49_TYPE_SetVolume, MSG49_VALUE_VolumeMute)
                        .execute()
                    R.string.bottom_dialog_volume_low -> CreateMessage.createMessage49(macAddress, MSG49_TYPE_SetVolume, MSG49_VALUE_VolumeLow)
                        .execute()
                    R.string.bottom_dialog_volume_middle -> CreateMessage.createMessage49(
                        macAddress,
                        MSG49_TYPE_SetVolume,
                        MSG49_VALUE_VolumeMiddle
                    ).execute()
                    R.string.bottom_dialog_volume_high -> CreateMessage.createMessage49(macAddress, MSG49_TYPE_SetVolume, MSG49_VALUE_VolumeHigh)
                        .execute()
                    R.string.bottom_dialog_language_only_chinese ->
                        CreateMessage.createMessage49(macAddress, MSG49_TYPE_SetLanguage, MSG49_VALUE_LanguageOnlyChinese).execute()
                    R.string.bottom_dialog_language_chinese -> CreateMessage.createMessage49(
                        macAddress,
                        MSG49_TYPE_SetLanguage,
                        MSG49_VALUE_LanguageChinese
                    ).execute()
                    R.string.bottom_dialog_language_english -> CreateMessage.createMessage49(
                        macAddress,
                        MSG49_TYPE_SetLanguage,
                        MSG49_VALUE_LanguageEnglish
                    ).execute()

                    R.string.bottom_dialog_unlock_period_5 -> CreateMessage.createMessage1D(macAddress, MSG1D_UnlockPeriod5).execute()

                    R.string.bottom_dialog_unlock_period_8 -> CreateMessage.createMessage1D(macAddress, MSG1D_UnlockPeriod8).execute()

                    R.string.bottom_dialog_unlock_period_10 -> CreateMessage.createMessage1D(macAddress, MSG1D_UnlockPeriod10).execute()

                    R.string.bottom_dialog_unlock_period_15 -> CreateMessage.createMessage1D(macAddress, MSG1D_UnlockPeriod15).execute()

                    R.string.bottom_dialog_unlock_period_30 -> CreateMessage.createMessage1D(macAddress, MSG1D_UnlockPeriod30).execute()

                    R.string.setting_nfc_encrypted_and_ordinary -> CreateMessage.createMessage19(macAddress, MSG19_SecurityCardEnable).execute()
                    R.string.setting_nfc_encrypted -> CreateMessage.createMessage19(macAddress, MSG19_SecurityCardUnable).execute()
                }
            }
        }
    }

    private fun mSettingOnClick(fragment: Fragment) = object : SettingItemAdapter.OnSettingItemListener {
        override fun onClick(systemItem: Any) {
            if (!isConnectedWithDialog(macAddress)) return
            when (systemItem) {
                is SwitchItem -> when (systemItem.itemName) {
                    R.string.setting_combination_unlock ->
                        CreateMessage.createMessage19(macAddress, if (systemItem.checked) 2 else 1).execute()
                    R.string.setting_bluetooth_keep_on ->
                        CreateMessage.createMessage19(
                            macAddress,
                            if (systemItem.checked) MSG19_BluetoothKeepOnUnable else MSG19_BluetoothKeepOnEnable
                        ).execute()
                    R.string.setting_voice_prompts ->
                        CreateMessage.createMessage19(
                            macAddress,
                            if (systemItem.checked) MSG19_VoicePromptsUnable else MSG19_VoicePromptsEnable
                        ).execute()
                    R.string.setting_anti_prizing_alarm ->
                        CreateMessage.createMessage19(
                            macAddress,
                            if (systemItem.checked) MSG19_AntiPrizingAlarmUnable else MSG19_AntiPrizingAlarmEnable
                        ).execute()
                    R.string.setting_lock_keep_open ->
                        CreateMessage.createMessage19(
                            macAddress,
                            if (systemItem.checked) MSG19_LockKeepOpenUnable else MSG19_LockKeepOpenEnable
                        ).execute()
                    R.string.setting_automatic_closing ->
                        CreateMessage.createMessage19(
                            macAddress,
                            if (systemItem.checked) MSG19_AutomaticClosingUnable else MSG19_AutomaticClosingEnable
                        ).execute()
                    R.string.setting_lock_cylinder ->
                        CreateMessage.createMessage19(
                            macAddress,
                            if (systemItem.checked) MSG19_LockCylinderUnable else MSG19_LockCylinderEnable
                        ).execute()
                    R.string.setting_follow_doorbell -> CreateMessage.createMessage49(
                        macAddress, MSG49_TYPE_SetDoorbellFollow,
                        if (systemItem.checked) MSG49_VALUE_DoorbellFollowUnable else MSG49_VALUE_DoorbellFollowUnable
                    ).execute()
                    R.string.setting_infrared ->
                        CreateMessage.createMessage19(macAddress, if (systemItem.checked) MSG19_InfraredUnable else MSG19_InfraredEnable)
                            .execute()
                    R.string.setting_magic_number ->
                        CreateMessage.createMessage19(macAddress, if (systemItem.checked) MSG19_MagicNumberUnable else MSG19_MagicNumberEnable)
                            .execute()
                    R.string.setting_temp_pwd ->
                        CreateMessage.createMessage19(macAddress, if (systemItem.checked) MSG19_TempPwdUnable else MSG19_TempPwdEnable)
                            .execute()
                    R.string.setting_wifi_power -> {
                        CreateMessage.createMessage49(
                            macAddress,
                            MSG49_TYPE_SetWifiPower,
                            if (systemItem.checked) MSG49_VALUE_WifiUnable else MSG49_VALUE_WifiEnable
                        ).execute()
                    }
                    else -> return
                }
                is LockStatusItem -> when (systemItem.itemName) {
                    R.string.setting_language -> {
                        getLanguageDialog(fragment.requireContext()).show()
                    }
                    R.string.setting_power_saving_period ->
                        mViewDeviceStatusLiveDate.value?.let { it1 ->
                            MaterialDialog.getTimePeriodPickerDialog(
                                fragment.requireContext(),
                                it1.powerSavingStartAt,
                                it1.powerSavingEndAt
                            ) { start, end ->
                                if (start != it1.powerSavingStartAt || end != it1.powerSavingEndAt) {
                                    CreateMessage.createMessage2D(macAddress, start, end).execute()
                                }
                            }.show()
                        }
                    R.string.setting_volume_adjustment -> {
                        getVolumeDialog(fragment.requireContext()).show()
                    }
                    R.string.setting_unlock_period -> {
                        getUnlockPeriodDialog(fragment.requireContext()).show()
                    }
                    R.string.setting_lock_status -> {
                        CreateMessage.createMessage49(macAddress, MSG49_TYPE_GetLockStatus, MSG49_VALUE_Default).execute()
                    }
                    R.string.setting_nfc -> {
                        getNfcDialog(fragment.requireContext()).show()
                    }
                    R.string.setting_wifi_status -> {
                        CreateMessage.createMessage49(macAddress, MSG49_TYPE_GetWifiStatus, MSG49_VALUE_Default).execute()
                    }
                    else -> return
                }
                R.string.setting_self_check_repair -> {
                    val action = SettingFragmentDirections.actionSettingFragmentToCheckRepairFragment(macAddress, serialNumber)
                    findNavController(fragment).navigate(action)
                }
                R.string.setting_version_information -> {
                    val action = SettingFragmentDirections.actionSettingFragmentToVersionInfoFragment(macAddress, serialNumber)
                    findNavController(fragment).navigate(action)
                }
                R.string.setting_version_update -> {
                    val action = SettingFragmentDirections.actionSettingFragmentToUpgradeNavigation(macAddress, serialNumber)
                    findNavController(fragment).navigate(action)
                }
                R.string.setting_restore_factory_settings -> {
                    MaterialDialog.getDelayDialog(
                        fragment.requireContext(),
                        title = R.string.setting_restore_factory_settings,
                        message = R.string.setting_restore_factory_tips,
                        delay = 5,
                        action = object : MaterialDialog.OnMaterialAlterDialogListener {
                            override fun onCancel() {}

                            override fun onConfirm() {
                                CreateMessage.createMessage19(macAddress, MSG19_RestoreFactorySettings).execute()
                                mResetDialog = MaterialDialog.getLoadingDialog(
                                    fragment.requireContext(),
                                    R.string.setting_restore_factory_settings,
                                    isCancel = false
                                )
                                mResetDialog?.show()
                                mHandler.postDelayed({ mResetDialog?.cancel() }, DIALOG_TIMEOUT)
                            }
                        }

                    ).show()
                }
                R.string.setting_wifi_setting -> {
                    val action = SettingFragmentDirections.actionSettingFragmentToWifiFragment(macAddress, serialNumber)
                    findNavController(fragment).navigate(action)
                }
                else -> return
            }
        }
    }

    fun getVolumeDialog(context: Context): BottomSheetDialog {
        val dialog = MaterialDialog.getBottomSheetDialogWithLayout(context, R.layout.bottom_sheet_menu, R.string.bottom_dialog_volume_selector)
        val mSelectorItemList = listOf(
            SelectorItem(R.string.bottom_dialog_volume_mute),
            SelectorItem(R.string.bottom_dialog_volume_low),
            SelectorItem(R.string.bottom_dialog_volume_middle),
            SelectorItem(R.string.bottom_dialog_volume_high)
        )
        mViewDeviceStatusLiveDate.value?.let {
            if (it.volume in listOf(0, 1, 2, 3))
                mSelectorItemList[it.volume].selected = true
        }
        val adapter = BottomSelectorAdapter(mSelectorItemList) {
            mDialogClick(it)
            dialog.cancel()
        }
        dialog.findViewById<RecyclerView>(R.id.dialog_recycle_view)?.adapter = adapter
        return dialog
    }

    private fun getLanguageDialog(context: Context): BottomSheetDialog {
        val dialog = MaterialDialog.getBottomSheetDialogWithLayout(context, R.layout.bottom_sheet_menu, R.string.bottom_dialog_language_selector)
        val mSelectorItemList = listOf(
            SelectorItem(R.string.bottom_dialog_language_only_chinese),
            SelectorItem(R.string.bottom_dialog_language_chinese),
            SelectorItem(R.string.bottom_dialog_language_english)
        )
        mViewDeviceStatusLiveDate.value?.let {
            when (it.language) {
                DeviceEnum.LockLanguage.CHINESE_ONLY -> mSelectorItemList[0].selected = true
                DeviceEnum.LockLanguage.CHINESE -> mSelectorItemList[1].selected = true
                DeviceEnum.LockLanguage.ENGLISH -> mSelectorItemList[2].selected = true
            }
        }
        val adapter = BottomSelectorAdapter(mSelectorItemList) {
            mDialogClick(it)
            dialog.cancel()
        }
        dialog.findViewById<RecyclerView>(R.id.dialog_recycle_view)?.adapter = adapter
        return dialog
    }

    fun getNfcDialog(context: Context): BottomSheetDialog {
        val dialog = MaterialDialog.getBottomSheetDialogWithLayout(context, R.layout.bottom_sheet_menu, R.string.setting_nfc)
        val mSelectorItemList = listOf(
            SelectorItem(R.string.setting_nfc_encrypted),
            SelectorItem(R.string.setting_nfc_encrypted_and_ordinary),
        )
        mViewDeviceStatusLiveDate.value?.let {
            when (it.nfcType) {
                DeviceEnum.NfcType.NORMAL -> mSelectorItemList[0].selected = true
                DeviceEnum.NfcType.ENCRYPTION -> mSelectorItemList[1].selected = true
            }
        }
        val adapter = BottomSelectorAdapter(mSelectorItemList) {
            mDialogClick(it)
            dialog.cancel()
        }
        dialog.findViewById<RecyclerView>(R.id.dialog_recycle_view)?.adapter = adapter
        return dialog
    }

    private fun getUnlockPeriodDialog(context: Context): BottomSheetDialog {
        val dialog =
            MaterialDialog.getBottomSheetDialogWithLayout(context, R.layout.bottom_sheet_menu, R.string.bottom_dialog_unlock_period_selector)
        val mSelectorItemList = ArrayList<SelectorItem>()
        mViewDeviceStatusLiveDate.value?.let { it ->
            when (it.unlockPeriod) {
                in listOf(5, 8, 10) -> {
                    mSelectorItemList.add(SelectorItem(R.string.bottom_dialog_unlock_period_5).apply {
                        selected = it.unlockPeriod == 5
                    })
                    mSelectorItemList.add(SelectorItem(R.string.bottom_dialog_unlock_period_8).apply {
                        selected = it.unlockPeriod == 8
                    })
                    mSelectorItemList.add(SelectorItem(R.string.bottom_dialog_unlock_period_10).apply {
                        selected = it.unlockPeriod == 10
                    })
                }
                in listOf(15, 30) -> {
                    mSelectorItemList.add(SelectorItem(R.string.bottom_dialog_unlock_period_15).apply {
                        selected = it.unlockPeriod == 15
                    })
                    mSelectorItemList.add(SelectorItem(R.string.bottom_dialog_unlock_period_30).apply {
                        selected = it.unlockPeriod == 30
                    })
                }
                else -> {
                }
            }
        }
        val adapter = BottomSelectorAdapter(mSelectorItemList) {
            mDialogClick(it)
            dialog.cancel()
        }
        dialog.findViewById<RecyclerView>(R.id.dialog_recycle_view)?.adapter = adapter
        return dialog
    }

    fun setObserve(fragment: Fragment, setting: RecyclerView) {
        mViewDeviceStatusLiveDate.observe(fragment.viewLifecycleOwner) {
            if (it == null) return@observe
            val list = arrayListOf<Any>(
                SwitchItem(R.string.setting_combination_unlock, it.enableCombinationLock),
                SwitchItem(R.string.setting_bluetooth_keep_on, it.enableBluetoothKeepOn),
                SwitchItem(R.string.setting_voice_prompts, it.enableVoice),
                SwitchItem(R.string.setting_anti_prizing_alarm, it.enableAntiPrizingAlarm)
            )
            if (it.hasLockKeepOpen)
                list.add(SwitchItem(R.string.setting_lock_keep_open, it.enableLockKeepOpen))
            if (it.hasAutomaticLock && !it.hasSelfEjectLock)
                list.add(SwitchItem(R.string.setting_automatic_closing, it.enableAutomaticClosing))
            if (it.hasLockCylinder && it.isAdministrator)
                list.add(SwitchItem(R.string.setting_lock_cylinder, it.enableLockCylinder))
            if (it.hasFollowDoorbell)
                list.add(SwitchItem(R.string.setting_follow_doorbell, it.enableDoorbell))
            if (it.hasInfrared && it.isAdministrator)
                list.add(SwitchItem(R.string.setting_infrared, it.enableInfrared))
            if (it.hasMagicNumber && it.isAdministrator)
                list.add(SwitchItem(R.string.setting_magic_number, it.enableMagicNumber))
            if (it.hasTemporaryPassword && it.isAdministrator)
                list.add(SwitchItem(R.string.setting_temp_pwd, it.enableTemporaryPassword))
            list.add(SettingItemAdapter.NULL_TYPE) // 间隔符
            list.add(
                LockStatusItem(
                    R.string.setting_power_saving_period,
                    if (it.powerSavingEndAt == 0) R.string.close else String.format(
                        fragment.getString(R.string.setting_power_saving_period_status),
                        it.powerSavingStartAt.toTimeString(),
                        it.powerSavingEndAt.toTimeString()
                    )
                )
            )
            if (it.hasVolumeAdjustment)
                list.add(
                    LockStatusItem(
                        R.string.setting_volume_adjustment, when (it.volume) {
                            0 -> R.string.bottom_dialog_volume_mute
                            1 -> R.string.bottom_dialog_volume_low
                            2 -> R.string.bottom_dialog_volume_middle
                            3 -> R.string.bottom_dialog_volume_high
                            else -> R.string.bottom_dialog_volume_mute
                        }
                    )
                )
            if (!it.hasAutomaticLock || !it.hasSelfEjectLock)
                list.add(
                    LockStatusItem(
                        R.string.setting_unlock_period,
                        String.format(fragment.getString(R.string.setting_unlock_period_time), it.unlockPeriod)
                    )
                )
            if (it.hasStatusQuery)
                list.add(
                    LockStatusItem(
                        R.string.setting_lock_status,
                        if (it.isOpening) R.string.setting_lock_status_open else R.string.setting_lock_status_close
                    )
                )
            if (it.hasLanguageSwitch)
                list.add(
                    LockStatusItem(
                        R.string.setting_language,
                        when (it.language) {
                            DeviceEnum.LockLanguage.CHINESE_ONLY -> R.string.setting_language_chinese_only
                            DeviceEnum.LockLanguage.CHINESE -> R.string.setting_language_chinese
                            DeviceEnum.LockLanguage.ENGLISH -> R.string.setting_language_english
                        }
                    )
                )
            if (!it.hasFace && it.isAdministrator)
                list.add(
                    LockStatusItem(
                        R.string.setting_nfc,
                        when (it.nfcType) {
                            DeviceEnum.NfcType.NORMAL -> R.string.setting_nfc_encrypted
                            DeviceEnum.NfcType.ENCRYPTION -> R.string.setting_nfc_encrypted_and_ordinary
                        }
                    )
                )

            if (it.hasWifi && it.isAdministrator) {
                list.add(SettingItemAdapter.NULL_TYPE) // 间隔符
                list.add(SwitchItem(R.string.setting_wifi_power, it.enableWifi))
                list.add(
                    LockStatusItem(
                        R.string.setting_wifi_status, when (it.wifiStatus) {
                            DeviceEnum.WifiStatus.NOT_SETTING -> R.string.setting_wifi_status_not_setting
                            DeviceEnum.WifiStatus.ROUTER_NOT_CONNECTED -> R.string.setting_wifi_status_router_dis_connected
                            DeviceEnum.WifiStatus.SERVER_NOT_CONNECTED -> R.string.setting_wifi_status_server_dis_connected
                            DeviceEnum.WifiStatus.SERVER_CONNECTED -> R.string.setting_wifi_status_server_connected
                            DeviceEnum.WifiStatus.ERROR -> R.string.setting_wifi_status_error
                        }
                    )
                )
                list.add(R.string.setting_wifi_setting)
            }

            list.add(SettingItemAdapter.NULL_TYPE)
            list.add(R.string.setting_self_check_repair)
            list.add(R.string.setting_version_information)
            if (it.isAdministrator) {
                list.add(R.string.setting_version_update)
                list.add(R.string.setting_restore_factory_settings)
            }
            list.add(SettingItemAdapter.BOTTOM_TYPE)
            if (setting.adapter == null) {
                setting.adapter = SettingItemAdapter(list).apply {
                    setOnSettingItemListener(mSettingOnClick(fragment))
                }
            } else {
                (setting.adapter as SettingItemAdapter).submitList(list)
            }
        }
    }

    override fun handle1E(errorCode: Int) {
        super.handle1E(errorCode)
        if (errorCode == MSG1E_RestoreFactorySettingsDone) {
            mResetDialog?.cancel()
            mBackPress?.let {
                it()
            }
        }
    }

}