package com.datangic.smartlock.components

import com.datangic.data.database.table.DeviceEnum

data class DeviceKeyItem(val icon: Int, val name: Any, val keyId: Int = 0, val type: DeviceEnum.KeyType = DeviceEnum.KeyType.UNKNOWN, val value: String = "", val syncAt: String = "")
