package com.datangic.smartlock.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentViewpager2WithTitleBinding
import com.datangic.smartlock.parcelable.IntentExtra
import com.datangic.smartlock.utils.INTENT_EXTRA
import com.datangic.smartlock.viewModels.FragmentManagerKeysViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class KeysFragment : Fragment() {
    private val TAG = KeysFragment::class.simpleName
    lateinit var mBinding: FragmentViewpager2WithTitleBinding
    private lateinit var mViewModel: FragmentManagerKeysViewModel
    private lateinit var args: IntentExtra
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_viewpager2_with_title, container, false)

        arguments?.let {
            initWithBundle(it)
        } ?: initWithIntent()

        mViewModel.setDefaultSelect(this, mBinding.pagerLayout, args.selectedType)
        return mBinding.root
    }

    private fun initWithBundle(bundle: Bundle) {

        args = LifecycleFragmentArgs.fromBundle(bundle).StringArgumentUserInfo
        mViewModel = getViewModel {
            parametersOf(
                args.serialNumber,
                args.macAddress,
                args.userID,
                args.hasNFC,
                args.hasFace
            )
        }
        (requireActivity() as ManagerActivity).apply {
            mBinding.managerToolbar.toolbar.setNavigationIcon(R.drawable.ic_close_24)
            mToolbarRepository.mOnBackPressed = fun(): Boolean {
                findNavController().navigate(R.id.action_navigation_manager_keys_to_navigation_manager_user)
                mBinding.managerToolbar.toolbar.navigationIcon = null
                mToolbarRepository.initToolbarWithBack(this, mBinding.managerToolbar.toolbar)
                return false
            }
        }
    }

    private fun initWithIntent() {
        requireActivity().intent.getParcelableExtra<IntentExtra>(INTENT_EXTRA)?.let {
            args = it
            mViewModel = getViewModel {
                parametersOf(
                    it.serialNumber,
                    it.macAddress,
                    it.userID,
                    it.hasNFC,
                    it.hasFace
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel.setTabLayTabLayoutMediator(mBinding.tabLayout, mBinding.pagerLayout)
    }

}
