package com.datangic.smartlock.viewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.datangic.common.utils.Logger
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.BannerImageAdapter
import com.datangic.smartlock.adapter.DeviceWithBluetoothAdapter
import com.datangic.smartlock.adapter.ManagerPagerAdapter
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.components.CardSetting
import com.datangic.smartlock.components.DeviceWithBluetooth
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.liveData.LockMutableBleStatusLiveData
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.respositorys.HomeFragmentRepository
import com.datangic.smartlock.respositorys.ScanQrCodeHelper
import com.datangic.smartlock.ui.scanning.ScanActivity
import com.datangic.smartlock.utils.UtilsFormat.toDateString
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.indicator.RectangleIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentHomeViewModel(
    application: Application, val mHomeFragmentRepository: HomeFragmentRepository, mBleManagerApi: BleManagerApiRepository
) : BaseViewModel(application, mBleManagerApi) {
    private val TAG = FragmentHomeViewModel::class.simpleName

    private var mCardSettingList: MutableList<CardSetting> = ArrayList()
    private var mViewManagerDeviceList: List<ViewManagerDevice> = ArrayList()

    val mScanQRCode by lazy { ScanQrCodeHelper }

    fun setToolBar(toolbar: MaterialToolbar, activity: FragmentActivity) {
        val width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.windowManager.currentWindowMetrics.bounds.width()
        } else {
            activity.windowManager.defaultDisplay.width
        }
        toolbar.titleMarginStart = (width / 2.3).toInt()
    }

    fun setBanner(banner: Banner<Any, BannerAdapter<Any, out RecyclerView.ViewHolder>>, activity: FragmentActivity) {
        banner.apply {
            setAdapter(BannerImageAdapter(mHomeFragmentRepository.getBannerList()), true)
            indicator = RectangleIndicator(activity.baseContext)
            setLoopTime(10 * 1000)
            addBannerLifecycleObserver(activity)
        }
    }

    fun getHeight(activity: FragmentActivity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.windowManager.currentWindowMetrics.bounds.height()
        } else {
            activity.windowManager.defaultDisplay.height
        }
    }

    fun getOnSearchClick(fragment: Fragment): View.OnClickListener {
        return View.OnClickListener {
            Logger.i(TAG, "getOnSearchClick")
            fragment.startActivity(Intent(fragment.requireContext(), ScanActivity::class.java))
        }
    }


    fun getOnScanClick(fragment: Fragment): View.OnClickListener {
        return View.OnClickListener {
            mScanQRCode.onScanQrCode(fragment.requireActivity())
        }
    }


    fun managerDeviceObserverHandle(
        pager: ViewPager2, fragment: Fragment, list: List<ViewManagerDevice>, mManagerPagerClick: ManagerPagerAdapter.OnManagerPagerClick
    ) {
        mViewManagerDeviceList = list
        if (pager.adapter == null) {
            pager.adapter = ManagerPagerAdapter(fragment.requireContext(), list, getCardSetting(list))
        } else {
            (pager.adapter as ManagerPagerAdapter).apply {
                setDevicesAndCards(list, getCardSetting(list))
            }
        }
        (pager.adapter as ManagerPagerAdapter).mOnManagerPagerClick = mManagerPagerClick
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var isScrolledDone = false
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (isScrolledDone && mViewManagerDeviceList.size > position) {
                    isScrolledDone = false
                    mBleManagerApi.setDefaultDeviceInfo(
                        mViewManagerDeviceList[position].serialNumber, mViewManagerDeviceList[position].macAddress
                    )
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == 2) {
                    isScrolledDone = true
                }
            }
        })
        exchangeView(pager, delay = true)
    }

    private fun getCardSetting(list: List<ViewManagerDevice>): List<CardSetting> {
        if (list.size == mCardSettingList.size) {
            for (i in mCardSettingList.indices) {
                mCardSettingList[i].lockName = list[i].name
                mCardSettingList[i].battery = list[i].battery
                mCardSettingList[i].syncAt = list[i].updateAt.toDateString()
            }
        } else {
            mCardSettingList.clear()
            for (i in list) {
                mCardSettingList.add(
                    CardSetting(
                        macAddress = i.macAddress, lockName = i.name, battery = i.battery, bleStatus = mBleManagerApi.getBleState(i.macAddress)
                    )
                )
            }
        }
        return mCardSettingList
    }

    fun getDevicesBottomSheetDialog(context: Context, deviceViewManagerDevice: ViewManagerDevice): BottomSheetDialog {
        Logger.e(TAG, "Devices Size =${mBleManagerApi.mViewDevices.size}")
        val devicesBottomSheetDialog: BottomSheetDialog = MaterialDialog.getBottomSheetDialogWithLayout(context, R.layout.bottom_sheet_devices)
        val deviceWithBluetoothList: MutableList<DeviceWithBluetooth> = ArrayList()
        for (i in mBleManagerApi.mViewDevices) {
            deviceWithBluetoothList.add(
                DeviceWithBluetooth(
                    deviceName = i.name,
                    serialNumber = i.serialNumber,
                    macAddress = i.macAddress,
                    deviceUserId =i.deviceUserID.first,
                    isSelected = i.macAddress == deviceViewManagerDevice.macAddress,
                    connect = mBleManagerApi.isConnected(i.macAddress)
                )
            )
        }
        val deviceAdapter = DeviceWithBluetoothAdapter(deviceWithBluetoothList) { it1 ->
            mBleManagerApi.setDefaultDeviceInfo(it1.serialNumber, it1.macAddress)
            devicesBottomSheetDialog.cancel()
        }
        devicesBottomSheetDialog.findViewById<RecyclerView>(R.id.dialog_recycle_view)?.adapter = deviceAdapter
        return devicesBottomSheetDialog
    }

    fun setDefaultDevice(lifecycleOwner: LifecycleOwner, pager: ViewPager2) {
        mBleManagerApi.mDefaultDeviceInfoLiveData.observe(lifecycleOwner) {
            if (pager.isVisible) {
                exchangeView(pager)
            } else {
                return@observe
            }
        }

    }

    private fun exchangeView(pager: ViewPager2, list: List<ViewManagerDevice> = mBleManagerApi.mViewDevices, delay: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            var newPosition = -1
            try {
                mBleManagerApi.mDefaultDeviceInfo?.let { pair ->
                    for (i in list.indices) {
                        if (list[i].serialNumber == pair.first && list[i].macAddress == pair.second) {
                            newPosition = i
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.e(TAG, "Default Info =${e.message}")
            }
            if (delay) delay(500)
            withContext(Dispatchers.Main) {
                if (newPosition != -1 && pager.currentItem != newPosition) {
                    pager.setCurrentItem(newPosition, true)
                }

            }
        }
    }


    fun bleManagerStateObserverHandle(state: LockMutableBleStatusLiveData) {
        Logger.e(TAG, "state=$state")
        val macAddress = state.device?.address ?: state.macAddress
        if (macAddress != null) {
            var position: Int = -1
            for (i in mCardSettingList.indices) {
                if (mCardSettingList[i].macAddress == macAddress) {
                    position = i
                    break
                }
            }
            Logger.e(TAG, "position=$position")
            if (position == -1) {
                return
            }
            mCardSettingList[position].bleStatus = state.getStatusMessageByMac(mCardSettingList[position].macAddress, mApplication)

        }
    }

    fun fabOnClick(macAddress: String, imei: String) {
        CreateMessage.createMessage21(macAddress, imei).execute()
    }
}