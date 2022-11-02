package com.datangic.smartlock.viewModels

import android.app.Application
import androidx.fragment.app.Fragment
import com.datangic.common.file.LockFile
import com.datangic.common.utils.Logger
import com.datangic.smartlock.R
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.*

class FragmentUpdateLockViewModel(application: Application, macAddress: String, serialNumber: String, mBleManagerApi: BleManagerApiRepository) :
    UpdateSoftwareViewModel(application, macAddress, serialNumber, mBleManagerApi) {
    var mMSG51Type: Byte = 0.toByte()
    override val TAG = FragmentUpdateLockViewModel::class.simpleName

    fun startUpgrade(fragment: Fragment, type: String, filenameList: List<String>, sha1: String? = null) {
        this.mMSG51Type = when (type) {
            UPGRADE_TYPE_LOCK -> MSG51_LockFirmware
            UPGRADE_TYPE_FINGERPRINT -> MSG51_FingerprintFirmware
            UPGRADE_TYPE_BACK_PANEL -> MSG51_BackPanelFirmware
            else -> 0.toByte()
        }
        mOtaParser.mProgressLiveData.observe(fragment.viewLifecycleOwner) { pair ->
            if (pair.second >= 100F) {
                mMapProgress.postValue(mutableMapOf(filenameList[0] to 100F))
            } else {
                mMapProgress.postValue(mutableMapOf(filenameList[0] to pair.second))
            }
        }
        LockFile.getSoftwareFile(filenameList[0])?.let {
            mOtaParser.setFile(it, sha1 = sha1)
            mTotalSize = it.length().toInt() + if (sha1.isNullOrEmpty()) 0 else 36
            CreateMessage.createMessage51StartOTA(
                macAddress, mMSG51Type,
                size = mTotalSize
            ).execute()
        }
        mBleManagerApi.getSendSuccessCallbackLiveData().observe(fragment.viewLifecycleOwner) {
            mSendNext?.let { it1 ->
                it1()
            }
        }

    }

    override fun handle5E(type: Byte, errorCode: Int) {
        super.handle5E(type, errorCode)
        Logger.e(TAG, "type=$type\n errorCode = $errorCode")
        if (type == MSG5E_Firmware) {
            when (errorCode) {
                2, 128, 16 -> {
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
                1 -> {
                    mState.postValue(R.string.update_failed)
                    mOtaParser.clear()
                    mSendNext = null
                }
            }
        } else {
            if (errorCode == 0) {
                mState.postValue(R.string.update_successful)
            } else if (errorCode == 1) {
                mState.postValue(R.string.update_failed)
            }
            mSendNext = null
        }
    }


    private fun sendNext() {
        CreateMessage.createMessage51(
            macAddress,
            mMSG51Type,
            mSendingIndex++,
            mOtaParser.getNextPacket()
        ).execute()
    }

    private fun sendStop() {
        mSendNext = null
        CreateMessage.createMessage51EndOTA(
            macAddress, mMSG51Type,
            mTotalSize
        ).execute()
    }

}