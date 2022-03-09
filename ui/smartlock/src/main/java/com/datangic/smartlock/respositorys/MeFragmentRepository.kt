package com.datangic.smartlock.respositorys

import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.SystemItemsAdapter
import com.datangic.smartlock.components.SystemItem

class MeFragmentRepository {
    val mSettingItemList = listOf(
            SystemItem(R.drawable.ic_setting_lock, R.string.device_management, SystemItemsAdapter.SYSTEM_ITEM_NEXT),
            SystemItem(R.drawable.ic_setting, R.string.system_settings, SystemItemsAdapter.SYSTEM_ITEM_NEXT),
            SystemItem(R.drawable.ic_secret, R.string.select_secret_code, SystemItemsAdapter.SYSTEM_ITEM_NEXT),
            0,
            SystemItem(R.drawable.ic_about_us, R.string.about_us, SystemItemsAdapter.SYSTEM_ITEM_NEXT),
    )
}