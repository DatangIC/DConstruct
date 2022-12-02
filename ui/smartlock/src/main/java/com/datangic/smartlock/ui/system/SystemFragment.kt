package com.datangic.smartlock.ui.system

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ServiceUtils.bindService
import com.blankj.utilcode.util.ServiceUtils.unbindService
import com.datangic.common.RouterList.LOGIN_ACTIVITY
import com.datangic.libs.base.services.NetworkDataService
import com.datangic.libs.base.services.NetworkServiceConnection
import com.datangic.network.RequestStatus
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentSettingBinding
import com.datangic.smartlock.viewModels.FragmentSystemViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SystemFragment : Fragment() {

    private lateinit var mBinding: FragmentSettingBinding

    val mViewModel: FragmentSystemViewModel by sharedViewModel()
    private val mNetworkServiceConnection = NetworkServiceConnection

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        Intent(this.requireActivity(), NetworkDataService::class.java).also { intent ->
            bindService(intent, mNetworkServiceConnection, Context.BIND_AUTO_CREATE)
        }
        mViewModel.setObserver(this) {
            mNetworkServiceConnection.mNetworkDataService.logout() { res ->
                if (res.requestStatus == RequestStatus.SUCCESS) {
                    ARouter.getInstance()
                        .build(LOGIN_ACTIVITY)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .withBoolean("logout", true)
                        .navigation()
                    activity?.finish()
                }
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mNetworkServiceConnection)
    }
}