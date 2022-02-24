package com.datangic.login.di

import com.datangic.libs.base.ApplicationProvider

class LoginApplicationProvider : ApplicationProvider() {
    init {
        moduleList.add(LoginApplication())
    }
}