package com.datangic.libs.base.dataSource

import android.app.Application
import com.datangic.api.GsonUtils
import com.datangic.api.LockApiObservable
import com.datangic.api.Page
import com.datangic.api.smartlock.*
import com.datangic.common.utils.Logger
import com.datangic.data.DatabaseRepository
import com.datangic.data.LogStatus
import com.datangic.data.database.table.Device
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.table.DeviceStatus
import com.datangic.data.database.table.DeviceUser
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.network.AppExecutors
import com.datangic.network.RequestStatus
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch

class DeviceSource(val application: Application, val mDatabase: DatabaseRepository, private val appExecutors: AppExecutors = AppExecutors()) {
    private var mLockApi: SmartLockApi? = null
    private val mNetLockList: MutableMap<String, NetLock<NetLockSource<NetLockUser>>> = mutableMapOf()
    private val mLocalLockList: MutableList<ViewManagerDevice> = mutableListOf()
    private val mGson by lazy { GsonUtils.getGson() }
    private var synStatus = Pair(false, false)


    init {
        mDatabase.setLogUserObservable(null) { user ->
            user.authorization?.let { auth ->

                if (auth.isNotEmpty() && user.status == LogStatus.LOGGED) {
                    mLockApi = SmartLockApi.create { return@create auth }
                    getNetLocks()
                }
            }
        }
        mDatabase.setManagerDevicesViewObserver(null) {
            MainScope().launch(appExecutors.singleIO().asCoroutineDispatcher()) {
                it?.let { devices ->
                    mLocalLockList.addAll(devices)
                    synStatus = Pair(synStatus.first, true)
                    syncMainInfoDevice()
                }
            }
        }
    }

    private fun getNetLocks() {
        mLockApi?.let { lockApi ->
            LockApiObservable(lockApi.getDevs()).subscribeOn(Schedulers.io()).subscribe { res ->
                when (res.requestStatus) {
                    RequestStatus.SUCCESS -> {
                        MainScope().launch(Dispatchers.IO) {
                            res.data?.let { data ->
                                val page = mGson.fromJson<Page<NetLock<NetLockSource<NetLockUser>>>>(
                                    data,
                                    object : TypeToken<Page<NetLock<NetLockSource<NetLockUser>>>>() {}.type
                                )
                                Logger.e("Device", "net list size=${page.list.size}")
                                for (netDev in page.list) {
                                    mNetLockList[netDev.devNo] = netDev
                                }
                                synStatus = Pair(true, synStatus.second)
                                syncMainInfoDevice()
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun syncMainInfoDevice() {
        if (synStatus != Pair(true, true)) return
        val pushList: MutableList<ViewManagerDevice> = mutableListOf()
        val pullMap: MutableMap<String, NetLock<NetLockSource<NetLockUser>>> = mutableMapOf()
        MainScope().launch(appExecutors.singleIO().asCoroutineDispatcher()) {
            for (device in mNetLockList) {
                pullMap[device.key] = device.value
            }
            for (device in mLocalLockList) {
                if (mNetLockList[device.serialNumber] == null) {
                    pushList.add(device)
                } else {
                    pullMap.remove(device.serialNumber)
                    if (device.deviceDirty) {
                        pushList.add(device)
                    }
                }
            }
            for (view in pushList)
                updateNetworkDevice(view)
            for (netlock in pullMap)
                createNewDeviceAndUser(mDatabase.getLogUser()?.userId ?: 0, netlock.value)
        }
    }

    private fun createNewDeviceAndUser(userId: Int, netLock: NetLock<NetLockSource<NetLockUser>>) {
        var mainDeviceUserID = userId
        try {
//            Logger.e("Desource", "Insert NetLock1 ===${netLock.productResource.users}")
            Logger.e("Device ", "下载新设备device=${netLock.devNo}")
            for (user in netLock.productResource.users) {
                if (user.phoneNumber == mDatabase.getLogUser()?.userPhone) {
                    mainDeviceUserID = user.userId
                    break
                }
            }
            val newDevice = Device(
                uid = userId,
                synNetwork = true,
                name = netLock.devName,
                serialNumber = netLock.devNo,
                macAddress = netLock.productResource.mac,
                secretCode = netLock.productResource.secretCode,
                deviceUserID = Pair(mainDeviceUserID, netLock.devNo),
                imei = netLock.productResource.imei,
                createAt = netLock.productResource.createTime,
                updateAt = (System.currentTimeMillis() / 1000),
                dirty = false
            )
            mDatabase.mDatabase.deviceDao().insert(newDevice)
            val newDeviceStatus = DeviceStatus(
                deviceUserID = Pair(mainDeviceUserID, netLock.devNo),
                serialNumber = netLock.devNo,
                macAddress = netLock.productResource.mac,
            )
            mDatabase.mDatabase.deviceStatusDao().insert(newDeviceStatus)

            for (user in netLock.productResource.users) {
                val newDeviceUser = DeviceUser(
                    deviceUsername = user.userName,
                    serialNumber = netLock.devNo,
                    macAddress = netLock.productResource.mac,
                    deviceUserId = Pair(user.userId, netLock.devNo),
                    authCode = user.authCode,
                    administrator = user.isAdmin == 1,
                    userStatus = DeviceEnum.DeviceUserStatus.UNKNOWN,
                    createAt = user.createTime,
                    updateAt = (System.currentTimeMillis() / 1000),
                    dirty = false
                )
                mDatabase.mDatabase.deviceUserDao().insert(newDeviceUser)
            }

        } catch (e: Exception) {
            Logger.e(":Desource", "err=$e")
        }
    }

    fun updateNetworkDevice(device: ViewManagerDevice) {
        mDatabase.mDatabase.getDeviceUser(device.serialNumber, device.macAddress, device.deviceUserID.first)?.let { deviceUser ->
            val netUser = NetLockUser(
                isAdmin = if (device.administrator) 1 else 0,
                authCode = device.authCode,
                userName = deviceUser.deviceUsername,
                userId = device.deviceUserID.first,
                createTime = device.createAt,
                phoneNumber = mDatabase.getLogUser()?.userPhone ?: ""
            )
            val netLockSource: NetLockSource<NetLockUser> = NetLockSource(
                secretCode = device.secretCode ?: "",
                imei = device.imei,
                sn = device.serialNumber,
                mac = device.macAddress,
                createTime = device.createAt,
                battery = device.battery,
                users = mutableListOf(netUser)
            )
            val postNetDevice: PostNetDevice<NetLockSource<NetLockUser>> = PostNetDevice(
                devName = device.name,
                devNo = device.serialNumber,
                productResource = netLockSource
            )
            mLockApi?.let { lockApi ->
                LockApiObservable(lockApi.updateDev(postNetDevice)).subscribeOn(Schedulers.io()).subscribe { res ->
                    if (res.requestStatus == RequestStatus.SUCCESS) {
                        res.data?.let { productResource ->
                            mDatabase.synUpdate(device.serialNumber, device.macAddress, device.deviceUserID.first, true)
                            val resource = mGson.fromJson<NetLockSource<NetLockUser>>(
                                productResource,
                                object : TypeToken<NetLockSource<NetLockUser>>() {}.type
                            )
                            mNetLockList[device.serialNumber] = NetLock(
                                devId = 0,
                                devName = device.name,
                                devNo = device.serialNumber,
                                productResource = resource
                            )
                        }
                    }
                }
            }
        }
    }

    fun getDeviceLog(serialNumber: String, macAddress: String) {

    }
}

