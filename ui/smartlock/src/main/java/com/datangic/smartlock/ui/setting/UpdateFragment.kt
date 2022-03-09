package com.datangic.smartlock.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentUpdateBinding
import com.datangic.smartlock.viewModels.FragmentUpdateViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class UpdateFragment : Fragment() {
    private lateinit var mBinding: FragmentUpdateBinding
    private lateinit var mViewModel: FragmentUpdateViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_update, container, false)
        arguments?.let {
            mViewModel = getViewModel {
                parametersOf(
                        UpdateFragmentArgs.fromBundle(it).StringArgumentMacAddress,
                        UpdateFragmentArgs.fromBundle(it).StringArgumentSerialNumber
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