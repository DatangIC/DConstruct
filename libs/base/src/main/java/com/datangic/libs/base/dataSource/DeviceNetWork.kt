package com.datangic.libs.base.dataSource

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

object DeviceNetWork {

    class SynDeviceInfo(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
        override fun doWork(): Result {

            return Result.success()
        }

    }
}