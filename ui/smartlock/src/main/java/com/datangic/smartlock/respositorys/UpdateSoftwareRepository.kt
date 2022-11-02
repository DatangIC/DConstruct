package com.datangic.smartlock.respositorys

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import com.datangic.smartlock.R
import com.datangic.smartlock.request.ApiHttp
import com.datangic.smartlock.request.LockRequest
import com.datangic.common.file.LockFile
import com.datangic.smartlock.utils.ParseFile
import com.datangic.smartlock.viewModels.UpdateSoftwareViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateSoftwareRepository(val macAddress: String, val serialNumber: String) {
    val TAG = UpdateSoftwareViewModel::class.simpleName
    val mMapProgress: MutableLiveData<MutableMap<String, Float>> = MutableLiveData()
    val mState: MutableLiveData<Int> = MutableLiveData<Int>(R.string.download)
    var mBleDevice: BluetoothDevice? = null
    var mSendingIndex = 0
    var mTotalSize: Int = 0
    var mSendNext: ((BluetoothDevice) -> Unit)? = null
    val mOtaParser = ParseFile()
    var downloadMap: Map<String, String> = HashMap()
    fun startDownload(pathList: Map<String, String>) {
        mState.postValue(R.string.downloading)
        downloadMap = pathList
        for (i in pathList) {
            LockFile.getSoftwareFile(i.key)?.let {
                GlobalScope.launch {
                    ApiHttp.enqueue(LockRequest.downloadForHttp(
                            i.value,
                            it,
                            downloadProgress
                    ))
                }
            }
        }
    }

    var downloadProgressMap: MutableMap<String, Float> = HashMap()
    private val downloadProgress = fun(curLength: Int, countLength: Long, filename: String) {
        downloadProgressMap[filename] = (curLength.toFloat() / countLength) * 100.0F
        mMapProgress.postValue(downloadProgressMap)
        GlobalScope.launch {
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