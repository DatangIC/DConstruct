package com.datangic.smartlock.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.fragment.app.Fragment
import androidx.work.impl.utils.ForceStopRunnable
import com.datangic.common.Config
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.ItemClickListener
import com.datangic.smartlock.adapter.RecycleItemAdapter
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.data.database.table.Device
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.*

class FragmentWifiViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    bleManagerApiRepository: BleManagerApiRepository
) : BaseViewModel(application, bleManagerApiRepository) {
    private val TAG by lazy {
        FragmentWifiViewModel::class.simpleName
    }

    val mAdapter = RecycleItemAdapter(itemType = RecycleItemAdapter.ItemType.WiFiItem)
    var mDevice: Device? = null
    val mDeviceLiveData = mBleManagerApi.getDeviceLiveData()

    fun initWifiManager(fragment: Fragment) {
        mAdapter.mOnClickListener = mItemOnClick(fragment)

        mDeviceLiveData.observe(fragment.viewLifecycleOwner) {
            mDevice = it
        }
        val wifiManager = fragment.requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val wifiScanReceiver = @SuppressLint("RestrictedApi")
        object : ForceStopRunnable.BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success == true) {
                    scanSuccess(wifiManager)
                } else {
                    scanFailure(wifiManager)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        fragment.requireContext().registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            scanFailure(wifiManager)
        }
    }


    private fun scanSuccess(wifiManager: WifiManager) {
        mAdapter.mItemList = wifiManager.scanResults
    }

    private fun scanFailure(wifiManager: WifiManager) {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        mAdapter.mItemList = wifiManager.scanResults
    }

    private fun mItemOnClick(fragment: Fragment) = object : ItemClickListener {
        override fun onItemClick(item: Any) {
            if (item is ScanResult) {
                MaterialDialog.getWifiAddInfoDialog(
                    fragment.requireContext(),
                    icon = null,
                    title = R.string.title_wifi,
                    wifi = item.SSID,
                    isDebug = if (mDevice?.secretCode in Config.HAS_SERVER_IP) false else mSystemSetting.debug
                ) { ip, port, pwd ->
                    CreateMessage.createMessage55(
                        macAddress, item.BSSID, item.SSID,
                        ip, port, pwd
                    ).execute()
                    showLoadingDialog()
                }.show()
            }
        }
    }

    override fun handle5E(type: Byte, errorCode: Int) {
        cancelLoadingDialog()
        when (errorCode) {
            MSG5E_WifiSuccess -> {
                CreateMessage.createMessage49(macAddress, type = MSG49_TYPE_GetWifiStatus).execute()
            }
            MSG5E_WifiFailure -> {
                MaterialDialog.getAlertDialog(
                    mActivity,
                    message = R.string.dialog_wifi_set_error,
                    isError = true,
                    isCancel = false
                ).show()
            }
        }
    }
}