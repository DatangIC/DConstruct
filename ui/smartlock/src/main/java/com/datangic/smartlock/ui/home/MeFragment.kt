package com.datangic.smartlock.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.datangic.common.Config
import com.datangic.easypermissions.EasyPermissions
import com.datangic.smartlock.R
import com.datangic.data.database.table.User
import com.datangic.smartlock.databinding.FragmentMeBinding
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.ui.scanning.ScanActivity
import com.datangic.smartlock.viewModels.FragmentMeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MeFragment : Fragment() {

    private val TAG = MeFragment::class.java.simpleName
    private val mViewModel: FragmentMeViewModel by sharedViewModel()
    private lateinit var mBinding: FragmentMeBinding
    private var mUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false)
        return mBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.setSettingAdapter(mBinding.settingsView, this.requireContext())
        mBinding.meScan.setOnClickListener {
            mViewModel.mScanQrCode.onScanQrCode(this.requireActivity())
        }
        mBinding.meSearch.setOnClickListener {
            this.startActivity(Intent(requireActivity(), ScanActivity::class.java))
        }

        mBinding.copyright.text = getString(R.string.version).format(Config.getVersionName(this.requireContext())) + getString(R.string.copyright)

//        mViewModel.mUserLiveData.observe(this.viewLifecycleOwner) { user ->
//            user?.let {
//                this.mUser = user
//                mBinding.userName.text = user.nickname ?: getText(R.string.user_name)
//            }
//        }
        mBinding.userName.setOnClickListener {
            MaterialDialog.getInputStringDialog(
                requireContext(),
                title = R.string.dialog_name_input_title,
                hint = mBinding.userName.text,
                icon = R.drawable.ic_user_32
            ) { str ->
                mUser?.let { user ->
                    user.nickname = str
                    lifecycleScope.launch {
                        mViewModel.mDatabase.appDatabase.userDao().update(user)
                    }
                } ?: let {
                    lifecycleScope.launch {
                        mViewModel.mDatabase.appDatabase.userDao().insert(
                            User(userId = 0, nickname = str, email = null, avatar = null)
                        )
                    }
                }
            }.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            mViewModel.mScanQrCode.getPermissionCallbacks(this.requireActivity())
        )
    }
}