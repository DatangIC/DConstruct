package com.datangic.smartlock.viewModels

import android.app.Application
import androidx.fragment.app.Fragment
import cn.dttsh.dts1586.MSG42
import com.datangic.smartlock.R
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.*

class FragmentUpdateFaceViewModel(application: Application, macAddress: String, serialNumber: String, mBleManagerApi: BleManagerApiRepository) :
    UpdateSoftwareViewModel(application, macAddress, serialNumber, mBleManagerApi) {

    override val TAG = FragmentUpdateFaceViewModel::class.simpleName
    private var mFileNameList: Iterator<Map.Entry<Byte, String>>? = null
    private var mUpdatingModel: Map.Entry<Byte, String>? = null
    private val PACKET_SIZE = 495
    private val mProcess: MutableMap<String, Float> = LinkedHashMap()

    fun startFaceUpgrade(fragment: Fragment, filenameList: Map<Byte, String>) {
        mFileNameList = filenameList.entries.iterator()
        Logger.e(TAG, "mFileNameSize=${filenameList.size}")
        mProcess.clear()
        filenameList.values.forEach { path ->
            mProcess[path] = 0F
        }
        mFileNameList?.next()?.let { entry ->
            mOtaParser.mProgressLiveData.observe(fragment.viewLifecycleOwner) { pair ->

                if (pair.second >= 100F) {
                    mProcess[pair.first] = 100F
                } else if (pair.first != "") {
                    mProcess[pair.first] = pair.second
                }
                mMapProgress.postValue(mProcess)
            }
            startOta(entry)
        }
        mBleManagerApi.getSendSuccessCallbackLiveData().observe(fragment.viewLifecycleOwner) {
            mSendNext?.let { it1 ->
                it1()
            }
        }
    }

    override fun handle42(msg: MSG42) {
        Logger.e(TAG, "msg42 type = ${msg.type} rep=${msg.rsp}")
        when (msg.rsp) {
            MSG42_RspOTADone -> {
                mState.postValue(R.string.update_successful)
                mSendNext = null
            }
            MSG42_RspOTAFailed -> {
                mState.postValue(R.string.update_failed)
                mSendNext = null
            }
            MSG42_RspOTAAllow -> {
                if (msg.type.toByte() == mUpdatingModel?.key) {
                    LockFile.getSoftwareFile(mUpdatingModel?.value ?: "")?.let { file ->
                        if (msg.type.toByte() == MSG41_TypeUpgradeModel) {
                            mOtaParser.setFile(file, fileSize = msg.size.toLong(), offset = msg.offset, packetSize = PACKET_SIZE)
                        } else {
                            mOtaParser.setFile(file, packetSize = PACKET_SIZE)
                        }
                        mTotalSize = file.length().toInt()
                        CreateMessage.createMessage41(
                            macAddress, cmd = MSG41_CMDOtaSize,
                            type = msg.type.toByte(),
                            data = mTotalSize
                        ).execute()
                    }
                }
            }
            MSG42_RspOTAGetSizeDone -> {
                if (msg.type.toByte() == MSG41_TypeUpgradeModel) {
                    LockFile.getSoftwareFile(mUpdatingModel?.value ?: "")?.let { file ->
                        mOtaParser.setFile(file, fileSize = msg.size.toLong(), offset = msg.offset, packetSize = PACKET_SIZE)
                    }
                }
                mSendingIndex = 0
                sendNext()
                mState.postValue(R.string.updating)
                mSendNext = fun() {
                    if (mOtaParser.hasNextPacket()) {
                        sendNext()
                    } else {
                        sendStop()
                    }
                }
            }
            MSG42_RspOTAGetFileDone -> {
                if (mFileNameList?.hasNext() == true) {
                    mFileNameList?.next()?.let { entry ->
                        Logger.e(TAG, "Send Failed Done")
                        startOta(entry, true)
                    }
                } else {
                    Logger.e(TAG, "OTA Done")
                    CreateMessage.createMessage41(
                        macAddress, MSG41_CMDUpgradeDone
                    ).execute()
                }
            }
        }
    }

    private fun sendNext() {
        CreateMessage.createMessage43(macAddress, mOtaParser.getNextPacket()).execute()
    }

    private fun sendStop() {
        mSendNext = null
    }

    private fun startOta(entry: Map.Entry<Byte, String>, next: Boolean = false) {
        mUpdatingModel = entry

        if (next) {
            LockFile.getSoftwareFile(mUpdatingModel?.value ?: "")?.let { file ->
                mTotalSize = file.length().toInt()
                mOtaParser.setFile(file, packetSize = PACKET_SIZE)
                CreateMessage.createMessage41(
                    macAddress,
                    cmd = MSG41_CMDOtaSize,
                    type = mUpdatingModel?.key ?: 0.toByte(),
                    data = mTotalSize
                ).execute()
            }
        } else {
            CreateMessage.createMessage41(
                macAddress, cmd = MSG41_CMDFastStartOta,
                type = entry.key,
                data = 0
            ).execute()
        }
    }


}