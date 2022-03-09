package com.datangic.smartlock.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
class BleService:Service() {

//    private val mViewModel: BleServiceViewModel by sharedViewModel()

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}