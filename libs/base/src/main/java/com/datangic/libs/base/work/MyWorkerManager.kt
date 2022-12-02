package com.datangic.libs.base.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.datangic.api.login.LoginApi
import com.datangic.api.login.LoginApiResource
import com.datangic.api.login.LoginDataResult
import com.datangic.data.LogStatus
import com.datangic.network.RequestStatus
import com.google.gson.reflect.TypeToken

object MyWorkerManager {
    var mLoginApi: LoginApi? = null

}