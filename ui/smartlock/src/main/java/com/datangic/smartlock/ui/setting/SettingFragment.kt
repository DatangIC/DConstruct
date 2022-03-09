package com.datangic.smartlock.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentSettingBinding
import com.datangic.smartlock.parcelable.IntentExtra
import com.datangic.smartlock.utils.INTENT_EXTRA
import com.datangic.smartlock.viewModels.FragmentSettingViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class SettingFragment : Fragment() {
    private val TAG = SettingFragment::class.simpleName

    private lateinit var mViewModel: FragmentSettingViewModel
    private lateinit var mBinding: FragmentSettingBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        requireActivity().intent.getParcelableExtra<IntentExtra>(INTENT_EXTRA)?.let {
            mViewModel = getViewModel {
                parametersOf(
                        it.macAddress,
                        it.serialNumber
                )
            }
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.setObserve(this, mBinding.settings)
    }

    override fun onResume() {
        super.onResume()
        mViewModel.mBackPress = fun() {
            this.requireActivity().onBackPressed()
        }
    }
}