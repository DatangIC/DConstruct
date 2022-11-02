package com.datangic.smartlock.viewModels

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewpager2.widget.ViewPager2
import com.datangic.common.utils.Logger
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.RecordPagerAdapter
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.table.DeviceUser
import com.datangic.data.database.view.ViewDeviceLog
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.*
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentRecordViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    val userID: Int,
    mBleManagerApi: BleManagerApiRepository
) : BaseViewModel(application, mBleManagerApi) {
    private val TAG = FragmentRecordViewModel::class.simpleName
    private val mTabList = listOf(R.string.record_lock, R.string.record_user)
    val adapter = RecordPagerAdapter(mTabList)
    private val mDeviceUserLiveData = mBleManagerApi.getDeviceUserLiveData(userID)
    var mDeviceUser: DeviceUser? = null


    val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            if (tab.position == 0 && tab.tag != true) {
                mHandler.postDelayed(
                    {
                        CreateMessage.createMessage31(
                            macAddress, if (mDeviceUser?.administrator == true) MSG31_QueryAllUserRecords else MSG31_QueryUserRecords,
                            userID
                        ).execute().also { state ->
                            if (state == CreateMessage.State.SUCCESS) tab.tag = true
                        }
                    }, 50
                )

            } else if (tab.position == 1 && tab.tag != true) {
                CreateMessage.createMessage31(
                    macAddress, if (mDeviceUser?.administrator == true) MSG31_QueryAllUserEvents else MSG31_QueryUserEvents,
                    userID
                ).execute().also { state ->
                    if (state == CreateMessage.State.SUCCESS) tab.tag = true
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            Logger.e(TAG, "onTabUnselected  ${tab.text}")
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            Logger.e(TAG, "onTabReselected  ${tab.text}")
        }
    }


     fun setMessageListener(fragment: Fragment) {
        mDeviceUserLiveData.observe(fragment.viewLifecycleOwner) { user ->
            if (user == null) return@observe
            mDeviceUser = user
            val mLogLiveData: LiveData<List<ViewDeviceLog>> = if (user.administrator) {
                Logger.e(TAG, "admin")
                mBleManagerApi.getDeviceLogsLiveData(serialNumber = serialNumber, macAddress = macAddress)
            } else {
                Logger.e(TAG, "normal")
                mBleManagerApi.getDeviceLogsLiveData(userID, serialNumber = serialNumber, macAddress = macAddress)
            }
            mLogLiveData.observe(fragment.viewLifecycleOwner) {
                adapter.setLogList(it)
            }
        }
    }

    fun onDelete(position: Int): Boolean {
        if (mBleManagerApi.isConnected(macAddress)) {
            adapter.getSelectedItems(position).forEach { item ->
                CreateMessage.createMessage33(
                    macAddress,
                    if (item.logState == DeviceEnum.LogState.UNLOCK)
                        MSG33_TYPE_DeleteOneLOCKLog else {
                        MSG33_TYPE_DeleteOneUSERLog
                    },
                    userID,
                    item.logId
                ).execute()
                GlobalScope.launch(Dispatchers.IO) {
                    mBleManagerApi.deleteDeviceLog(item.deviceUserID.first, item.logId, item.logState)
                }
            }
            mHandler.postDelayed({ adapter.setClose(mTabList[position] == R.string.record_lock) }, 30)
            return true
        } else {
            return false
        }
    }

    /**
     * TabLayout
     */
    fun setTabLayTabLayoutMediator(tabLayout: TabLayout, viewPager: ViewPager2) {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            (viewPager.adapter as RecordPagerAdapter).let {
                when (it.mTabList[position]) {
                    is Int -> {
                        tab.setText(it.mTabList[position] as Int)
                    }
                    is String -> {
                        tab.text = it.mTabList[position] as String
                    }
                }
            }
        }.attach()
    }

    fun setCheckBox(checkBox: MaterialCheckBox) {
        adapter.mCheckCount = object : RecordPagerAdapter.OnCheckCount {
            override fun onCheck(count: RecordPagerAdapter.Count) {
                when (count) {
                    RecordPagerAdapter.Count.ALL,
                    RecordPagerAdapter.Count.COUNT -> checkBox.isChecked = true
                    RecordPagerAdapter.Count.CLEAR -> checkBox.isChecked = false
                }
            }
        }
    }
}