package com.datangic.smartlock.di

import com.datangic.libs.base.ApplicationProvider


class LockApplicationForDebug : ApplicationProvider() {
    init {
        moduleList.add(LockApplication())
    }
}