package com.datangic.smartlock.ui.manager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.datangic.smartlock.R
import com.datangic.smartlock.ble.ReceivedMessageHandle
import com.datangic.smartlock.ble.livedata.state.ConnectionState
import com.datangic.smartlock.databinding.ActivityManagerBinding
import com.datangic.smartlock.liveData.LockMutableBleStatusLiveData.Companion.getStatusMessage
import com.datangic.smartlock.parcelable.IntentExtra
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.respositorys.ToolbarRepository
import com.datangic.smartlock.utils.INTENT_EXTRA
import com.datangic.smartlock.utils.Logger
import org.koin.android.ext.android.inject

class ManagerActivity : AppCompatActivity() {
    private val TAG = ManagerActivity::class.simpleName
    lateinit var mBinding: ActivityManagerBinding
    val mToolbarRepository: ToolbarRepository by inject()
    val mBleRepository: BleManagerApiRepository by inject()
    private lateinit var mNavController: NavController

    private var mMenuDeleteItem: MenuItem? = null
    private var mMenu: Int = 0

    var mMenuClick: OnMenuClick? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manager)

        mToolbarRepository.initToolbarWithBack(this, mBinding.managerToolbar.toolbar)
        mBinding.managerToolbar.stateProgress.visibility = View.GONE
        mBinding.scanToolBarData = mToolbarRepository.mScannerActivityToolBar
        mNavController = Navigation.findNavController(this, R.id.nav_manager_fragment)
        mNavController.addOnDestinationChangedListener { _, destination, _ ->
            mToolbarRepository.mScannerActivityToolBar.title = destination.label ?: ""
        }
        intent.getParcelableExtra<IntentExtra>(INTENT_EXTRA)?.let {
            when (it.selectedType) {
                R.drawable.ic_management_temp_pwd -> {
                    mNavController.navigate(R.id.navigationManagerTempKeys)
                }
                R.drawable.ic_management_record -> {
                    mMenu = R.menu.toolbar_log_menu
                    mNavController.navigate(R.id.navigationManagerRecord)
                }
                R.drawable.ic_management_users -> {
                    if (mNavController.currentDestination?.id in listOf(
                            R.id.navigationManagerKeys,
                            R.id.navigationUserLifecycle
                        )
                    ) return@let
                    mNavController.navigate(R.id.navigationManagerUser)
                }
                R.drawable.ic_management_face,
                R.drawable.ic_management_password,
                R.drawable.ic_management_nfc,
                R.drawable.ic_management_fingerprint -> {
                    mNavController.navigate(R.id.navigationManagerKeys)
                }
            }
        }

        mBleRepository.setLockBleManagerStateObserver(this) {
            if (intent.getParcelableExtra<IntentExtra>(INTENT_EXTRA)?.selectedType == R.drawable.ic_management_temp_pwd) return@setLockBleManagerStateObserver

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
        if (intent.getParcelableExtra<IntentExtra>(INTENT_EXTRA)?.selectedType == R.drawable.ic_management_temp_pwd) {
            mBinding.bleTips.visibility = View.GONE
            return
        }
        mBleRepository.mDefaultDeviceInfo?.let { pair ->
            if (mBleRepository.isConnected(pair.second)) {
                mBinding.bleTips.visibility = View.GONE
            } else {
                mBinding.bleTips.visibility = View.VISIBLE
            }
        } ?: let {
            Logger.e(TAG, "DefaultDevice=${mBleRepository.mDefaultDeviceInfo}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        when (mMenu) {
            R.menu.toolbar_log_menu -> {
                menuInflater.inflate(R.menu.toolbar_log_menu, menu)
                mMenuDeleteItem = menu.findItem(R.id.delete)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                if (item.title == resources.getString(R.string.delete)) {
                    item.setIcon(R.drawable.ic_edit_back)
                    item.setTitle(R.string.back)
                    mMenuClick?.onDelete()
                } else {
                    item.setTitle(R.string.delete)
                    item.setIcon(R.drawable.ic_delete)
                    mMenuClick?.onClose()
                }
            }
//            R.id.album -> {
//                mMenuClick?.onAlbum()
//            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initMenu() {
        when (mMenu) {
            R.menu.toolbar_log_menu -> {
                mMenuDeleteItem?.let {
                    it.setTitle(R.string.delete)
                    it.setIcon(R.drawable.ic_delete)
                }
            }
            else -> {
            }
        }

    }

    interface OnMenuClick {
        fun onDelete()
        fun onAlbum()
        fun onClose()
    }


    override fun onBackPressed() {
        if (mToolbarRepository.mOnBackPressed != null) {
            mToolbarRepository.mOnBackPressed?.let {
                mToolbarRepository.mOnBackPressed = null
                if (it())
                    super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}