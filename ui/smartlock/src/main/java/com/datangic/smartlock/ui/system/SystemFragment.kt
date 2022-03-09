package com.datangic.smartlock.ui.system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentSettingBinding
import com.datangic.smartlock.viewModels.FragmentSystemViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SystemFragment : Fragment() {

    private lateinit var mBinding: FragmentSettingBinding

    val mViewModel: FragmentSystemViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        mViewModel.setObserver(this)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.settings.adapter = mViewModel.mAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var count = 0
        (requireActivity() as SystemActivity).mBinding.layoutToolbar.toolbar.setOnClickListener {
            if (count < 5) {
                count += 1
            } else {
                mViewModel.addTestItem()
                count = 0
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.onResume()
    }
}