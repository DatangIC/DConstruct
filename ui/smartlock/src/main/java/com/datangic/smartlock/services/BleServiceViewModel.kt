package com.datangic.smartlock.services

import androidx.lifecycle.ViewModel
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import kotlinx.coroutines.ObsoleteCoroutinesApi

class BleServiceViewModel @ObsoleteCoroutinesApi constructor(val mBleManagerApi:BleManagerApiRepository):ViewModel()