package com.datangic.smartlock.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.datangic.common.utils.Logger
import com.datangic.smartlock.R
import com.datangic.smartlock.parcelable.UpdateFace
import com.datangic.smartlock.databinding.FragmentUpdateLockBinding
import com.datangic.smartlock.utils.*
import com.datangic.smartlock.utils.UtilsFormat.DATE_WITH_YEAR
import com.datangic.smartlock.utils.UtilsFormat.toDateString
import com.datangic.smartlock.utils.UtilsFormat.toHtml
import com.datangic.smartlock.viewModels.FragmentUpdateFaceViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.min

class UpdateFaceFragment : Fragment() {
    private val TAG = UpdateFaceFragment::class.simpleName
    private lateinit var mBinding: FragmentUpdateLockBinding
    private lateinit var mViewModel: FragmentUpdateFaceViewModel
    lateinit var args: UpdateFace

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_lock, container, false)
        arguments?.let {
            args = UpdateFaceFragmentArgs.fromBundle(it).StringArgumentUpdateFace
            mBinding.runtimeVersion.text = getString(R.string.version_runtime_version).format(args.currentMainVersion).toHtml()
            mBinding.lastedVersion.text = getString(R.string.version_lasted_version).format(args.mainVersion).toHtml()
            mBinding.serialNumber.text = getString(R.string.version_serial_number).format(args.serialNumber).toHtml()
            mBinding.releaseTime.text = getString(R.string.version_release_time).format(args.updateDate.toDateString(DATE_WITH_YEAR)).toHtml()
            Logger.e(TAG, "releaseNote=${args.releaseNotes}")
            mBinding.releaseNotes.text = args.releaseNotes ?: NULL_STRING
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
        val pathList: MutableMap<String, String> = LinkedHashMap()
        val updateList: MutableMap<Byte, String> = LinkedHashMap()
        mBinding.downloadBtn.setOnClickListener {
            when (mBinding.downloadBtn.text) {
                getText(R.string.download) -> {
                    args.nCpuFilename?.let {
                        pathList[it] = args.nCpuPath
                        updateList.put(MSG41_TypeUpgradeNCPU, it)
                    }
                    args.sCpuFilename?.let {
                        pathList[it] = args.sCpuPath
                        updateList.put(MSG41_TypeUpgradeSCPU, it)
                    }
                    args.modelFilename?.let {
                        pathList[it] = args.modelPath
                        pathList[FILENAME_MODEL_FW] = args.modelFwPath
                        updateList.put(MSG41_TypeUpgradeFW, FILENAME_MODEL_FW)
                        updateList.put(MSG41_TypeUpgradeModel, it)
                    }
                    args.uiFilename?.let {
                        pathList[it] = args.uiPath
                        updateList.put(MSG41_TypeUpgradeUI, it)
                    }
                    Logger.e(TAG, "list=$pathList")
                    mViewModel.startDownload(pathList)
                    mBinding.downloadBtn.isEnabled = false
                }
                getText(R.string.update) -> {
                    Logger.e(TAG, "list=$updateList")
                    mViewModel.startFaceUpgrade(
                            this,
                            updateList
                    )
                    mBinding.downloadBtn.isEnabled = false
                }
            }
        }
        mBinding.upgradeType.visibility = View.GONE
        mBinding.upgradeStep.visibility = View.VISIBLE
        mViewModel.mMapProgress.observe(this.viewLifecycleOwner)
        { mProgress ->
            lifecycleScope.launch {
                var count = 0
                var progress = 0F
                Logger.e(TAG, "mProgress =$mProgress")
                mProgress.forEach { entry ->
                    if (entry.value == 100F) {
                        count++
                    } else if (entry.value != 0F) {
                        progress = entry.value
                    }
                }
                if (count == updateList.size) {
                    progress = 100F
                }
                mBinding.upgradeStep.text = getString(R.string.update_step).format(min(count + 1, updateList.size), updateList.size)
                mBinding.progress.progress = progress.toInt()
                mBinding.percent.text = getString(R.string.percent).format(progress)
            }
        }
        mViewModel.mState.observe(this.viewLifecycleOwner)
        {
            mBinding.downloadBtn.setText(it)
            if (it in listOf(R.string.update, R.string.download, R.string.update_successful, R.string.update_failed))
                mBinding.downloadBtn.isEnabled = true
        }
    }
}