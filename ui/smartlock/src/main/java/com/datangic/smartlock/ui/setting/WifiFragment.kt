package com.datangic.smartlock.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentSetWifiBinding
import com.datangic.smartlock.viewModels.FragmentWifiViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class WifiFragment : Fragment() {
    private lateinit var mBinding: FragmentSetWifiBinding
    private lateinit var mViewModel: FragmentWifiViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_wifi, container, false)
        arguments?.let { bundle ->
            mViewModel = getViewModel {
                parametersOf(
                        WifiFragmentArgs.fromBundle(bundle).StringArgumentMacAddress,
                        WifiFragmentArgs.fromBundle(bundle).StringArgumentSerialNumber
                )
            }
        }
        mViewModel.initWifiManager(this)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.scanningDevicesList.adapter = mViewModel.mAdapter
    }
}