package com.datangic.smartlock.ui.scanning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.easypermissions.EasyPermissions
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentScanningBinding
import com.datangic.smartlock.utils.Logger
import com.datangic.smartlock.utils.REQUEST_SET_DEVICE_INFO
import com.datangic.smartlock.utils.REQUEST_SET_SECRET_CODE
import com.datangic.smartlock.utils.SCAN_FOR_ACTION
import com.datangic.smartlock.viewModels.FragmentScanningViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ScanningFragment : Fragment() {
    private val TAG = ScanningFragment::class.simpleName

    lateinit var mBinding: FragmentScanningBinding
    private val mViewModel: FragmentScanningViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scanning, container, false)
        mBinding.apply {
            scanningDevicesList.adapter = mViewModel.mScannerDeviceAdapter
            scanningButtonText = mViewModel.mScanningButtonName
            scanButtonClick = mViewModel.getButtonClickListener(this@ScanningFragment)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.setObserve(requireActivity(), mBinding.scanningButton)
        mViewModel.setItemsOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        if (this.requireActivity().intent.extras?.getInt(SCAN_FOR_ACTION, 0) == REQUEST_SET_SECRET_CODE) {
            mViewModel.mBleManagerApi.mScannerRepository.startScan(this, true)
        } else {
            mViewModel.mBleManagerApi.mScannerRepository.startScan(this)
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, mViewModel.mBleManagerApi.mScannerRepository.mPermissionCallbacks)
    }
}