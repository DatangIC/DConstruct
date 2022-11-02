package com.datangic.smartlock.respositorys

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import cn.dttsh.dts1586.DTS1586
import cn.dttsh.dts1586.MSG
import com.datangic.api.smartlock.SmartLockOta
import com.datangic.data.DatabaseRepository
import com.datangic.smartlock.R
import com.datangic.data.SystemSettings
import com.datangic.smartlock.ble.*
import com.datangic.smartlock.ble.ReceivedMessageHandle.RegisterType
import com.datangic.smartlock.ble.livedata.state.ConnectionState
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.smartlock.liveData.*
import com.datangic.common.utils.Logger
import com.datangic.smartlock.utils.UtilsBle.Companion.checkMac
import com.datangic.smartlock.viewModels.ScannerRepository
import kotlinx.coroutines.*
import no.nordicsemi.android.support.v18.scanner.ScanFilter

@ObsoleteCoroutinesApi
class BleManagerApiRepository constructor(
    val mContext: Context,
    val mScannerRepository: ScannerRepository,
    private val mDatabase: DatabaseRepository
) {

    private val TAG = BleManagerApiRepository::class.simpleName
    private val mHandler = Handler(Looper.getMainLooper())
    private var mDeviceInfo: Triple<String, String, String>? = null
    private var mShareUser: Pair<Int, String>? = null

    private val mLockMutableBleStatusLiveData = LockMutableBleStatusLiveData()

    val mSendDataLiveData: LockBleSendMessageLiveData by lazy { LockBleSendMessageLiveData() }
    private val mServerManager by lazy { LockServerManager(mContext) }
    private val mBleManagers: MutableMap<BluetoothDevice, LockBleManager> by lazy { HashMap() }
    private val mManagerDevices: MutableList<BluetoothDevice> = ArrayList()

    private val mHttpOTA by lazy { SmartLockOta.create() }

    // 该变量为最后一次断开的设备信息，保存60S，以减少60秒内需要扫描设备
    private var mLastDisconnectedBluetoothDevice: BluetoothDevice? = null
        set(value) {
            field = value
            mHandler.removeCallbacks(clearLastDisconnectedBluetoothDevice)
            mHandler.postDelayed(clearLastDisconnectedBluetoothDevice, 60 * 1000L)
        }
    private val clearLastDisconnectedBluetoothDevice = {
        mLastDisconnectedBluetoothDevice = null
    }

    /**
     * 准备连接的蓝牙地址，当Scan当时候添加，扫描到后移除，停止扫描后清除
     */
    private val mReadyConnectMac: MutableList<String> = mutableListOf()

    private val mScannerStateObserver = Observer<ScannerStateLiveData> {
        when (it.state) {
            ScannerStateLiveData.State.SCAN_TIMEOUT -> {
                mLockMutableBleStatusLiveData.scanTimeout(mReadyConnectMac)
            }
            ScannerStateLiveData.State.SCANNING -> mLockMutableBleStatusLiveData.scanning(mReadyConnectMac)
            ScannerStateLiveData.State.SCAN_STOP -> {
                mReadyConnectMac.clear()
                mLockMutableBleStatusLiveData.scanStop()
            }
            else -> {
            }
        }
    }
    private val mScannerObserver = Observer<ScannerLiveData> {
        for (i in it.mDevices) {
            if (mReadyConnectMac.contains(i.device.address)) {
                connect(i.device)
                mReadyConnectMac.remove(i.device.address)
            }
        }
    }

    init {
        mScannerRepository.mScannerLiveData.observeForever(mScannerObserver)
        mScannerRepository.mScannerStateLiveData.observeForever(mScannerStateObserver)
        mLockMutableBleStatusLiveData.observeForever {
            Logger.e("LockBleStatusLiveDate2", "Connect State Mac=${it.device?.address} state =${it.mConnectionState.state}")
            if (it.mConnectionState is ConnectionState.Ready) {
                Logger.e("LockBleStatusLiveDate2", "Connected Mac=${it.device?.address}")
                MainScope().launch(Dispatchers.IO) {
                    it.device?.let { it1 ->
                        mMessageApi.getMessageOperation().register(it1, mDeviceInfo, mShareUser)
                    }
                }
            }
        }
    }
    /***
     * Retrofit
     */


    /** =======================Send Message==================**/
    private fun Pair<BluetoothDevice, MSG>.execute1(): CreateMessage.State {
        return if (isConnected(this.first)) {
            val data = DTS1586.creatorCmd(this.second)
            if (data == null) {
                Logger.e(TAG, "Send Message ERROR data is Null")
                CreateMessage.State.CREATE_ERROR
            } else {
                Logger.e(TAG, "Send Message tag = ${this.second.tag.toString(16)}")
                mBleManagers[this.first]?.send(data)
                mSendDataLiveData.sendMessageData(this.first, this.second)
                CreateMessage.State.SUCCESS
            }
        } else {
            Logger.e(TAG, "Send Message ERROR  DisConnect3")
            CreateMessage.State.DISCONNECT
        }
    }

    private fun Pair<String, MSG>.execute(): CreateMessage.State {
        return getBluetoothDevice(this.first)?.let { device ->
            Pair(device, this.second).execute1()
        } ?: CreateMessage.State.CREATE_ERROR
    }

    private fun sendMessage(device: BluetoothDevice, msg: MSG): CreateMessage.State {
        return Pair(device, msg).execute1()
    }

    fun sendMessage(macAddress: String, msg: MSG): CreateMessage.State {
        return Pair(macAddress, msg).execute()
    }

    /****=========================================***/

    private val mMessageApi = MessageApi(mDatabase, ::sendMessage).also {
        mSendDataLiveData.observeForever { data ->
            MainScope().launch {
                data.device?.let { device ->
                    it.getMessageOperation().setMessageData(device, data.msg)
                }
            }
        }
    }

    fun getSendSuccessCallbackLiveData() = mMessageApi.mSendSuccessCallbacks.mSendSuccessLiveData
    fun getMessageOperation() = mMessageApi.getMessageOperation()
    fun getReceivedMessageLiveData() = mMessageApi.mReceivedDataLiveData


    /** =================================== Database ===============**/
    val mDefaultDeviceInfo
        get() = mDatabase.mDefaultDeviceInfo

    val mDefaultDeviceInfoLiveData
        get() = mDatabase.mDefaultDeviceInfoLiveData

    fun setDefaultDeviceInfo(serialNumber: String, macAddress: String) = mDatabase.setDefaultDeviceInfo(
        serialNumber, macAddress
    )

    val mViewDevices
        get() = mDatabase.mViewManagerDevices

    val mViewDevicesLiveData
        get() = mDatabase.mViewManagerDevicesLiveData

    val mDefaultDeviceView
        get() = mDatabase.mDefaultViewDevice

    val mSecretCodeMap
        get() = mDatabase.mSecretCodeMap

    val mDefaultSecretCode
        get() = mDatabase.mDefaultSecretCode

    val mViewDevicesStatusLiveData
        get() = mDatabase.mViewDevicesStatusLiveData

    val mSystemSettingsLiveData
        get() = mDatabase.mSystemSettingsLiveData

    fun <T> update(item: T) = mDatabase.update(item)

    /**
     * keys of Device
     */
    fun getViewDeviceKeysLiveData(
        userID: Int,
        types: List<DeviceEnum.KeyType> = listOf(
            DeviceEnum.KeyType.PASSWORD,
            DeviceEnum.KeyType.FACE,
            DeviceEnum.KeyType.FINGERPRINT,
            DeviceEnum.KeyType.SEIZED_FINGERPRINT,
            DeviceEnum.KeyType.NFC
        ),
        serialNumber: String? = null,
        macAddress: String? = null
    ) = mDatabase.getViewDeviceKeysLiveData(userID, types, serialNumber, macAddress)

    fun updateDeviceKeyName(userID: Int, keyId: Int, keyType: DeviceEnum.KeyType, newName: String) =
        mDatabase.updateDeviceKeyName(userID, keyId, keyType, newName)

    /**
     *  View of Devices
     */
    fun setDevicesViewObserver(lifecycleOwner: LifecycleOwner?, observer: Observer<List<ViewManagerDevice>>) =
        mDatabase.setManagerDevicesViewObserver(lifecycleOwner, observer)

    fun updateDeviceName(newName: String, serialNumber: String? = null, macAddress: String? = null) =
        mDatabase.updateDeviceName(newName, serialNumber, macAddress)

    fun deleteDevice(serialNumber: String, macAddress: String) = mDatabase.deleteDevice(serialNumber, macAddress)

    /**
     * Device
     */
    fun getDeviceLiveData(serialNumber: String? = null, macAddress: String? = null) = mDatabase.getDeviceLiveData(serialNumber, macAddress)

    fun getDeviceByMac(macAddress: String) = mDatabase.getDeviceByMac(macAddress)

    /**
     * Device Logs
     */

    fun getDeviceLogsLiveData(userID: Int = 0, serialNumber: String? = null, macAddress: String? = null) =
        mDatabase.getDeviceLogsLiveData(userID, serialNumber, macAddress)

    fun deleteDeviceLog(userId: Int, logId: Int, logState: DeviceEnum.LogState, serialNumber: String? = null, macAddress: String? = null) =
        mDatabase.deleteDeviceLog(userId, logId, logState, serialNumber, macAddress)

    /**
     * User
     */

    fun getDeviceUserLiveData(userID: Int, serialNumber: String? = null, macAddress: String? = null) =
        mDatabase.getDeviceUserLiveData(userID, serialNumber, macAddress)

    fun getUserWithChildUsersLiveData(userID: Int, serialNumber: String? = null, macAddress: String? = null) =
        mDatabase.getUserWithChildUsersLiveData(userID, serialNumber, macAddress)

    fun updateDeviceUserName(newName: String, userID: Int, serialNumber: String? = null, macAddress: String? = null) =
        mDatabase.updateDeviceUserName(newName, userID, serialNumber, macAddress)


    /**
     *  SecretCode of Lock
     */
    fun setDefaultSecretCode(defaultKey: String) {
        MainScope().launch(Dispatchers.IO) {
            mDatabase.dataStore.setDefaultSecretCode(defaultKey)
        }
    }

    /**
     * SystemSettings 系统设置监听
     */
    fun setSystemSettingsObserver(lifecycleOwner: LifecycleOwner, observer: Observer<SystemSettings>) =
        mDatabase.mSystemSettingsLiveData.observe(lifecycleOwner, observer)


    /** ====================================BLE Connect================ **/

    fun connectWithRegister(address: String, lifecycleOwner: LifecycleOwner, type: RegisterType) {
        connect(address)
        mMessageApi.getMessageOperation().mRegisterManagers[address] = type
    }

    fun connectWithRegister(
        device: BluetoothDevice,
        type: RegisterType
    ) {
        connect(device)
        mMessageApi.getMessageOperation().mRegisterManagers[device.address] = type
    }

    /**
     * 通过SharedCode 连接
     */
    fun connectWithShareCode(
        address: String, lifecycleOwner: LifecycleOwner,
        share: Pair<Int, String>
    ) {
        Logger.e(TAG, "add Share Code")
        mShareUser = share
        connect(address)
        mMessageApi.getMessageOperation().mRegisterManagers[address] = RegisterType.SHARE_REGISTER
    }

    fun connectWithSetDeviceInfo(
        device: BluetoothDevice,
        deviceInfo: Triple<String, String, String>
    ) {
        mDeviceInfo = deviceInfo
        connect(device)
        mMessageApi.getMessageOperation().mRegisterManagers[device.address] = RegisterType.SET_INFORMATION
    }


    private fun connect(address: String) {
        if (mLastDisconnectedBluetoothDevice != null && mLastDisconnectedBluetoothDevice?.address == address.checkMac()) {
            connect(mLastDisconnectedBluetoothDevice!!)
        } else {
            mReadyConnectMac.add(address.checkMac())
            if (mScannerRepository.mScannerStateLiveData.isScanning()) {
                mLockMutableBleStatusLiveData.scanning(address)
                return
            }
            mScannerRepository.startBLEScan(
                mContext,
                getScanFilter()
            )
            mLockMutableBleStatusLiveData.scanning(address)
        }
    }

    private fun getScanFilter(): MutableList<ScanFilter> {
        val list: MutableList<ScanFilter> = mutableListOf()
        mViewDevices.forEach {
            list.add(ScanFilter.Builder().setDeviceAddress(it.macAddress.checkMac()).build())
        }
        return list
    }

    private val mConnectStateObserver = Observer<ConnectionState> {
        mLockMutableBleStatusLiveData.changeState(it)
        mLastDisconnectedBluetoothDevice = it.device
        Logger.e(
            TAG, "BLE address=${it.device?.address} state=${it.state} " +
                    "\n type=${it} "
        )
        if (it is ConnectionState.Disconnected) {
            Logger.e(TAG, "ManagerDevice delete ${it.device?.address} ")
            mManagerDevices.remove(it.device)
        } else {
            if (!mManagerDevices.contains(it.device)) {
                it.device?.let { it1 -> mManagerDevices.add(it1) }
            }
        }
    }

    private fun connect(device: BluetoothDevice) {
        if (mManagerDevices.contains(device)) {
            return
        }
        mManagerDevices.add(device)
        mLockMutableBleStatusLiveData.deviceFound(device)
        Logger.e(TAG, "ManagerDevice has ${device.address} = ${mManagerDevices.contains(device)}")
        var lockBleManager: LockBleManager? = mBleManagers[device]
        if (lockBleManager == null) {
            lockBleManager = LockBleManager(device, mContext, mMessageApi.mSendSuccessCallbacks, mMessageApi.mReceiveMessageCallbacks)
            mBleManagers[device] = lockBleManager
            lockBleManager.useServer(mServerManager)
            lockBleManager.getConnectionStateLiveData().observeForever(mConnectStateObserver)
        }
        Logger.e(TAG, "callback")
        lockBleManager.connect(device)
            .before { Logger.v(TAG, "Start Connect") }
            .done {
                Logger.v(TAG, "Connected")
                if (mReadyConnectMac.isNullOrEmpty())
                    mHandler.postDelayed({ mScannerRepository.stopScan(true) }, 3000)
            }
            .fail { it, _ ->
                Logger.v(TAG, "Connect Failure")
                if (mReadyConnectMac.isNullOrEmpty())
                    mHandler.postDelayed({ mScannerRepository.stopScan(true) }, 3000)
                mManagerDevices.remove(it)
                mBleManagers.remove(it)
            }
            .retry(4, 400)
            ?.timeout(5000)
            ?.enqueue()
    }

    fun disconnect(device: BluetoothDevice) {
        val lockBleManager = mBleManagers[device]
        if (lockBleManager?.isConnected == true) {
            Logger.i(TAG, "Disconnect by user")
            lockBleManager.disconnect().enqueue()
        }
        mManagerDevices.remove(device)
    }

    fun disconnect(macAddress: String) {
        var lockBluetoothDevice: BluetoothDevice? = null
        for (i in mBleManagers.keys) {
            if (i.address == macAddress) {
                lockBluetoothDevice = i
                break
            }
        }
        if (mBleManagers[lockBluetoothDevice]?.isConnected == true) {
            Logger.i(TAG, "Disconnect by user")
            mBleManagers[lockBluetoothDevice]?.disconnect()?.enqueue()
        }
        mManagerDevices.remove(lockBluetoothDevice)
    }

    /***====================================**/
    /**
     * 检测蓝牙是否连接
     */
    fun isConnected(device: BluetoothDevice): Boolean {
        val result = if (mManagerDevices.contains(device))
            return mBleManagers[device]?.isConnected ?: false
        else false

        Logger.e(TAG, "MAC1=${device.address} Connected=${result}")
        return result
    }

    fun isConnected(macAddress: String): Boolean {
        var result = false
        for (i in mBleManagers.keys) {
            if (i.address == macAddress) {
                result = mBleManagers[i]?.isConnected ?: false
            }
        }
        Logger.e(TAG, "MAC2=${macAddress} Connected=${result}")
        return result
    }

    private var lastDevice: BluetoothDevice? = null
    private fun getBluetoothDevice(macAddress: String): BluetoothDevice? {
        if (lastDevice?.address == macAddress) {
            return lastDevice
        }
        for (i in mBleManagers.keys) {
            if (i.address == macAddress) {
                lastDevice = i
                return i
            }
        }
        return null
    }

    fun getBleState(macAddress: String): Int {
        for (i in mBleManagers.keys) {
            if (i.address == macAddress) {
                return when (mBleManagers[i]?.connectionState) {
                    BluetoothGatt.STATE_CONNECTED -> R.string.ble_connected
                    BluetoothGatt.STATE_CONNECTING -> R.string.ble_connecting
                    BluetoothGatt.STATE_DISCONNECTING -> R.string.ble_disconnecting
                    else -> R.string.ble_disconnected
                }
            }
        }
        return R.string.ble_disconnected
    }

    fun setConnectionObserver(device: BluetoothDevice, lifecycleOwner: LifecycleOwner, observer: Observer<ConnectionState>) {
        mBleManagers[device]?.getConnectionStateLiveData()?.observe(lifecycleOwner, observer)
    }

    fun removeConnectionObserver(device: BluetoothDevice, observer: Observer<ConnectionState>) {
        mBleManagers[device]?.getConnectionStateLiveData()?.removeObserver(observer)
    }

    fun removeConnectionObserver(device: BluetoothDevice, lifecycleOwner: LifecycleOwner) {
        mBleManagers[device]?.getConnectionStateLiveData()?.removeObservers(lifecycleOwner)
    }

    fun setLockBleManagerStateObserver(lifecycleOwner: LifecycleOwner?, action: (LockMutableBleStatusLiveData) -> Unit) {
        if (lifecycleOwner == null) {
            mLockMutableBleStatusLiveData.observeForever {
                action(it)
            }
        } else {
            mLockMutableBleStatusLiveData.observe(lifecycleOwner) {
                action(it)
            }
        }
    }

    fun setReceivedDataObserver(lifecycleOwner: LifecycleOwner, action: (LockBleReceivedLiveData) -> Unit) {
        mMessageApi.setReceivedDataObserver(lifecycleOwner, action)
    }

}