package com.datangic.smartlock.ble

import android.bluetooth.BluetoothDevice
import cn.dttsh.dts1586.DTS1586
import cn.dttsh.dts1586.MSG
import cn.dttsh.dts1586.MSG12
import com.datangic.smartlock.R
import com.datangic.data.DatabaseRepository
import com.datangic.common.utils.Logger

/**
 * 该函数用于处理系列操作，如，msg01-msg04的消息组册，OTA等
 */
class MessageOperation(mDatabaseRepository: DatabaseRepository, execute: (BluetoothDevice, MSG) -> CreateMessage.State) :
    ReceivedMessageHandle(mDatabaseRepository, execute) {
    private val TAG = MessageOperation::class.simpleName

    fun register(device: BluetoothDevice, deviceInfo: Triple<String, String, String>? = null, shareUser: Pair<Int, String>? = null) {
        Logger.e(TAG, "type=${mRegisterManagers[device.address]}")
        when (mRegisterManagers[device.address]) {
            RegisterType.SCAN_REGISTER -> {
                register(RegisterType.SCAN_REGISTER, device)
            }
            RegisterType.NORMAL_REGISTER -> {
                mDatabase.mDatabase.deviceDao().getManagerDevices(device.address)?.let {
                    register(
                        RegisterType.NORMAL_REGISTER,
                        device,
                        it.deviceUserID.first,
                        it.authCode,
                        it.secretCode
                    )
                }
            }
            RegisterType.SHARE_REGISTER -> {
                Logger.e(TAG, "ShareCode")
                shareUser?.let { user ->
                    Logger.e(TAG, "ShareCode2")
                    register(RegisterType.SHARE_REGISTER, device, useID = user.first, authCode = user.second)
                }
            }
            RegisterType.CALLBACK_REGISTER -> register(RegisterType.CALLBACK_REGISTER, device)
            RegisterType.SET_INFORMATION -> {
                deviceInfo?.let {
                    register(
                        RegisterType.SET_INFORMATION,
                        device,
                        sn = deviceInfo.first,
                        mac = deviceInfo.second,
                        nodeID = deviceInfo.third
                    )
                }
            }
            RegisterType.SET_SECRET_CODE -> {
                register(RegisterType.SET_SECRET_CODE, device)
            }
            else -> {
                shareUser?.let { user ->
                    Logger.e(TAG, "ShareCode2")
                    register(RegisterType.SHARE_REGISTER, device, useID = user.first, authCode = user.second)
                }
                deviceInfo?.let {
                    register(
                        RegisterType.SET_INFORMATION,
                        device,
                        sn = deviceInfo.first,
                        mac = deviceInfo.second,
                        nodeID = deviceInfo.third
                    )
                }
            }
        }
    }


    fun register(
        type: RegisterType, device: BluetoothDevice,
        useID: Int = 0,
        authCode: String? = null,
        secretCode: String? = null,
        mac: String = "",
        sn: String = "",
        nodeID: String = ""
    ) {
        when (type) {
            RegisterType.SCAN_REGISTER -> registerByScan(device, secretCode)

            RegisterType.NORMAL_REGISTER -> registerByNormal(device, useID, authCode, secretCode)

            RegisterType.SHARE_REGISTER -> registerByNormal(device, useID, authCode, mDatabase.mDefaultSecretCode)

            RegisterType.CALLBACK_REGISTER -> {
                registerByRetrieve(device, useID, authCode, secretCode)
            }

            RegisterType.SET_INFORMATION -> {
                mSendMessage(device, CreateMessage.createMessage05(device.address, mac, sn, nodeID).second)
            }

            RegisterType.SET_SECRET_CODE -> {
                mSendMessage(
                    device, CreateMessage.createMessage07(
                        device.address,
                        if (secretCode?.length == 10) secretCode else mDatabase.mDefaultSecretCode
                    ).second
                )
            }
        }
        mRegisterManagers[device.address] = type
    }

    init {

    }

    private fun registerByScan(device: BluetoothDevice, secretCode: String?) {
        val secretCode1 = if (secretCode?.length != 10) {
            mDatabase.mDefaultSecretCode
        } else {
            secretCode
        }
        mSendMessage(device, CreateMessage.createMessage01(device.address, 0, 0, secretCode = secretCode1).second)
    }

    private fun registerByNormal(device: BluetoothDevice, userID: Int, authCode: String? = null, secretCode: String? = null) {
        mSendMessage(device, CreateMessage.createMessage01(device.address, 1, userID, authCode = authCode, secretCode = secretCode).second)
    }

    private fun registerByRetrieve(device: BluetoothDevice, userID: Int, authCode: String? = null, secretCode: String? = null) {
        val secretCode1 = if (secretCode?.length != 10) {
            mDatabase.mDefaultSecretCode
        } else {
            secretCode
        }
        mSendMessage(device, CreateMessage.createMessage01(device.address, 2, userID, authCode = authCode, secretCode = secretCode1).second)
    }

    override fun message04(data: Triple<Int, MSG, BluetoothDevice>) {
        super.message04(data)
        when (mRegisterManagers[data.third.address]) {
            RegisterType.SCAN_REGISTER -> mSendMessage(data.third, CreateMessage.createMessage11(data.third.address, 0, 0).second)
            RegisterType.NORMAL_REGISTER, RegisterType.CALLBACK_REGISTER, RegisterType.SHARE_REGISTER -> mSendMessage(
                data.third, CreateMessage.createMessage25(
                    data.third.address,
                    DTS1586.getCmdInfo(data.first).userID
                ).second
            )
            else -> {
            }
        }
    }

    override fun message0E(data: Triple<Int, MSG, BluetoothDevice>) {
        Logger.v(TAG, "MSG0E ErrorCode = ${data.second.errCode}")
        when (mRegisterManagers[data.third.address]) {
            RegisterType.SET_INFORMATION, RegisterType.SET_SECRET_CODE -> {
                Logger.v(TAG, "MSG0E errorCode = ${data.second.errCode}")
            }
            else -> {
            }
        }
        val error: Int? = when (data.second.errCode) {
            0x01 -> R.string._0e_0x01_
            0x02 -> R.string._0e_0x02_
            0x03 -> R.string._0e_0x03_
            0x04 -> R.string._0e_0x04_
            0x05 -> R.string._0e_0x05_
            0x06 -> R.string._0e_0x06_
            0x07 -> R.string._0e_0x07_
            0x08 -> R.string._0e_0x08_
            0x09 -> R.string._0e_0x09_
            0x0a -> R.string._0e_0x0a_
            else -> null
        }
        if (error != null) {
            Logger.e(TAG, "MSG 0E Dialog")
        }
    }

    override fun message12(data: Triple<Int, MSG, BluetoothDevice>) {
        super.message12(data)
        if (data.second is MSG12) {
            val msg12 = data.second as MSG12
            data.third.let {
//                mSendMessage.sendMessage25(it, msg12.userID)
                when (mRegisterManagers[data.third.address]) {
                    RegisterType.SCAN_REGISTER -> {
                        mSendMessage(it, CreateMessage.createMessage25(it.address, msg12.userID).second)
                    }
                    else -> {
                    }
                }
            }
        }
    }
}