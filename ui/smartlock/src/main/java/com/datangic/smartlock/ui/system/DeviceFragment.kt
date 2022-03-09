package com.datangic.smartlock.ui.system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.PagerListViewBinding
import com.datangic.smartlock.viewModels.FragmentDeviceViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DeviceFragment : Fragment() {
    private val TAG = DeviceFragment::class.simpleName

    lateinit var mBinding: PagerListViewBinding
    val mViewModel: FragmentDeviceViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.pager_list_view, container, false)
        mViewModel.setObserver(this)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as SystemActivity).mRetrieveClick = object : () -> Unit {
            override fun invoke() {
                mViewModel.getSheetDialog(this@DeviceFragment, title = R.string.retrieve).show()
            }

        }
        mBinding.apply {
            newItem = mViewModel.mTitle
            newKeyOnClick = View.OnClickListener {
                mViewModel.getSheetDialog(this@DeviceFragment, title = R.string.new_device).show()
            }
            mBinding.keysList.adapter = mViewModel.mAdapter
        }
    }
}