package com.datangic.smartlock.viewModels

import android.app.Application
import android.view.View
import androidx.fragment.app.Fragment
import com.datangic.smartlock.R
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.data.database.table.DeviceEnum
import com.datangic.smartlock.databinding.FragmentVersionInfoBinding
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.*
import com.datangic.smartlock.utils.UtilsFormat.toHtml

class FragmentVersionInfoViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    bleManagerApiRepository: BleManagerApiRepository
) : BaseViewModel(application, bleManagerApiRepository) {
    private val TAG = FragmentVersionInfoViewModel::class.simpleName

    private val mDeviceLiveData by lazy {
        mBleManagerApi.getDeviceLiveData()
    }

    fun setInfo(fragment: Fragment, binding: FragmentVersionInfoBinding) {
        mDeviceLiveData.observe(fragment.viewLifecycleOwner) {
            binding.infoSerialNumber.text = fragment.getString(R.string.version_info_serial_number).format(it.serialNumber).toHtml()
            binding.infoImei.text = fragment.getString(R.string.version_info_imei).format(it.imei).toHtml()
            binding.infoMacAddress.text = String.format(fragment.getString(R.string.version_info_mac_address), it.macAddress).toHtml()
            binding.infoVersionSoftware.text = String.format(
                fragment.getString(R.string.version_info_software_version), it.softwareVersion
                    ?: NULL_STRING
            ).toHtml()
            binding.infoVersionHardware.text = String.format(
                fragment.getString(R.string.version_info_hardware_version), it.hardwareVersion
                    ?: NULL_STRING
            ).toHtml()

            binding.infoVersionFingerprint.text = String.format(
                fragment.getString(R.string.version_info_software_fingerprint), it.fingerprintSoftwareVersion
                    ?: NULL_STRING
            ).toHtml()

            if (it.face) {
                binding.infoVersionFace.text = String.format(
                    fragment.getString(R.string.version_info_software_face), it.faceSoftwareVersion[DeviceEnum.FaceVersion.MAIN]
                        ?: NULL_STRING
                ).toHtml()
            }
            binding.infoVersionFace.visibility = if (it.face) View.VISIBLE else View.GONE


            if (it.backPanelOta) {
                binding.infoVersionBackPanel.text = String.format(
                    fragment.getString(R.string.version_info_software_back_panel), it.backPanelSoftwareVersion
                        ?: NULL_STRING
                ).toHtml()
            }
            binding.infoVersionBackPanel.visibility = if (it.backPanelOta) View.VISIBLE else View.GONE

            if (it.wifi) {
                binding.infoVersionWifi.text = String.format(
                    fragment.getString(R.string.version_info_software_wifi), it.wifiSoftwareVersion
                        ?: NULL_STRING
                ).toHtml()
            }
            binding.infoVersionWifi.visibility = if (it.wifi) View.VISIBLE else View.GONE
        }
        queryVersion()
    }

    private fun queryVersion() {
        CreateMessage.createMessage19(macAddress, MSG19_VersionInformationQuery).execute()
        CreateMessage.createMessage19(macAddress, MSG19_FingerprintVersionQuery).execute()

        mHandler.postDelayed({
            if (mDeviceLiveData.value?.backPanelOta == true) {
                CreateMessage.createMessage19(macAddress, MSG19_BackPanelVersionQuery).execute()
            }
            if (mDeviceLiveData.value?.wifi == true) {
                CreateMessage.createMessage57(macAddress, MSG57_WifiVersionQuery).execute()
            }
            if (mDeviceLiveData.value?.face == true) {
                CreateMessage.createMessage41(macAddress, MSG41_CMDQueryVersion).execute()
            }
        }, 100)
    }

}