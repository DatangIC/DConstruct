package com.datangic.smartlock.ui.scanning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.datangic.smartlock.databinding.FragmentVerifyingBinding
import com.datangic.smartlock.viewModels.FragmentVerifyingViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VerifyingFragment : Fragment() {

    private val TAG = VerifyingFragment::class.java.simpleName

    lateinit var mBinding: FragmentVerifyingBinding
    val mViewModel: FragmentVerifyingViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentVerifyingBinding.inflate(layoutInflater, container, false).apply {
            editView = false
        }
        arguments?.let {
            mViewModel.mScanQrCodeResult = VerifyingFragmentArgs.fromBundle(it).scanResult
            mViewModel.mScanBleResult = it.getParcelable("ble_device")
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.init(this, mBinding)
        mViewModel.connectDevice(this)
    }

}