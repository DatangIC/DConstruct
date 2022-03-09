package com.datangic.smartlock.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.dttsh.dts1586.MSG1A
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.CheckRepairItemAdapter
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.components.CheckRepairItem
import com.datangic.data.database.view.ViewDeviceStatus
import com.datangic.smartlock.databinding.FragmentCheckRepairBinding
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.*

class FragmentCheckRepairViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    bleManagerApiRepository: BleManagerApiRepository
) : BaseViewModel(application, bleManagerApiRepository) {
    private val TAG = FragmentCheckRepairViewModel::class.simpleName
    private val mViewDeviceStatusLiveDate: LiveData<ViewDeviceStatus> = mBleManagerApi.mViewDevicesStatusLiveData
    private val mMSG1ALiveData: MutableLiveData<MSG1A> = MutableLiveData()

    var mCheckItem: ArrayList<CheckRepairItem> = arrayListOf(
        CheckRepairItem(R.string.check_repair_repair_keyboard, R.drawable.ic_keyboard, null),
        CheckRepairItem(R.string.management_fingerprint, R.drawable.ic_management_fingerprint, null)
    )

    fun setAdapter(fragment: Fragment, view: RecyclerView) {
        mViewDeviceStatusLiveDate.observe(fragment) {
            for (i in mCheckItem) {
                i.correct = null
            }
            if (mCheckItem.size == 2) {
                if (it.hasFace) {
                    mCheckItem.add(CheckRepairItem(R.string.management_face, R.drawable.ic_management_face))
                } else {
                    mCheckItem.add(CheckRepairItem(R.string.management_nfc, R.drawable.ic_management_nfc))
                }
                if (it.backPanelOTA) {
                    mCheckItem.add(CheckRepairItem(R.string.check_repair_repair_back_panel, R.drawable.ic_back_panel))
                }
            }
            if (view.adapter == null) {
                view.layoutManager = GridLayoutManager(fragment.requireContext(), mCheckItem.size)
                view.adapter = CheckRepairItemAdapter(mCheckItem)
            }
        }
    }


    var count = 0
    var errorCode = 0

    var errText = ""

    @SuppressLint("SetTextI18n")
    fun setListener(fragment: Fragment, binding: FragmentCheckRepairBinding) {
        mMSG1ALiveData.observe(fragment) {
            if (it.type == 0.toByte()) {
                cancelLoadingDialog()
                if (it.value == MSG1A_WorkCorrectly) {
                    for (i in mCheckItem) {
                        i.correct = true
                    }
                    binding.checkText.setText(R.string.check_repair_repair_all_properly)
                    binding.repairBtn.visibility = View.GONE
                    binding.checkError.visibility = View.GONE
                } else {
                    if (it.value and MSG1A_WifiSerialPortError == MSG1A_WifiSerialPortError) {
                        for (i in mCheckItem) {
                            if (i.itemName == R.string.check_repair_repair_back_panel) {
                                i.correct = false
                            }
                        }
                        count += 1
                        errorCode += 0x200
                        binding.checkError.text = String.format(
                            fragment.requireContext().getString(R.string.check_repair_repair_err_code),
                            errorCode
                        ) + errText + fragment.getString(R.string.check_repair_repair_err_wifi)
                    } else {
                        for (i in mCheckItem) {
                            i.correct = true
                        }
                        errorCode = it.value
                        count = 0
                        errText = ""
                        binding.checkError.text =
                            String.format(fragment.requireContext().getString(R.string.check_repair_repair_err_code), it.value)
                        if (it.value and MSG1A_KeyError == MSG1A_KeyError) {
                            count += 1
                            mCheckItem[0].correct = false
                            errText += fragment.getString(R.string.check_repair_repair_err_key)
                        }
                        if (it.value and MSG1A_FingerprintError == MSG1A_FingerprintError) {
                            count += 1
                            mCheckItem[1].correct = false
                            errText += fragment.getString(R.string.check_repair_repair_err_fingerprint)
                        }
                        if (it.value and MSG1A_NFCError == MSG1A_NFCError) {
                            count += 1
                            mCheckItem[2].correct = false
                            errText += fragment.getString(R.string.check_repair_repair_err_nfc)
                        }
                        if (it.value and MSG1A_WifiSerialPortError == MSG1A_WifiSerialPortError) {
                            count += 1
                            errText += fragment.getString(R.string.check_repair_repair_err_wifi)
                        }
                        if (it.value and MSG1A_FaceError != 0) {
                            count += 1
                            for (i in mCheckItem) {
                                if (i.itemName == R.string.management_face) {
                                    i.correct = false
                                }
                            }
                            if (it.value and MSG1A_FaceNIRError == MSG1A_FaceNIRError) {
                                errText += fragment.getString(R.string.check_repair_repair_err_face_nir)
                            }
                            if (it.value and MSG1A_FaceRGBError == MSG1A_FaceRGBError) {
                                errText += fragment.getString(R.string.check_repair_repair_err_face_rgb)
                            }
                            if (it.value and MSG1A_FaceFlashError == MSG1A_FaceFlashError) {
                                errText += fragment.getString(R.string.check_repair_repair_err_face_flash)
                            }
                            if (it.value and MSG1A_FaceTouchError == MSG1A_FaceTouchError) {
                                errText += fragment.getString(R.string.check_repair_repair_err_face_touch)
                            }
                            if (it.value and MSG1A_FaceLcmError == MSG1A_FaceLcmError) {
                                errText += fragment.getString(R.string.check_repair_repair_err_face_lcm)
                            }
                            if (it.value and MSG1A_FaceExpansionChipError == MSG1A_FaceExpansionChipError) {
                                errText += fragment.getString(R.string.check_repair_repair_err_face_chip)
                            }

                        }
                        binding.checkError.text = binding.checkError.text as String + errText
                    }
                    binding.checkError.visibility = View.VISIBLE
                    binding.repairBtn.visibility = View.VISIBLE
                    if (count == 1) {
                        binding.checkText.setText(R.string.check_repair_repair_error)
                    } else {
                        binding.checkText.text = String.format(fragment.requireContext().getString(R.string.check_repair_repair_errors), count)
                    }

                }
            }
        }
    }

    fun check() {
        CreateMessage.createMessage19(macAddress, MSG19_DeviceCheck).execute().also { state ->
            if (state == CreateMessage.State.SUCCESS) {
                showLoadingDialog()
            }
        }
        mHandler.postDelayed({ CreateMessage.createMessage19(macAddress, MSG19_BackPanelCheck).execute() }, 500)

    }

    fun repair() {
        CreateMessage.createMessage19(macAddress, MSG19_SystemRepair).execute().also { state ->
            if (state == CreateMessage.State.SUCCESS) {
                showLoadingDialog()
            }
        }

    }

    override fun handle1E(errorCode: Int) {
        if (errorCode == MSG1E_OpenTheSlide) {
            MaterialDialog.getAlertDialog(mActivity,
                message = R.string.dialog_error_message_wrong_qr_code,
                isCancel = false,
                action = object : MaterialDialog.OnMaterialAlterDialogListener {
                    override fun onCancel() {
                    }

                    override fun onConfirm() {
                        check()
                    }

                }
            ).show()
        }
        if (errorCode == MSG1E_SystemRepairDone) {
            check()
        }
    }

    override fun handle1A(msg: MSG1A) {
        mMSG1ALiveData.postValue(msg)
    }
}