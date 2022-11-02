package com.datangic.smartlock.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentLifecycleBinding
import com.datangic.smartlock.parcelable.IntentExtra
import com.datangic.common.utils.Logger
import com.datangic.smartlock.utils.UtilsFormat.DATE_WITH_YEAR
import com.datangic.smartlock.utils.UtilsFormat.toDateString
import com.datangic.smartlock.utils.UtilsFormat.toHtml
import com.datangic.smartlock.utils.UtilsFormat.toTimeString
import com.datangic.smartlock.viewModels.FragmentLifecycleViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class LifecycleFragment : Fragment() {
    private val TAG = LifecycleFragment::class.simpleName
    lateinit var mBinding: FragmentLifecycleBinding
    private lateinit var mViewModel: FragmentLifecycleViewModel

    private lateinit var args: IntentExtra

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lifecycle, container, false)

        arguments?.let {
            args = LifecycleFragmentArgs.fromBundle(it).StringArgumentUserInfo
            mViewModel = getViewModel {
                parametersOf(
                    args.macAddress,
                    args.serialNumber,
                    args.userID
                )
            }
            (requireActivity() as ManagerActivity).apply {
                mBinding.managerToolbar.toolbar.setNavigationIcon(R.drawable.ic_close_24)
                mToolbarRepository.mOnBackPressed = fun(): Boolean {
                    findNavController().navigate(R.id.action_navigation_user_lifecycle_to_navigation_manager_user)
                    mBinding.managerToolbar.toolbar.navigationIcon = null
                    mToolbarRepository.initToolbarWithBack(this, mBinding.managerToolbar.toolbar)
                    return false
                }
            }
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.mDeviceUserLiveData.observe(this.viewLifecycleOwner) { user ->
            if (user == null) return@observe
            mViewModel.mDeviceUser = user
            try {
                Logger.e(TAG, "LifeCycle User Changed")
                mBinding.userInfo.text = getString(R.string.user_lifecycle_user_info).format(user.deviceUsername, user.deviceUserId).toHtml()
                mBinding.lifecycleValue.textValue.text =
                    getString(R.string.user_lifecycle_value_title).format(getPeriod(user.lifecycleStart, user.lifecycleEnd, false)).toHtml()
                mBinding.validPeriodValue1.textValue.text =
                    getString(R.string.user_valid_first_period).format(getPeriod(user.enablePeriodStart[0], user.enablePeriodEnd[0])).toHtml()
                mBinding.validPeriodValue2.textValue.text =
                    getString(R.string.user_valid_second_period).format(getPeriod(user.enablePeriodStart[1], user.enablePeriodEnd[1])).toHtml()
                mBinding.validPeriodValue3.textValue.text =
                    getString(R.string.user_valid_third_period).format(getPeriod(user.enablePeriodStart[2], user.enablePeriodEnd[2])).toHtml()
            } catch (e: Exception) {
                Logger.e(TAG, "LifeCycle Error=${e.message}")
            }

        }
        with(mBinding) {
            onLifecycleClick = mViewModel.onLifecycleClick(this@LifecycleFragment)
            onFirstClick = mViewModel.onValidPeriodClick(this@LifecycleFragment, 0)
            onSecondClick = mViewModel.onValidPeriodClick(this@LifecycleFragment, 1)
            onThirdClick = mViewModel.onValidPeriodClick(this@LifecycleFragment, 2)
            onLifecycleTipsClick = mViewModel.onTipsClick(this@LifecycleFragment, 0)
            onValidPeriodTipsClick = mViewModel.onTipsClick(this@LifecycleFragment, 1)
        }
    }

    private fun getPeriod(start: Int = 0, end: Int = 0, time: Boolean = true): String {
        return if (start > 0 && end > 0) {
            if (time) {
                getString(R.string.user_valid_period_value).format(start.toTimeString(), end.toTimeString())
            } else {
                getString(R.string.user_lifecycle_value).format(start.toDateString(DATE_WITH_YEAR), end.toDateString(DATE_WITH_YEAR))
            }
        } else {
            getString(R.string.close)
        }
    }

}