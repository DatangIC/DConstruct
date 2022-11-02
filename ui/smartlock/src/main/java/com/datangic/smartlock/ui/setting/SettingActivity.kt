package com.datangic.smartlock.ui.setting

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.datangic.smartlock.R
import com.datangic.smartlock.ble.ReceivedMessageHandle
import com.datangic.smartlock.ble.livedata.state.ConnectionState
import com.datangic.smartlock.databinding.ActivitySettingBinding
import com.datangic.smartlock.liveData.LockMutableBleStatusLiveData.Companion.getStatusMessage
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.respositorys.ToolbarRepository
import com.datangic.smartlock.ui.scanning.ScanActivity
import com.datangic.common.utils.Logger
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SettingActivity : AppCompatActivity() {
    private val TAG = ScanActivity::class.simpleName

    lateinit var mBinding: ActivitySettingBinding
    val mToolbarRepository: ToolbarRepository by inject()
    val mBleRepository: BleManagerApiRepository by inject()
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.e(TAG, "onCreateView")
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        lifecycleScope.launch {
            mToolbarRepository.initToolbarWithBack(this@SettingActivity, mBinding.settingToolbar.toolbar)
            mBinding.scanToolBarData = mToolbarRepository.mScannerActivityToolBar
            mNavController = Navigation.findNavController(this@SettingActivity, R.id.nav_setting_fragment)
            mNavController.addOnDestinationChangedListener { _, destination, _ ->
                mToolbarRepository.mScannerActivityToolBar.title = destination.label!!
            }
            this@SettingActivity.onBackPressedDispatcher.addCallback(this@SettingActivity, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (mNavController.currentDestination?.id == R.id.settingFragment) {
                        finish()
                    } else {
                        mNavController.popBackStack()
                    }
                }
            })
        }

        mBleRepository.setLockBleManagerStateObserver(this) {
            if (it.macAddress == mBleRepository.mDefaultDeviceInfo?.second) {
                mBinding.bleTips.text = it.mConnectionState.getStatusMessage(this)
                if (it.mConnectionState.state in listOf(
                        ConnectionState.State.READY,
                        ConnectionState.State.DISCONNECTED,
                        ConnectionState.State.STOP_SCAN
                    )
                )
                    isConnect()
            }
        }
        isConnect()
        mBinding.bleTips.setOnClickListener {
            mBleRepository.connectWithRegister(
                mBleRepository.mDefaultDeviceInfo?.second!!,
                this,
                ReceivedMessageHandle.RegisterType.NORMAL_REGISTER
            )
        }
    }

    private fun isConnect() {
        mBleRepository.mDefaultDeviceInfo?.let { pair ->
            if (mBleRepository.isConnected(pair.second)) {
                mBinding.bleTips.visibility = View.GONE
            } else {
                mBinding.bleTips.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        if (mToolbarRepository.mOnBackPressed != null) {
            mToolbarRepository.mOnBackPressed?.let {
                if (it())
                    mToolbarRepository.mOnBackPressed = null
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}