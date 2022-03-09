package com.datangic.smartlock.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentVersionInfoBinding
import com.datangic.smartlock.viewModels.FragmentVersionInfoViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class VersionInfoFragment : Fragment() {
    private lateinit var mBinding: FragmentVersionInfoBinding
    private lateinit var mViewModel: FragmentVersionInfoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_version_info, container, false)
        arguments?.let {
            mViewModel = getViewModel {
                parametersOf(
                        VersionInfoFragmentArgs.fromBundle(it).StringArgumentMacAddress,
                        VersionInfoFragmentArgs.fromBundle(it).StringArgumentSerialNumber
                )
            }
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.setInfo(this, mBinding)
    }

}