package com.datangic.smartlock.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.PagerListViewBinding
import com.datangic.smartlock.parcelable.IntentExtra
import com.datangic.smartlock.utils.INTENT_EXTRA
import com.datangic.common.utils.Logger
import com.datangic.smartlock.viewModels.FragmentManagerUserViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class UserFragment : Fragment() {

    private val TAG = "UserFragment"
    lateinit var mBinding: PagerListViewBinding
    private lateinit var mViewModel: FragmentManagerUserViewModel
    private lateinit var args: IntentExtra

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.pager_list_view, container, false)
        requireActivity().intent.getParcelableExtra<IntentExtra>(INTENT_EXTRA)?.let {
            args = it
            mViewModel = getViewModel {
                parametersOf(
                    it.macAddress,
                    it.serialNumber,
                    it.userID
                )
            }
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Logger.e(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        mViewModel.setMessageListener(this)
        with(mBinding) {
            newItem = mViewModel.mTitle
            newKeyOnClick = mViewModel.addNewUser(requireContext())
        }
        mBinding.keysList.adapter = mViewModel.mAdapter
    }
}