package com.datangic.libs.base.services

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import androidx.core.app.JobIntentService


@SuppressLint("SpecifyJobSchedulerIdRange")
class NetworkBinderService : JobIntentService() {
    fun onStartJob(params: JobParameters?): Boolean {
        return true
    }

    fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    override fun onHandleWork(intent: Intent) {
        TODO("Not yet implemented")
    }

}