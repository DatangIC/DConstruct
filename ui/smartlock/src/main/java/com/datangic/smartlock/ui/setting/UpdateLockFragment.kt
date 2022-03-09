package com.datangic.smartlock.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.FragmentUpdateLockBinding
import com.datangic.smartlock.parcelable.UpdateFile
import com.datangic.smartlock.utils.UPGRADE_TYPE_BACK_PANEL
import com.datangic.smartlock.utils.UPGRADE_TYPE_FINGERPRINT
import com.datangic.smartlock.utils.UtilsFormat.DATE_WITH_YEAR
import com.datangic.smartlock.utils.UtilsFormat.toDateString
import com.datangic.smartlock.utils.UtilsFormat.toHtml
import com.datangic.smartlock.utils.UtilsMessage.displaySnackBar
import com.datangic.smartlock.viewModels.FragmentUpdateLockViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class UpdateLockFragment : Fragment() {
    private val TAG = UpdateLockFragment::class.simpleName
    private lateinit var mBinding: FragmentUpdateLockBinding
    private lateinit var mViewModel: FragmentUpdateLockViewModel
    lateinit var args: UpdateFile

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_lock, container, false)
        arguments?.let {
            args = UpdateLockFragmentArgs.fromBundle(it).StringArgumentUpdateFile
            mBinding.serialNumber.text = getString(R.string.version_serial_number).format(args.serialNumber).toHtml()
            mBinding.runtimeVersion.text = getString(R.string.version_runtime_version).format(args.currentVersion).toHtml()
            mBinding.lastedVersion.text = getString(R.string.version_lasted_version).format(args.version).toHtml()
            mBinding.releaseTime.text = getString(R.string.version_release_time).format(args.updateDate.toDateString(DATE_WITH_YEAR)).toHtml()
            mBinding.releaseNotes.text = args.releaseNotes
            mViewModel = getViewModel {
                parametersOf(
                        args.macAddress,
                        args.serialNumber
                )
            }
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.downloadBtn.setOnClickListener {
            when (mBinding.downloadBtn.text) {
                getText(R.string.download) -> {
                    mViewModel.startDownload(mapOf(args.filename to args.path))
                    mBinding.downloadBtn.isEnabled = false
                }
                getText(R.string.update) -> {
                    mViewModel.startUpgrade(
                            this,
                            args.type,
                            listOf(args.filename),
                            if (args.sha1.length > 10) args.sha1 else null
                    )
                    mBinding.downloadBtn.isEnabled = false
                }
                getString(R.string.update_failed) -> {
                    displaySnackBar(it, R.string.update_failed)
                }
                getString(R.string.update_successful) -> {
                    displaySnackBar(it, R.string.update_successful)
                }
            }
        }
        mBinding.upgradeType.text = this.getText(when (args.type) {
            UPGRADE_TYPE_FINGERPRINT -> {
                R.string.fingerprint
            }
            UPGRADE_TYPE_BACK_PANEL -> {
                R.string.back_panel
            }
            else -> {
                R.string.lock
            }
        })
        mViewModel.mMapProgress.observe(this.viewLifecycleOwner) {
            mBinding.progress.progress = it[args.filename]?.toInt() ?: 0
            mBinding.percent.text = getString(R.string.percent).format(it[args.filename] ?: 0F)
        }

        mViewModel.mState.observe(this.viewLifecycleOwner) {
            mBinding.downloadBtn.setText(it)
            if (it in listOf(R.string.update, R.string.download, R.string.update_successful, R.string.update_failed))
                mBinding.downloadBtn.isEnabled = true
        }
    }
}