package com.datangic.libs.base.dataSource

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.datangic.api.login.LoginApi

object UserNetWork {
    var mLoginApi: LoginApi? = null

    class LoginWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {
        override suspend fun doWork(): Result {
            return Result.success()
        }
    }
}