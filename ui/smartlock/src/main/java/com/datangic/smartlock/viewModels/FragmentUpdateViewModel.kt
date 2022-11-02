package com.datangic.smartlock.viewModels

import android.app.Application
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.datangic.api.smartlock.SmartLockOtaRepository
import com.datangic.api.smartlock.UpgradeRequest
import com.datangic.common.utils.Logger
import com.datangic.network.RequestStatus
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.UpgradeItemAdapter
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.parcelable.UpdateFace
import com.datangic.smartlock.parcelable.UpdateFile
import com.datangic.smartlock.components.UpdateItem
import com.datangic.data.database.table.Device
import com.datangic.data.database.table.DeviceEnum
import com.datangic.smartlock.databinding.FragmentUpdateBinding
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.ui.setting.UpdateFragmentDirections
import com.datangic.smartlock.utils.*
import kotlinx.coroutines.launch
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class FragmentUpdateViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    bleManagerApiRepository: BleManagerApiRepository
) : BaseViewModel(application, bleManagerApiRepository) {
    private val TAG = FragmentUpdateViewModel::class.simpleName

    private var mModel = -1

    private val mUpdateItem: ArrayList<UpdateItem> = ArrayList()

    var mUpgradeRes: UpgradeRequest.Response? = null

    private val mStateLiveDate = MutableLiveData<Any>().apply {
        value = R.string.version_checking_version
    }

    private val mDeviceLiveData by lazy {
        mBleManagerApi.getDeviceLiveData()
    }
    private val mAdapter by lazy {
        UpgradeItemAdapter().apply {
            setOnUpgradeItemListener(onClickListener)
        }
    }

    private var mNavController: NavController? = null

    private val mSmartLockOtaRepository by lazy {
        SmartLockOtaRepository(
            api = com.datangic.api.smartlock.SmartLockOta.create()
        )
    }

    fun setInfo(fragment: Fragment, binging: FragmentUpdateBinding) {
        mDeviceLiveData.observe(fragment.viewLifecycleOwner) {
            mModel -= 1
            if (mModel == 0) {
                mHandler.postDelayed({
                    request(fragment)
                    mStateLiveDate.postValue(R.string.version_querying_version)
                }, 200)
            }
        }
        mStateLiveDate.observe(fragment.viewLifecycleOwner) {
            binging.upgradeProgress.state = it
            if (it == R.string.version_checking_done) {
                updateIU(fragment, binging)
            }
        }
        queryVersion()
        mNavController = NavHostFragment.findNavController(fragment)
        binging.upgradeItems.adapter = mAdapter
    }

    private fun request(fragment: Fragment) {
        mDeviceLiveData.value?.let { device ->
            if (device.softwareVersion != null) {
                var fingerprint: UpgradeRequest.Fingerprint? = null
                var backPanel: UpgradeRequest.BackPanel? = null
                var face: UpgradeRequest.Face? = null
                device.fingerprintSoftwareVersion?.let { it1 ->
                    val ver = it1.split("_")
                    val zone = ver[1].split(".")
                    fingerprint = UpgradeRequest.Fingerprint(
                        fpType = ver[0],
                        fpCurZone = zone[1],
                        fpCurVer = it1
                    )
                }
                device.backPanelSoftwareVersion?.let { it1 ->
                    val ver = it1.split("_")
                    backPanel = UpgradeRequest.BackPanel(
                        bpType = ver[0],
                        bpCurVer = it1
                    )
                }
                device.faceSoftwareVersion.let { it1 ->
                    if (it1.size > 1) {
                        face = UpgradeRequest.Face(
                            faceType = it1[DeviceEnum.FaceVersion.MAIN]!!.split("_")[0],
                            mainCurVer = it1[DeviceEnum.FaceVersion.MAIN]!!,
                            nCpuCurVer = it1[DeviceEnum.FaceVersion.NCPU],
                            sCpuCurVer = it1[DeviceEnum.FaceVersion.SCPU],
                            modelCurVer = it1[DeviceEnum.FaceVersion.MODEL],
                            uiCurVer = it1[DeviceEnum.FaceVersion.UI]
                        )
                    }
                }
                val requestData = UpgradeRequest.UpdateRequestData(
                    deviceSn = device.serialNumber,
                    devCurVer = device.softwareVersion ?: "",
                    isTest = mSystemSetting.debugOta,
                    fingerprint = fingerprint,
                    backPanel = backPanel,
                    face = face
                )
//                ApiHttp.enqueue(LockRequest.getUpdateForHttp(requestData, requestDone(device), requestError(fragment)))
                mSmartLockOtaRepository.updateFirmware(requestData).observe(fragment) { response ->
                    when (response.requestStatus) {
                        RequestStatus.SUCCESS -> {
                            response.data?.let { requestSuccess(device, it) }
                        }
                        RequestStatus.ERROR -> {
                            MaterialDialog.getAlertDialog(
                                mActivity,
                                message = fragment.getString(R.string.dialog_internet_error).format(
                                    response.message
                                        ?: REQUEST_ERROR_TIMEOUT
                                ),
                                isCancel = false,
                                isConfirm = true,
                                action = object : MaterialDialog.OnMaterialAlterDialogListener {
                                    override fun onCancel() {

                                    }

                                    override fun onConfirm() {
                                        fragment.requireActivity().onBackPressed()
                                    }
                                }
                            ).show()
                        }
                        else -> {}
                    }
                }

            } else {
                UtilsMessage.displaySnackBar(fragment.requireView(), R.string.version_version_error)
            }
        }
    }

    private fun requestSuccess(device: Device, response: UpgradeRequest.Response) {
        try {
            mUpgradeRes = response
            mUpdateItem.clear()
            mUpdateItem.add(
                UpdateItem(R.drawable.ic_ble_lock_36dp, R.string.lock, mUpgradeRes?.device?.version != null)
            )
            mUpdateItem.add(
                UpdateItem(R.drawable.ic_fingerprint_36, R.string.management_fingerprint, mUpgradeRes?.fingerprint?.version != null)
            )
            if (device.backPanelOta)
                mUpdateItem.add(
                    UpdateItem(R.drawable.ic_back_panel_36, R.string.check_repair_repair_back_panel, mUpgradeRes?.backPanel?.version != null)
                )
            if (device.face)
                mUpdateItem.add(
                    UpdateItem(R.drawable.ic_face_36, R.string.management_face, mUpgradeRes?.face?.version != null)
                )
            mStateLiveDate.postValue(R.string.version_checking_done)
        } catch (e: Exception) {
            Logger.e(TAG, "e=$e")
        }
    }

    private fun requestDone(device: Device) = fun(_: Call, json: JSONObject) {
        viewModelScope.launch {
            requestSuccess(device, UpgradeRequest.responseToData(json))
        }
    }

    private fun requestError(fragment: Fragment) = fun(_: Call, error: IOException) {
        MaterialDialog.getAlertDialog(
            mActivity,
            message = fragment.getString(R.string.dialog_internet_error).format(
                error.message
                    ?: REQUEST_ERROR_TIMEOUT
            ),
            isCancel = false,
            isConfirm = true,
            action = object : MaterialDialog.OnMaterialAlterDialogListener {
                override fun onCancel() {

                }

                override fun onConfirm() {
                    fragment.requireActivity().onBackPressed()
                }
            }
        ).show()
    }

    private fun updateIU(fragment: Fragment, binging: FragmentUpdateBinding) {
        mAdapter.submitList(mUpdateItem)
        binging.upgradeProgress.progress.visibility = View.GONE
        binging.upgradeItems.visibility = View.VISIBLE
    }

    private val onClickListener = object : UpgradeItemAdapter.OnUpgradeItemListener {
        override fun onClick(updateItem: UpdateItem) {
            when (updateItem.icon) {
                R.drawable.ic_ble_lock_36dp -> {
                    mUpgradeRes?.device?.let {
                        val action = UpdateFragmentDirections.actionUpgradeFragmentToUpdateLockFragment(
                            UpdateFile(
                                macAddress = macAddress,
                                serialNumber = serialNumber,
                                type = UPGRADE_TYPE_LOCK,
                                filename = it.filename,
                                currentVersion = (mDeviceLiveData.value?.softwareVersion?.split("_")?.get(1))
                                    ?: "",
                                version = it.version,
                                md5 = it.md5,
                                updateDate = it.updateDate,
                                releaseNotes = it.msg,
                                path = it.path
                            )
                        )
                        mNavController?.navigate(action)
                    }
                }
                R.drawable.ic_fingerprint_36 -> {
                    mUpgradeRes?.fingerprint?.let {
                        val action = UpdateFragmentDirections.actionUpgradeFragmentToUpdateLockFragment(
                            UpdateFile(
                                macAddress = macAddress,
                                serialNumber = serialNumber,
                                type = UPGRADE_TYPE_FINGERPRINT,
                                filename = it.filename,
                                currentVersion = (mDeviceLiveData.value?.fingerprintSoftwareVersion?.split("_")?.get(1))
                                    ?: "",
                                version = it.version,
                                sha1 = it.sha1,
                                zone = it.zone,
                                updateDate = it.updateDate,
                                releaseNotes = it.msg,
                                path = it.path
                            )
                        )
                        mNavController?.navigate(action)
                    }
                }
                R.drawable.ic_back_panel_36 -> {
                    mUpgradeRes?.backPanel?.let {
                        val action = UpdateFragmentDirections.actionUpgradeFragmentToUpdateLockFragment(
                            UpdateFile(
                                macAddress = macAddress,
                                serialNumber = serialNumber,
                                type = UPGRADE_TYPE_BACK_PANEL,
                                filename = it.filename,
                                currentVersion = (mDeviceLiveData.value?.backPanelSoftwareVersion?.split("_")?.get(1))
                                    ?: "",
                                version = it.version,
                                updateDate = it.updateDate,
                                releaseNotes = it.msg,
                                path = it.path
                            )
                        )
                        mNavController?.navigate(action)
                    }
                }
                R.drawable.ic_face_36 -> {
                    mUpgradeRes?.face?.let {
                        val action = UpdateFragmentDirections.actionUpgradeFragmentToUpdateFaceFragment(
                            UpdateFace(
                                macAddress = macAddress,
                                serialNumber = serialNumber,
                                currentMainVersion = (mDeviceLiveData.value?.faceSoftwareVersion?.get(DeviceEnum.FaceVersion.MAIN)?.split("_")
                                    ?.get(1))
                                    ?: "",
                                mainVersion = it.version,
                                updateDate = it.updateDate,
                                releaseNotes = it.msg,
                                nCpuFilename = it.nCpu?.filename,
                                nCpuVersion = it.nCpu?.version ?: "",
                                nCpuPath = it.nCpu?.path ?: "",

                                sCpuFilename = it.sCpu?.filename,
                                sCpuVersion = it.sCpu?.version ?: "",
                                sCpuPath = it.sCpu?.path ?: "",

                                modelFilename = it.model?.filename,
                                modelVersion = it.model?.version ?: "",
                                modelPath = it.model?.path ?: "",
                                modelFwFilename = it.model?.fwFilename ?: "",
                                modelFwPath = it.model?.fwPath ?: "",

                                uiFilename = it.ui?.filename,
                                uiVersion = it.ui?.version ?: "",
                                uiPath = it.ui?.path ?: "",
                            )
                        )
                        mNavController?.navigate(action)
                    }
                }
            }
        }

    }

    private fun queryVersion() {
        CreateMessage.createMessage19(macAddress, MSG19_VersionInformationQuery).execute()
        CreateMessage.createMessage19(macAddress, MSG19_FingerprintVersionQuery).execute()
        mModel = 2
        mHandler.postDelayed({
            mDeviceLiveData.value?.let { it1 ->
                if (it1.face) {
                    CreateMessage.createMessage41(macAddress, MSG41_CMDQueryVersion).execute()
                }
                if (it1.backPanelOta) {
                    CreateMessage.createMessage19(macAddress, MSG19_BackPanelVersionQuery).execute()
                }
            }
        }, 20)
        mStateLiveDate.postValue(R.string.version_sync_version)
    }

}