package com.datangic.libs.base.work

import android.content.Context
import android.util.Log
import androidx.work.*

class SleepWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 1L
    }

    override suspend fun doWork(): Result {
        val firstUpdate = workDataOf(Progress to 0)
        Thread.sleep(1000)
        val lastUpdate = workDataOf(Progress to 100)
        setProgress(firstUpdate)
        val STE = inputData.getInt("STE", 0)
        Log.e("SleepWorker", "Ste=$STE")
        val data = Data.Builder().put("set", STE).build()
        return Result.success(data)
    }
}