package com.datangic.smartlock.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.datangic.easypermissions.EasyPermissions
import com.datangic.smartlock.MainActivity
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.ManagerPagerAdapter
import com.datangic.smartlock.ble.ReceivedMessageHandle
import com.datangic.smartlock.components.CardSetting
import com.datangic.smartlock.components.DeviceItem
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.smartlock.databinding.FragmentHomeBinding
import com.datangic.smartlock.parcelable.IntentExtra
import com.datangic.smartlock.ui.manager.ManagerActivity
import com.datangic.smartlock.ui.setting.SettingActivity
import com.datangic.smartlock.utils.INTENT_EXTRA
import com.datangic.smartlock.utils.Logger
import com.datangic.smartlock.viewModels.FragmentHomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.java.simpleName
    private val mViewModel: FragmentHomeViewModel by sharedViewModel()
    lateinit var mBinding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            mToolbarProgress = mViewModel.mHomeFragmentRepository.mHomeToolbar
            onSearchClick = mViewModel.getOnSearchClick(this@HomeFragment)
            onScanClick = mViewModel.getOnScanClick(this@HomeFragment)
        }
        mViewModel.setToolBar(mBinding.homeToolbar.toolbar, requireActivity())
        mViewModel.setBanner(mBinding.banner, requireActivity())
        mBinding.banner.layoutParams.height = (mViewModel.getHeight(requireActivity()) * .55).toInt()
        setObserve()
    }

    private val mManagerPagerClick = object : ManagerPagerAdapter.OnManagerPagerClick {
        override fun onLockNameClick(view: View, deviceViewManagerDevice: ViewManagerDevice, cardSetting: CardSetting) {
            mViewModel.getDevicesBottomSheetDialog(this@HomeFragment.requireContext(), deviceViewManagerDevice).let {
                it.setOnCancelListener {
                    cardSetting.open = false
                }
                if (it.isShowing) {
                    it.cancel()
                } else {
                    it.show()
                }
            }
        }

        override fun onSettingsClick(view: View, deviceViewManagerDevice: ViewManagerDevice) {
            startActivity(Intent(this@HomeFragment.requireContext(), SettingActivity::class.java).apply {
                mViewModel.mBleManagerApi.setDefaultDeviceInfo(deviceViewManagerDevice.serialNumber, deviceViewManagerDevice.macAddress)
                putExtra(
                    INTENT_EXTRA, IntentExtra(
                        deviceViewManagerDevice.macAddress,
                        deviceViewManagerDevice.serialNumber,
                        deviceViewManagerDevice.deviceUserID.first
                    )
                )
            })
        }

        override fun onBleClick(view: View, deviceViewManagerDevice: ViewManagerDevice) {
            if (mViewModel.mBleManagerApi.isConnected(macAddress = deviceViewManagerDevice.macAddress)) {
                mViewModel.mBleManagerApi.disconnect(deviceViewManagerDevice.macAddress)
            } else {
                mViewModel.mBleManagerApi.connectWithRegister(
                    deviceViewManagerDevice.macAddress,
                    this@HomeFragment,
                    ReceivedMessageHandle.RegisterType.NORMAL_REGISTER
                )
            }
        }

        override fun onManagerOperation(view: View, deviceViewManagerDevice: ViewManagerDevice, item: DeviceItem) {
            startActivity(Intent(this@HomeFragment.requireContext(), ManagerActivity::class.java).apply {
                mViewModel.mBleManagerApi.setDefaultDeviceInfo(deviceViewManagerDevice.serialNumber, deviceViewManagerDevice.macAddress)
                putExtra(
                    INTENT_EXTRA, IntentExtra(
                        macAddress = deviceViewManagerDevice.macAddress,
                        serialNumber = deviceViewManagerDevice.serialNumber,
                        userID = deviceViewManagerDevice.deviceUserID.first,
                        selectedType = item.icon,
                        hasNFC = deviceViewManagerDevice.nfc,
                        hasFace = deviceViewManagerDevice.face
                    )
                )
            })
        }
    }

    @ObsoleteCoroutinesApi
    private fun setObserve() {
        mViewModel.mBleManagerApi.setDevicesViewObserver(this) {
            if (it.isNotEmpty()) {
                (requireActivity() as MainActivity).localPasswordRepository.getNewPasswordDialog(
                    requireActivity(),
                    R.string.password
                )
                Logger.e(TAG, "Change size =${it.size} mDefault=${mViewModel.mBleManagerApi.mDefaultDeviceInfo}")
                mViewModel.managerDeviceObserverHandle(mBinding.managerPager, this, it, mManagerPagerClick)
                lifecycleScope.launch(Dispatchers.Main) {
                    if (mBinding.scanCardView.isVisible) {
                        mBinding.banner.layoutParams.height = (mViewModel.getHeight(requireActivity()) * .37).toInt()
                        mBinding.scanCardView.visibility = View.GONE
                        mBinding.managerPager.visibility = View.VISIBLE
                    }
                }
                if (activity is MainActivity) {
                    (activity as MainActivity).apply {
                        mFabListener = object : MainActivity.FabListener {
                            override fun hasDevice(): Boolean {
                                return true
                            }

                            override fun onClick() {
                                val position = this@HomeFragment.mBinding.managerPager.currentItem
                                mViewModel.fabOnClick(it[position].macAddress, it[position].imei)
                            }
                        }
                        showFab()
                    }
                }
            } else {
                if (mBinding.scanCardView.isGone) {
                    mBinding.banner.layoutParams.height = (mViewModel.getHeight(requireActivity()) * .55).toInt()
                    mBinding.scanCardView.visibility = View.VISIBLE
                    mBinding.managerPager.visibility = View.GONE
                }
                (activity as MainActivity).apply {
                    mFabListener = object : MainActivity.FabListener {
                        override fun hasDevice(): Boolean {
                            return false
                        }

                        override fun onClick() {}
                    }
                    mBinding.homeFab.hide()
                }
            }
        }


        mViewModel.mBleManagerApi.setLockBleManagerStateObserver(this@HomeFragment) { it1 ->
            mViewModel.bleManagerStateObserverHandle(it1)
        }
//        Thread.sleep(120)
        mViewModel.setDefaultDevice(this, mBinding.managerPager)
    }

    override fun onDestroy() {
        Logger.e(TAG, "Destroy")
        super.onDestroy()
    }

//    override fun onResume() {
//        super.onResume()
//        mViewModel.setDefaultDevice(mBinding.managerPager)
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            mViewModel.mScanQRCode.getPermissionCallbacks(this.requireActivity())
        )
    }
}