package com.datangic.smartlock.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentViewpager2WithTitleBinding
import com.datangic.smartlock.parcelable.IntentExtra
import com.datangic.smartlock.utils.INTENT_EXTRA
import com.datangic.smartlock.viewModels.FragmentRecordViewModel
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class RecordFragment : Fragment() {
    lateinit var mBinding: FragmentViewpager2WithTitleBinding
    private lateinit var args: IntentExtra
    private lateinit var mViewModel: FragmentRecordViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_viewpager2_with_title, container, false)
        requireActivity().intent.getParcelableExtra<IntentExtra>(INTENT_EXTRA)?.let {
            args = it
            mViewModel = getViewModel {
                parametersOf(
                        it.macAddress,
                        it.serialNumber,
                        it.userID)
            }
        }
        mViewModel.setMessageListener(this)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.pagerLayout.adapter = mViewModel.adapter
        mBinding.tabLayout.addOnTabSelectedListener(mViewModel.onTabSelectedListener)
        mViewModel.setTabLayTabLayoutMediator(mBinding.tabLayout, mBinding.pagerLayout)

        (requireActivity() as ManagerActivity).mMenuClick = menuClick(true)

        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                (requireActivity() as ManagerActivity).let {
                    it.mMenuClick = menuClick(tab.position == 0)
                    it.initMenu()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                mViewModel.adapter.setClose(tab.position == 0)
                mBinding.deleteBar.visibility = View.GONE
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        mBinding.deleteButton.setOnClickListener {
            if (mViewModel.onDelete(mBinding.tabLayout.selectedTabPosition)) {
                mBinding.deleteBar.visibility = View.GONE
                (requireActivity() as ManagerActivity).initMenu()
            }
        }
        with(mBinding.checkBox) {
            mViewModel.setCheckBox(this)
            this.setOnClickListener {
                mViewModel.adapter.setCheckAll(mBinding.tabLayout.selectedTabPosition == 0, this.isChecked)
            }
        }
    }

    fun menuClick(isLock: Boolean) = object : ManagerActivity.OnMenuClick {
        override fun onDelete() {
            mViewModel.adapter.setDelete(isLock)
            mBinding.deleteBar.visibility = View.VISIBLE
        }

        override fun onAlbum() {
        }

        override fun onClose() {
            mViewModel.adapter.setClose(isLock)
            mBinding.deleteBar.visibility = View.GONE
        }
    }

}


