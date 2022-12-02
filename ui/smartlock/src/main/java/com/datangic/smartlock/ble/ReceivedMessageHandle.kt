package com.datangic.smartlock.ble

import android.bluetooth.BluetoothDevice
import cn.dttsh.dts1586.*
import com.datangic.common.utils.Logger
import com.datangic.data.database.*
import com.datangic.data.database.table.*
import com.datangic.smartlock.liveData.LockBleReceivedLiveData
import com.datangic.data.DatabaseRepository
import com.datangic.smartlock.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.nordicsemi.android.ble.utils.ParserUtils
import java.util.*
import kotlin.collections.HashMap
import kotlin.experimental.and

abstract class ReceivedMessageHandle(val mDatabase: DatabaseRepository, val mSendMessage: (BluetoothDevice, MSG) -> CreateMessage.State) {
    private val TAG = ReceivedMessageHandle::class.simpleName

    enum class RegisterType {
        SCAN_REGISTER, NORMAL_REGISTER, SHARE_REGISTER, CALLBACK_REGISTER, SET_INFORMATION, SET_SECRET_CODE
    }

    private data class TempMessage(
        var msg04: MSG04? = null,
        var msg12: MSG12? = null,
        var msg14: MSG14? = null
    )

    val mRegisterManagers: MutableMap<String, RegisterType> by lazy { HashMap() }

    private val mDeviceManagers: MutableMap<BluetoothDevice, DeviceAndDeviceStatus> by lazy { HashMap() }

    private val mNewRegisterDevice: MutableMap<BluetoothDevice, TempMessage> by lazy { HashMap() }

    private val mSendMessageData: MutableMap<BluetoothDevice, MutableMap<Byte, MSG>> = HashMap()

    private val mMessageHandleManager = mapOf(
        MSG.M02 to ::message02,
        MSG.M04 to ::message04,
        MSG.M06 to ::message06,
        MSG.M0E to ::message0E,

        MSG.M12 to ::message12,
        MSG.M14 to ::message14,
        MSG.M16 to ::message16,
        MSG.M18 to ::message18,
        MSG.M1A to ::message1A,
        MSG.M1C to ::message1C,
        MSG.M1E to ::message1E,

        MSG.M24 to ::message24,
        MSG.M26 to ::message26,
        MSG.M2E to ::message2E,

        MSG.M32 to ::message32,
        MSG.M3E to ::message3E,

        MSG.M42 to ::message42,
        MSG.M44 to ::message44,
        MSG.M4A to ::message4A,
        MSG.M4C to ::message4C,
        MSG.M4E to ::message4E,

        MSG.M52 to ::message52,
        MSG.M58 to ::message58,
        MSG.M5A to ::message5A,
        MSG.M5E to ::message5E,
        MSG.M5F to ::message5F
    )

    fun messageObserver(data: LockBleReceivedLiveData) {
        if (data.device != null && data.msg.tag != 0.toByte()) {
            Logger.v(TAG, "Receive tag=${data.msg.tag.toString(16)} ")
            mMessageHandleManager[data.msg.tag]?.let {
                val mData = Triple(data.mark, data.msg, data.device!!)
                GlobalScope.launch {
                    withContext(Dispatchers.Default) {
                        it(mData)
                    }
                }
            }
        }
    }

    fun setMessageData(bluetoothDevice: BluetoothDevice, msg: MSG) {
        mSendMessageData[bluetoothDevice]?.let { m ->
            m[msg.tag] = msg
        } ?: let {
            mSendMessageData[bluetoothDevice] = mutableMapOf(msg.tag to msg)
        }
    }

    private fun message02(data: Triple<Int, MSG, BluetoothDevice>) {
        if (data.second is MSG02)
            Logger.e("Message Received", "AK = ${ParserUtils.parse((data.second as MSG02).ak)}")
        data.third.let {
            mSendMessage(it, CreateMessage.createMessage03(it.address).second)
        }
    }

    open fun message04(data: Triple<Int, MSG, BluetoothDevice>) {
        if (data.second.tag == MSG.M04) {
            Logger.e(TAG, "MSG 04 inner")
            GlobalScope.launch {
                data.third.let {
                    val mCmdInfo = DTS1586.getCmdInfo(data.first)
                    val msg04 = data.second as MSG04
                    Logger.e(TAG, "map =${msg04.allStatus.contentToString()}")
                    if (mRegisterManagers[it.address] in listOf(
                            RegisterType.SCAN_REGISTER,
                            RegisterType.CALLBACK_REGISTER,
                            RegisterType.SHARE_REGISTER,
                            RegisterType.CALLBACK_REGISTER
                        )
                    ) {
                        mNewRegisterDevice[it] = TempMessage(msg04 = msg04)
                    } else {
                        mDatabase.mDatabase.deviceDao().getDeviceWithStatusByMac(mCmdInfo.sn, it.address)?.let { deviceAndStatus ->
                            mDeviceManagers[it] = deviceAndStatus
                            syncDeviceAndStatusWithMessage04(deviceAndStatus, msg04)
                            mDatabase.mDatabase.deviceDao().update(deviceAndStatus.device)
                            mDatabase.mDatabase.deviceStatusDao().update(deviceAndStatus.deviceStatus)
                            synChildUser(
                                deviceAndStatus.device.serialNumber,
                                deviceAndStatus.device.macAddress,
                                deviceAndStatus.device.deviceUserID,
                                msg04.allUserWithStatus
                            )
                        }
                    }
                }
            }
        }

    }

    fun message06(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message08(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    open fun message0E(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    open fun message12(data: Triple<Int, MSG, BluetoothDevice>) {
        Logger.e(TAG, "msg12 Handle")
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG12) {
                val msg12 = data.second as MSG12
                data.third.let { bluetooth ->
                    val mCmdInfo = DTS1586.getCmdInfo(data.first)
                    mNewRegisterDevice[bluetooth]?.let { tempMessage ->
                        tempMessage.msg12 = msg12
                    }
                    mDatabase.mDatabase.deviceUserDao().getDeviceUser(mCmdInfo.sn, bluetooth.address, Pair(msg12.userID, mCmdInfo.sn))
                        ?.let { deviceUser ->
                            deviceUser.authCode = msg12.authCode
                            deviceUser.activeAt = msg12.activeTime
                            mDatabase.mDatabase.deviceUserDao().update(deviceUser)
                        } ?: let {
                        if (msg12.userID != mCmdInfo.userID) {
                            val newUser = DeviceUser(
                                serialNumber = mCmdInfo.sn,
                                macAddress = bluetooth.address,
                                deviceUserId = Pair(msg12.userID, mCmdInfo.sn),
                                parentUserId = Pair(mCmdInfo.userID, mCmdInfo.sn),
                                userStatus = DeviceEnum.DeviceUserStatus.INACTIVATED,
                                authCode = msg12.authCode,
                                deviceUsername = mDatabase.getNewUserName(msg12.userID)
                            )
                            mDatabase.mDatabase.deviceUserDao().insert(newUser)
                        }
                    }
                }
            }
        }
    }

    fun message14(data: Triple<Int, MSG, BluetoothDevice>) {
        Logger.e(TAG, "msg14 Handle")
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG14) {
                val msg14 = data.second as MSG14
                data.third.let { bluetooth ->
                    val mCmdInfo = DTS1586.getCmdInfo(data.first)
                    mNewRegisterDevice[bluetooth]?.let { tempMessage ->
                        tempMessage.msg14 = msg14
                    }
                    mDatabase.mDatabase.deviceUserDao().getDeviceUser(bluetooth.address, mCmdInfo.sn, Pair(msg14.userID, mCmdInfo.sn))
                        ?.let { deviceUser ->
                            deviceUser.authCode = msg14.authCode
                            deviceUser.activeAt = msg14.activeTime
                            mDatabase.mDatabase.deviceUserDao().update(deviceUser)
                        } ?: let {
                        if (msg14.userID != mCmdInfo.userID) {
                            val newUser = DeviceUser(
                                serialNumber = mCmdInfo.sn,
                                macAddress = bluetooth.address,
                                deviceUserId = Pair(msg14.userID, mCmdInfo.sn),
                                parentUserId = Pair(mCmdInfo.userID, mCmdInfo.sn),
                                userStatus = DeviceEnum.DeviceUserStatus.INACTIVATED,
                                authCode = msg14.authCode,
                                deviceUsername = mDatabase.getNewUserName(msg14.userID)
                            )
                            mDatabase.mDatabase.deviceUserDao().insert(newUser)
                        }
                    }
                }
            }
        }

    }

    private fun message16(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG16) {
                val msg16 = data.second as MSG16
                val mCmdInfo = DTS1586.getCmdInfo(data.first)
                updateDevice(data, MSG.M15) {
                    if (it is MSG15 && it.cmd == MSG15_CmdNew) {
                        val keyType = when (it.type) {
                            MSG15_TypePassword -> DeviceEnum.KeyType.PASSWORD
                            MSG15_TypeFingerprint -> DeviceEnum.KeyType.FINGERPRINT
                            MSG15_TypeNFC -> DeviceEnum.KeyType.NFC
                            MSG15_TypeFace -> DeviceEnum.KeyType.FACE
                            MSG15_TypeSeizedFingerprint -> DeviceEnum.KeyType.SEIZED_FINGERPRINT
                            else -> return@updateDevice
                        }
                        mDatabase.mDatabase.deviceUserDao().getDeviceUser(mCmdInfo.sn, mCmdInfo.macAddress, Pair(it.userID, mCmdInfo.sn))
                            ?.let { user ->
                                if (user.userStatus in listOf(DeviceEnum.DeviceUserStatus.INACTIVATED, DeviceEnum.DeviceUserStatus.UNKNOWN)) {
                                    user.userStatus = DeviceEnum.DeviceUserStatus.ACTIVATED
                                    mDatabase.mDatabase.deviceUserDao().update(user)
                                }
                            }
                        mDatabase.mDatabase.deviceKeyDao().insertOrUpdate(
                            item = DeviceKey(
                                serialNumber = mCmdInfo.sn,
                                macAddress = mCmdInfo.macAddress,
                                deviceUserId = Pair(it.userID, mCmdInfo.sn),
                                keyLockId = msg16.lockID,
                                keyType = keyType
                            ),
                            mDatabase.getDeviceKeyName(keyType, msg16.lockID)
                        )
                    }
                }
            }
        }

    }

    fun message18(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message1A(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message1C(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second.tag == MSG.M1C) {
                val msg1C = data.second as MSG1C
                val mCmdInfo = DTS1586.getCmdInfo(data.first)
                val device = mDatabase.mDatabase.deviceDao().getDeviceByMac(mCmdInfo.macAddress)
                device?.let {
                    it.updateAt = (System.currentTimeMillis() / 1000)
                    if (msg1C.type == MSG1C.Type.Lock) {
                        it.softwareVersion = msg1C.swVer
                        it.hardwareVersion = msg1C.hwVer
                    } else {
                        it.fingerprintSoftwareVersion = msg1C.swVer
                    }
                    mDatabase.mDatabase.deviceDao().update(it)
                }
            }
        }
    }

    fun message1E(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second.tag == MSG.M1E) {
                val msg1E = data.second as MSG1E
                val mCmdInfo = DTS1586.getCmdInfo(data.first)
                val deviceStatus = if (mCmdInfo.macAddress.length > 10 && mCmdInfo.sn.length > 10) mDatabase.mDatabase.deviceStatusDao()
                    .getDeviceStatus(mCmdInfo.sn, mCmdInfo.macAddress) else null
                val deviceUser = mDatabase.mDatabase.deviceUserDao()
                    .getDeviceUser(mCmdInfo.sn, mCmdInfo.macAddress, Pair(mCmdInfo.operateUserID, mCmdInfo.sn))
                when (msg1E.errCode) {
                    MSG1E_SuspendedUserDone -> {
                        deviceUser?.userStatus = DeviceEnum.DeviceUserStatus.PAUSE
                    }
                    MSG1E_SuspendedUserFailed -> {
                    }
                    MSG1E_DeleteUserDone -> deviceUser?.let { mDatabase.mDatabase.deviceUserDao().delete(it) }
                    MSG1E_EnableUserDone -> {
                        deviceUser?.userStatus = DeviceEnum.DeviceUserStatus.ACTIVATED
                    }
                    MSG1E_ModifyFaceDone,
                    MSG1E_ModifyNFCDone,
                    MSG1E_ModifyFingerprintDone,
                    MSG1E_AddOrModifyPasswordDone -> {
                        updateDevice(data, MSG.M15) {
                            if (it is MSG15 && it.cmd == MSG15_CmdEdit) {
                                val keyType = when (it.type) {
                                    MSG15_TypePassword -> DeviceEnum.KeyType.PASSWORD
                                    MSG15_TypeFingerprint -> DeviceEnum.KeyType.FINGERPRINT
                                    MSG15_TypeNFC -> DeviceEnum.KeyType.NFC
                                    MSG15_TypeFace -> DeviceEnum.KeyType.FACE
                                    MSG15_TypeSeizedFingerprint -> DeviceEnum.KeyType.SEIZED_FINGERPRINT
                                    else -> return@updateDevice
                                }
                                mDatabase.mDatabase.deviceKeyDao().insertOrUpdate(
                                    item = DeviceKey(
                                        serialNumber = mCmdInfo.sn,
                                        macAddress = mCmdInfo.macAddress,
                                        deviceUserId = Pair(it.userID, mCmdInfo.sn),
                                        keyName = null,
                                        keyLockId = it.lockID.toInt(),
                                        keyType = keyType
                                    ),
                                    mDatabase.getDeviceKeyName(keyType, it.lockID.toInt())
                                )
                            }
                        }
                    }
                    MSG1E_DeleteFingerprintDone,
                    MSG1E_DeletePasswordDone,
                    MSG1E_DeleteNFCDone,
                    MSG1E_DeleteFaceDone -> {
                        updateDevice(data, MSG.M15) {
                            if (it is MSG15 && it.cmd == MSG15_CmdDelete) {
                                val keyType = when (it.type) {
                                    MSG15_TypePassword -> DeviceEnum.KeyType.PASSWORD
                                    MSG15_TypeFingerprint -> DeviceEnum.KeyType.FINGERPRINT
                                    MSG15_TypeNFC -> DeviceEnum.KeyType.NFC
                                    MSG15_TypeFace -> DeviceEnum.KeyType.FACE
                                    MSG15_TypeSeizedFingerprint -> DeviceEnum.KeyType.SEIZED_FINGERPRINT
                                    else -> return@updateDevice
                                }
                                GlobalScope.launch {
                                    mDatabase.mDatabase.deviceKeyDao().getDeviceKey(
                                        keyType = keyType,
                                        serialNumber = mCmdInfo.sn,
                                        macAddress = mCmdInfo.macAddress,
                                        deviceUserId = Pair(it.userID, mCmdInfo.sn),
                                        keyId = it.lockID.toInt()
                                    )?.let { deleteKey ->
                                        mDatabase.mDatabase.deviceKeyDao().delete(deleteKey)
                                    }
                                }
                            }
                        }
                    }
                    MSG1E_CombinationLockDone -> deviceStatus?.enableCombinationLock = !deviceStatus?.enableCombinationLock!!
                    MSG1E_LockKeepOpenDone -> deviceStatus?.enableLockKeepOpen = !deviceStatus?.enableLockKeepOpen!!
                    MSG1E_VoicePromptsDone -> deviceStatus?.enableVoice = !deviceStatus?.enableVoice!!
                    MSG1E_LockCylinderDone -> deviceStatus?.enableLockCylinder = !deviceStatus?.enableLockCylinder!!
                    MSG1E_AntiPrizingAlarmDone -> deviceStatus?.enableAntiPrizingAlarm = !deviceStatus?.enableAntiPrizingAlarm!!
                    MSG1E_UserValidPeriodDone -> {
                        data.third.let { device ->
                            mSendMessageData[device]?.let { map ->
                                map[MSG.M1B]?.let {
                                    if (it is MSG1B) {
                                        deviceUser?.enablePeriodStart = it.tsBegin.toList()
                                        deviceUser?.enablePeriodEnd = it.tsEnd.toList()
                                    }
                                }
                            }
                        }
                    }
                    MSG1E_UnlockPeriodDone -> {
                        data.third.let { device ->
                            mSendMessageData[device]?.let { map ->
                                map[MSG.M1D]?.let {
                                    if (it is MSG1D) {
                                        deviceStatus?.unlockPeriod = it.time.toInt()
                                    }
                                }
                            }
                        }
                    }
                    MSG1E_RestoreFactorySettingsDone -> {
                        val device = mDatabase.mDatabase.deviceDao().getDeviceByMac(mCmdInfo.macAddress)
                        device?.let {
                            mDatabase.mDatabase.deviceDao().delete(it)
                        }
                    }
                    MSG1E_SecurityNFCDone -> {
                        deviceStatus?.enableNfcType = if (deviceStatus?.enableNfcType == DeviceEnum.NfcType.NORMAL) {
                            DeviceEnum.NfcType.ENCRYPTION
                        } else {
                            DeviceEnum.NfcType.NORMAL
                        }
                    }
                    MSG1E_BluetoothKeepOnDone -> deviceStatus?.enableBluetoothKeepOn = !deviceStatus?.enableBluetoothKeepOn!!
                    MSG1E_EnableInfraredDone -> deviceStatus?.enableInfrared = true
                    MSG1E_UnableInfraredDone -> deviceStatus?.enableInfrared = false
                    MSG1E_EnableAutomaticClosingDone -> deviceStatus?.enableAutomaticClosing = true
                    MSG1E_UnableAutomaticClosingDone -> deviceStatus?.enableAutomaticClosing = true
                    MSG1E_EnableMagicNumberDone -> deviceStatus?.enableMagicNumber = true
                    MSG1E_UnableMagicNumberDone -> deviceStatus?.enableMagicNumber = false
                    MSG1E_EnableTempPwdDone -> deviceStatus?.enableTemporaryPassword = true
                    MSG1E_UnableTempPwdDone -> deviceStatus?.enableTemporaryPassword = false
                }
                deviceStatus?.let {
                    mDatabase.mDatabase.deviceStatusDao().update(it)
                }
                deviceUser?.let {
                    mDatabase.mDatabase.deviceUserDao().update(it)
                }
            }
        }

    }

    fun message24(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message26(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second.tag == MSG.M26) {
                val msg26 = data.second as MSG26
                data.third.let {
                    val mCmdInfo = DTS1586.getCmdInfo(data.first)
                    if (mNewRegisterDevice[it] != null) {
                        mNewRegisterDevice[it]?.let { tempMessage ->
                            tempMessage.msg04?.let { msg04 ->
                                Logger.e(TAG, "Received MSG26  userID =${mCmdInfo.userID} imei=${mCmdInfo.imei}")
                                Logger.e(TAG, "all=${msg04.synStatus.toList()}")
                                try {
                                    Logger.e(TAG, "all=${msg04.allUserWithStatus}")
                                    createNewDeviceAndUser(it, msg04, msg26, mCmdInfo)
                                } catch (e: Exception) {
                                    Logger.e(TAG, "e=$e")
                                }
                            }
                        }
                        mNewRegisterDevice.remove(it)
                        mRegisterManagers.remove(it.address)
                        mDatabase.mDatabase.deviceDao().getDeviceWithStatusByMac(mCmdInfo.sn, it.address)?.let { deviceAndStatus ->
                            mDeviceManagers[it] = deviceAndStatus
                        }
                    } else {
                        mDeviceManagers[it]?.let { viewDevice ->
                            syncDeviceKeyWithMessage26(msg26, mCmdInfo, viewDevice.device.macAddress, viewDevice.device.face)
                            mDatabase.mDatabase.deviceUserDao().getDeviceUser(
                                viewDevice.device.serialNumber,
                                viewDevice.device.macAddress,
                                if (mCmdInfo.operateUserID != 0) Pair(mCmdInfo.operateUserID, mCmdInfo.sn) else Pair(mCmdInfo.userID, mCmdInfo.sn)
                            )?.let { user ->
                                user.lifecycleStart = msg26.lifeTSBegin
                                user.lifecycleEnd = msg26.lifeTSEnd
                                user.authCode = DTS1586.getAuthCode(
                                    mCmdInfo.operateUserID,
                                    viewDevice.device.imei,
                                    viewDevice.device.macAddress,
                                    mCmdInfo.dynCode,
                                    msg26.ts
                                )
                                user.enablePeriodStart = msg26.tsBegin.toList()
                                user.enablePeriodEnd = msg26.tsEnd.toList()
                                mDatabase.mDatabase.deviceUserDao().update(user)
                            }
                        }
                    }
                }
            }
        }
    }

    fun message2E(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG2E) {
                val msg2E = data.second as MSG2E
                val mCmdInfo = DTS1586.getCmdInfo(data.first)
                val deviceStatus = if (mCmdInfo.macAddress.length > 10 && mCmdInfo.sn.length > 10) mDatabase.mDatabase.deviceStatusDao()
                    .getDeviceStatus(mCmdInfo.sn, mCmdInfo.macAddress) else null
                val deviceUser = mDatabase.mDatabase.deviceUserDao()
                    .getDeviceUser(mCmdInfo.sn, mCmdInfo.macAddress, Pair(mCmdInfo.operateUserID, mCmdInfo.sn))
                when (msg2E.errCode) {
                    MSG2E_PowerSavingDone -> {
                        data.third.let { device ->
                            mSendMessageData[device]?.let { map ->
                                map[MSG.M2D]?.let {
                                    if (it is MSG2D) {
                                        deviceStatus?.powerSavingStartAt = it.powerSaveTSBegin
                                        deviceStatus?.powerSavingEndAt = it.powerSaveTSEnd
                                    }
                                }
                            }
                        }
                    }
                    MSG2E_UserLifecycleDone -> {
                        data.third.let { device ->
                            mSendMessageData[device]?.let { map ->
                                map[MSG.M29]?.let {
                                    if (it is MSG29) {
                                        deviceUser?.lifecycleStart = it.tsBegin
                                        deviceUser?.lifecycleEnd = it.tsEnd
                                    }
                                }
                            }
                        }
                    }
                }
                if (deviceStatus != null) {
                    mDatabase.mDatabase.deviceStatusDao().update(deviceStatus)
                }
                if (deviceUser != null) {
                    mDatabase.mDatabase.deviceUserDao().update(deviceUser)
                }
            }
        }
    }

    fun message32(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG32) {
                val msg32 = data.second as MSG32
                Logger.i(
                    TAG,
                    "mac =${data.third.address} msg32 userID=${msg32.userID} state= ${msg32.state} type=${msg32.type} logID=${msg32.logID} lockID=${msg32.lockID}"
                )
                val mCmdInfo = DTS1586.getCmdInfo(data.first)
                when (msg32.state) {
                    DeviceEnum.setLogState(DeviceEnum.LogState.BATTERY) -> {
                        val deviceStatus = if (mCmdInfo.macAddress.length > 10 && mCmdInfo.sn.length > 10) mDatabase.mDatabase.deviceStatusDao()
                            .getDeviceStatus(mCmdInfo.sn, mCmdInfo.macAddress) else null
                        deviceStatus?.let { status ->
                            status.battery = msg32.lockID
                            mDatabase.mDatabase.deviceStatusDao().update(status)
                        }
                    }
                    else -> {
                        if (mCmdInfo.macAddress.length < 10 && mCmdInfo.sn.length < 10) return@launch
                        val log = DeviceLog(
                            serialNumber = mCmdInfo.sn,
                            macAddress = mCmdInfo.macAddress,
                            deviceUserId = Pair(msg32.userID, mCmdInfo.sn),
                            logId = msg32.logID,
                            logUnlockType = DeviceEnum.getUnlockLogType(msg32.type),
                            logState = DeviceEnum.getLogState(msg32.state),
                            logLockId = msg32.lockID,
                            logCreateAt = msg32.time.toLong()
                        )
                        mDatabase.mDatabase.deviceLogDao().insertOrUpdate(log)
                    }
                }
            }
        }

    }

    fun message3E(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message42(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message44(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG44) {

                val msg44 = data.second as MSG44
                Logger.e(
                    TAG,
                    "44Version scpu=${msg44.sCpuVer} \n ncpu=${msg44.nCpuVer} model=${msg44.modelVer} main=${msg44.majorVer} \n ui=${msg44.uiVer}"
                )
                val mCmdInfo = DTS1586.getCmdInfo(data.first)
                val device = mDatabase.mDatabase.deviceDao().getDeviceByMac(mCmdInfo.macAddress)
                device?.let {
                    it.updateAt = (System.currentTimeMillis() / 1000)
                    val versionMap: MutableMap<DeviceEnum.FaceVersion, String> = EnumMap(DeviceEnum.FaceVersion::class.java)
                    versionMap[DeviceEnum.FaceVersion.MAIN] = msg44.majorVer
                    versionMap[DeviceEnum.FaceVersion.SCPU] = msg44.sCpuVer
                    versionMap[DeviceEnum.FaceVersion.NCPU] = msg44.nCpuVer
                    versionMap[DeviceEnum.FaceVersion.MODEL] = msg44.modelVer
                    versionMap[DeviceEnum.FaceVersion.UI] = msg44.uiVer
                    it.faceSoftwareVersion = versionMap
                    mDatabase.mDatabase.deviceDao().update(it)
                }
            }
        }
    }

    fun message4A(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG4A) {
                val msg4A = data.second as MSG4A
                val mCmdInfo = DTS1586.getCmdInfo(data.first)
                val deviceStatus = if (mCmdInfo.macAddress.length > 10 && mCmdInfo.sn.length > 10) mDatabase.mDatabase.deviceStatusDao()
                    .getDeviceStatus(mCmdInfo.sn, mCmdInfo.macAddress) else null
                when (msg4A.type) {
                    MSG4A_TYPE_Volume -> deviceStatus?.volume = msg4A.value
                    MSG4A_TYPE_LockStatus -> deviceStatus?.opening = msg4A.value == MSG4A_VALUE_LockIsOpen
                    MSG4A_TYPE_Language -> deviceStatus?.language = DeviceEnum.getLockLanguage(msg4A.value)
                    MSG4A_TYPE_DoorbellFollow -> deviceStatus?.enableDoorbell = msg4A.value == MSG49_VALUE_DoorbellFollowEnable
                    MSG4A_TYPE_WifiRSSI -> deviceStatus?.wifiRssi = msg4A.value
                    MSG4A_TYPE_WifiStatus -> deviceStatus?.wifiStatus = DeviceEnum.getWifiStatus(msg4A.value)
                    MSG4A_TYPE_WifiPower -> deviceStatus?.enableWifi = msg4A.value == MSG4A_VALUE_WifiOn
                }
                if (deviceStatus != null) {
                    mDatabase.mDatabase.deviceStatusDao().update(deviceStatus)
                }
            }
        }

    }

    fun message4C(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message4E(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG4E) {
                val msg4E = data.second as MSG4E
                val mCmdInfo = DTS1586.getCmdInfo(data.first)
                val deviceStatus = if (mCmdInfo.macAddress.length > 10 && mCmdInfo.sn.length > 10) mDatabase.mDatabase.deviceStatusDao()
                    .getDeviceStatus(mCmdInfo.sn, mCmdInfo.macAddress) else null
                when (msg4E.errCode) {
                    MSG4E_SetVolumeDone -> {
                        updateDevice(data, MSG.M49) {
                            if (it is MSG49 && it.type == MSG49_TYPE_SetVolume) {
                                deviceStatus?.volume = it.value
                            }
                        }
                    }
                    MSG4E_SetLanguageDone -> {
                        updateDevice(data, MSG.M49) {
                            if (it is MSG49 && it.type == MSG49_TYPE_SetLanguage) {
                                deviceStatus?.language = DeviceEnum.getLockLanguage(it.value)
                            }
                        }
                    }
                    MSG4E_SetDoorbellFollowDone -> deviceStatus?.enableDoorbell = !(deviceStatus?.enableDoorbell
                        ?: true)
                    MSG4E_SetWifiPowerDone -> deviceStatus?.enableWifi = !(deviceStatus?.enableWifi
                        ?: true)
                }
                if (deviceStatus != null) {
                    mDatabase.mDatabase.deviceStatusDao().update(deviceStatus)
                }
            }
        }
    }

    fun message52(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG52) {
                val msg52 = data.second as MSG52
                mDatabase.mDatabase.deviceDao().getDeviceByMac(
                    data.third.address
                        ?: ""
                )?.let { device ->
                    device.wifiSoftwareVersion = msg52.version
                    mDatabase.mDatabase.deviceDao().update(device)
                }
            }
        }

    }

    fun message58(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message5A(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message5E(data: Triple<Int, MSG, BluetoothDevice>) {

    }

    fun message5F(data: Triple<Int, MSG, BluetoothDevice>) {
        GlobalScope.launch(Dispatchers.IO) {
            if (data.second is MSG5F) {
                val msg5F = data.second as MSG5F
                val mCmdInfo = DTS1586.getCmdInfo(data.first)
                val device = mDatabase.mDatabase.deviceDao().getDeviceByMac(mCmdInfo.macAddress)
                device?.let {
                    it.updateAt = (System.currentTimeMillis() / 1000)
                    if (msg5F.type == MSG5F_VersionBackPanel) {
                        it.backPanelSoftwareVersion = msg5F.swVer
                    }
                    mDatabase.mDatabase.deviceDao().update(it)
                }
            }
        }
    }

    private suspend fun createNewDeviceAndUser(bluetoothDevice: BluetoothDevice, msg04: MSG04, msg26: MSG26, cmdInfo: CmdInfo) {
        val newDevice = Device(
            uid = mDatabase.getLogUser()?.userId?:0,
            name = mDatabase.getNewDeviceName(),
            serialNumber = cmdInfo.sn,
            macAddress = bluetoothDevice.address,
            secretCode = cmdInfo.secretCode,
            deviceUserID = Pair(cmdInfo.userID, cmdInfo.sn),
            imei = cmdInfo.imei,
            createAt = (System.currentTimeMillis() / 1000),
            updateAt = (System.currentTimeMillis() / 1000)
        )
        syncDeviceWithMessage04(newDevice, msg04)
        val newDeviceStatus = DeviceStatus(
            serialNumber = cmdInfo.sn,
            macAddress = bluetoothDevice.address,
            deviceUserID = Pair(cmdInfo.userID, cmdInfo.sn),
            createAt = (System.currentTimeMillis() / 1000),
            updateAt = (System.currentTimeMillis() / 1000)
        )
        syncDeviceStatusWithMessage04(newDeviceStatus, msg04)
        mDatabase.mDatabase.deviceDao().insert(newDevice)
        mDatabase.mDatabase.deviceStatusDao().insert(newDeviceStatus)
        val newDeviceUser = DeviceUser(
            deviceUsername = mDatabase.getNewUserName(if (cmdInfo.operateUserID == 0) cmdInfo.userID else cmdInfo.operateUserID, msg26.userType),
            serialNumber = cmdInfo.sn,
            macAddress = bluetoothDevice.address,
            deviceUserId = Pair(cmdInfo.userID, cmdInfo.sn),
            authCode = cmdInfo.authCode,
            administrator = msg26.userType == 1.toByte(),
            allUserId = msg04.allUserWithStatus,
            userStatus = DeviceEnum.getDeviceUserStatus(msg26.status.toInt()),
            enablePeriodStart = msg26.tsBegin.toList(),
            enablePeriodEnd = msg26.tsEnd.toList(),
            lifecycleStart = msg26.lifeTSBegin,
            lifecycleEnd = msg26.lifeTSBegin,
            createAt = (System.currentTimeMillis() / 1000),
            updateAt = (System.currentTimeMillis() / 1000)
        )
        Logger.e(TAG, "Insert DeviceUser")
        mDatabase.mDatabase.deviceUserDao().insert(newDeviceUser)
        syncDeviceKeyWithMessage26(msg26, cmdInfo, bluetoothDevice.address, (msg04.enableStatus1 and 2.toByte()) == 2.toByte())
        synChildUser(cmdInfo.sn, bluetoothDevice.address, Pair(cmdInfo.userID, cmdInfo.sn), msg04.allUserWithStatus)
    }

    private fun syncDeviceAndStatusWithMessage04(deviceAndDeviceStatus: DeviceAndDeviceStatus, msg04: MSG04) {
        syncDeviceWithMessage04(deviceAndDeviceStatus.device, msg04)
        syncDeviceStatusWithMessage04(deviceAndDeviceStatus.deviceStatus, msg04)
    }

    private fun syncDeviceWithMessage04(device: Device, msg04: MSG04) {
        device.apply {
            Logger.e(TAG, "msg04 = ${msg04.enableStatus2.toInt()}")
            nfc = (msg04.enableStatus1 and 1.toByte()) != 1.toByte()
            face = (msg04.enableStatus1 and 2.toByte()) == 2.toByte()
            infrared = (msg04.enableStatus1 and 4.toByte()) == 4.toByte()
            variablePassword = (msg04.enableStatus1 and 8.toByte()) == 8.toByte()
            automaticLock = (msg04.enableStatus1 and 16.toByte()) == 16.toByte()
            selfEjectLock = (msg04.enableStatus1 and 32.toByte()) == 32.toByte()
            lockKeepOpen = (msg04.enableStatus1 and 64.toByte()) == 64.toByte()
            permissionSwitch = (msg04.enableStatus1 and 128.toByte()) == 128.toByte()

            magicNumber = (msg04.enableStatus2 and 1.toByte()) == 1.toByte()
            temporaryPassword = (msg04.enableStatus2 and 2.toByte()) == 2.toByte()
            statusQuery = (msg04.enableStatus2 and 4.toByte()) == 4.toByte()
            seizedFingerprint = (msg04.enableStatus2 and 8.toByte()) == 8.toByte()
            volumeAdjustment = (msg04.enableStatus2 and 16.toByte()) == 16.toByte()
            languageSwitch = (msg04.enableStatus2 and 32.toByte()) == 32.toByte()
            followDoorbell = (msg04.enableStatus2 and 64.toByte()) == 64.toByte()
            wifi = (msg04.enableStatus2 and 128.toByte()) == 128.toByte()

            lockCylinder = (msg04.settingStatus1 and 128.toByte()) == 0.toByte()

            backPanelOta = (msg04.enableStatus3 and 1.toByte()) == 1.toByte()
            temporaryPasswordWithoutAsterisk = (msg04.enableStatus3 and 2.toByte()) == 2.toByte()
            temporaryPasswordSecretCode = msg04.tempPwdSk
            updateAt = (System.currentTimeMillis() / 1000)
        }
    }

    private fun syncDeviceStatusWithMessage04(deviceStatus: DeviceStatus, msg04: MSG04) {
        deviceStatus.apply {
            battery = msg04.batPercent.toInt()
            unlockPeriod = msg04.unlockTime.toInt()
            enableBluetoothKeepOn = (msg04.settingStatus1 and 0x01) == 0x01.toByte()
            enableVoice = (msg04.settingStatus1 and 0x02) == 0x02.toByte()
            enableLockCylinder = (msg04.settingStatus1 and 0x04) == 0x04.toByte()
            enableLockCylinder = (msg04.settingStatus1 and 0x04) == 0x04.toByte()
            enableAntiPrizingAlarm = (msg04.settingStatus1 and 0x08) == 0x08.toByte()
            enableCombinationLock = (msg04.settingStatus1 and 0x10) == 0x10.toByte()
            enableNfcType = DeviceEnum.getNfcType((msg04.settingStatus1 and 0x20) == 0x20.toByte())
            enableBluetoothKeepOn = (msg04.settingStatus1 and 0x40) == 0x40.toByte()

            enableAutomaticClosing = (msg04.settingStatus2 and 1.toByte()) == 1.toByte()
            enableInfrared = (msg04.settingStatus2 and 2.toByte()) == 2.toByte()
            enableMagicNumber = (msg04.settingStatus2 and 4.toByte()) == 4.toByte()
            enableTemporaryPassword = (msg04.settingStatus2 and 8.toByte()) == 8.toByte()
            opening = (msg04.settingStatus2 and 16.toByte()) == 16.toByte()
            enableDoorbell = (msg04.settingStatus2 and 32.toByte()) == 32.toByte()
            enableWifi = (msg04.settingStatus2 and 64.toByte()) == 64.toByte()
            powerSavingStartAt = msg04.powerSaveBegin
            powerSavingEndAt = msg04.powerSaveEnd
            updateAt = (System.currentTimeMillis() / 1000)
        }
    }

    private fun syncDeviceKeyWithMessage26(msg26: MSG26, cmdInfo: CmdInfo, macAddress: String, isFace: Boolean) {
        val temp = DeviceKey(
            serialNumber = cmdInfo.sn,
            macAddress = macAddress,
            deviceUserId = Pair(cmdInfo.operateUserID, cmdInfo.sn)
        )
        if (msg26.key != 0.toByte()) {
            insertDeviceKey(temp, DeviceEnum.KeyType.PASSWORD, msg26.key.toInt())
        } else {
            mDatabase.mDatabase.deviceKeyDao().deleteByType(
                temp.serialNumber,
                temp.macAddress,
                temp.deviceUserId,
                DeviceEnum.KeyType.PASSWORD
            )
        }
        Logger.e(TAG, "nfc=${msg26.nfc} face =${msg26.face}")
        if (msg26.nfc != 0.toByte()) {
            insertDeviceKey(temp, DeviceEnum.KeyType.NFC, msg26.nfc.toInt())
        } else {
            mDatabase.mDatabase.deviceKeyDao().deleteByType(
                temp.serialNumber,
                temp.macAddress,
                temp.deviceUserId,
                DeviceEnum.KeyType.NFC
            )
        }

        if (msg26.face != 0.toByte()) {
            insertDeviceKey(temp, DeviceEnum.KeyType.FACE, msg26.face.toInt())
        } else {
            mDatabase.mDatabase.deviceKeyDao().deleteByType(
                temp.serialNumber,
                temp.macAddress,
                temp.deviceUserId,
                DeviceEnum.KeyType.FACE
            )
        }

        mDatabase.mDatabase.deviceKeyDao().getDeviceFPKeys(
            temp.serialNumber,
            temp.macAddress,
            temp.deviceUserId
        )?.let { keyList ->
            for (key in keyList) {
                if (!msg26.fp.contains(key.keyLockId.toByte())) {
                    mDatabase.mDatabase.deviceKeyDao().delete(key)
                }
            }
        }
        for (i in msg26.fp) {
            insertDeviceKey(
                temp, if (i == msg26.seizedFp) DeviceEnum.KeyType.SEIZED_FINGERPRINT else DeviceEnum.KeyType.FINGERPRINT, i.toInt()
            )
        }
    }

    private fun synChildUser(serialNumber: String, macAddress: String, parentId: Pair<Int, String>, userMap: MutableMap<Int, Int>) {
        mDatabase.mDatabase.deviceUserDao().getDeviceUser(
            serialNumber = serialNumber,
            macAddress = macAddress,
            userID = parentId
        )?.let { deviceUser ->
            deviceUser.allUserId = userMap
            deviceUser.userStatus = DeviceEnum.getDeviceUserStatus(userMap[parentId.first] ?: 0)
            mDatabase.mDatabase.deviceUserDao().update(deviceUser)
        }
        mDatabase.mDatabase.deviceUserDao().getDeviceUserWithChild(
            serialNumber = serialNumber,
            macAddress = macAddress,
            userID = parentId,
        )?.let {
            it.childUsers.forEach { child ->
                if (!userMap.contains(child.deviceUserId.first)) {
                    mDatabase.mDatabase.deviceUserDao().delete(child)
                }
            }
            userMap.forEach { entry ->
                if (entry.key != parentId.first) {
                    val newUser = DeviceUser(
                        serialNumber = serialNumber,
                        macAddress = macAddress,
                        parentUserId = parentId,
                        deviceUserId = Pair(entry.key, serialNumber),
                        userStatus = DeviceEnum.getDeviceUserStatus(entry.value),
                        deviceUsername = mDatabase.getNewUserName(entry.key)
                    )
                    mDatabase.mDatabase.deviceUserDao().updateOrInsertUser(newUser)
                }
            }
        }
    }


    private fun insertDeviceKey(
        baseKey: DeviceKey,
        type: DeviceEnum.KeyType,
        id: Int = 0
    ) {
        if (id == 0) return
        val key = baseKey.apply {
            keyType = type
            keyLockId = id
        }
        mDatabase.mDatabase.deviceKeyDao().insertOrUpdate(key, mDatabase.getDeviceKeyName(type, id))
        with(baseKey) {
            keyType = DeviceEnum.KeyType.UNKNOWN
            keyLockId = 0
        }
    }

    private fun updateDevice(data: Triple<Int, MSG, BluetoothDevice>, tag: Byte, action: (MSG) -> Unit) {
        data.third.let { device ->
            mSendMessageData[device]?.let { map ->
                map[tag]?.let {
                    action(it)
                }
            }
        }
    }
}
