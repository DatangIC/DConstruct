package com.datangic.smartlock.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentCheckRepairBinding
import com.datangic.smartlock.viewModels.FragmentCheckRepairViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CheckRepairFragment : Fragment() {

    private val TAG = CheckRepairFragment::class.simpleName
    private lateinit var mBinding: FragmentCheckRepairBinding
    private lateinit var mViewModel: FragmentCheckRepairViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_check_repair, container, false)
        arguments?.let {
            mViewModel = getViewModel {
                parametersOf(
                        CheckRepairFragmentArgs.fromBundle(it).StringArgumentMacAddress,
                        CheckRepairFragmentArgs.fromBundle(it).StringArgumentSerialNumber
                )
            }
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.setAdapter(this, mBinding.checkView)

        mBinding.checkBtn.setOnClickListener {
            mViewModel.check()
        }
        mBinding.repairBtn.setOnClickListener {
            mViewModel.repair()
        }
        mBinding.repairBtn.visibility = View.GONE
        mViewModel.setListener(this, mBinding)

    }

    override fun onResume() {
        super.onResume()
        mViewModel.check()
    }
}