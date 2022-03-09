package com.datangic.smartlock.viewModels

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.datangic.smartlock.R
import com.datangic.smartlock.request.ApiHttp
import com.datangic.smartlock.request.LockRequest
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class UpdateSoftwareViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    bleManagerApiRepository: BleManagerApiRepository
) : BaseViewModel(application, bleManagerApiRepository) {
    open val TAG = UpdateSoftwareViewModel::class.simpleName
    val mMapProgress: MutableLiveData<MutableMap<String, Float>> = MutableLiveData()
    val mState: MutableLiveData<Int> = MutableLiveData<Int>(R.string.download)
//    var mBleDevice: BluetoothDevice? = null
    var mSendingIndex = 0
    var mTotalSize: Int = 0
    var mSendNext: (() -> Unit)? = null
    protected val mOtaParser = ParseFile()
    var downloadMap: Map<String, String> = HashMap()
    fun startDownload(pathList: Map<String, String>) {
        mState.postValue(R.string.downloading)
        downloadMap = pathList
        for (i in pathList) {
            LockFile.getSoftwareFile(i.key)?.let { file ->
                if (file.length() <= 1000) {
                    GlobalScope.launch {
                        ApiHttp.enqueue(
                            LockRequest.downloadForHttp(
                                i.value,
                                file,
                                downloadProgress
                            )
                        )
                    }
                } else {
                    downloadProgress(file.length().toInt(), file.length(), file.name)
                }
            }
        }
    }

    var downloadProgressMap: MutableMap<String, Float> = HashMap()
    private val downloadProgress = fun(curLength: Int, countLength: Long, filename: String) {
        downloadProgressMap[filename] = (curLength.toFloat() / countLength) * 100.0F
        mMapProgress.postValue(downloadProgressMap)
        viewModelScope.launch {
            if (curLength.toLong() == countLength) {
                downloadProgressMap[filename] = 100F
                var done = true
                for (i in downloadProgressMap.values) {
                    if (i != 100F) {
                        done = false
                        break
                    }
                }
                if (done) {
                    mState.postValue(R.string.update)
                    downloadProgressMap.clear()
                }
            }
        }
    }
}