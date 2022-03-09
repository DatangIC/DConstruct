package com.datangic.smartlock.viewModels

import android.app.Application
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.util.Pair
import com.datangic.smartlock.R
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.data.database.table.DeviceUser
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.utils.UtilsMessage
import com.datangic.smartlock.utils.UtilsFormat.getIndex

class FragmentLifecycleViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    val userID: Int,
    mBleManagerApi: BleManagerApiRepository
) : BaseViewModel(application, mBleManagerApi) {

    val mDeviceUserLiveData = mBleManagerApi.getDeviceUserLiveData(userID)
    var mDeviceUser: DeviceUser? = null
    private val mItems = arrayOf(R.string.close, R.string.edit)

    init {
        CreateMessage.createMessage25(macAddress, userID).execute()
    }

    fun onLifecycleClick(fragment: Fragment) = View.OnClickListener {

        if (mDeviceUser?.lifecycleStart ?: 0 > 0 && mDeviceUser?.lifecycleEnd ?: 0 > 0) {
            MaterialDialog.getItemDialog(fragment.requireContext(), mItems) { which ->
                when (mItems[which]) {
                    R.string.edit -> showDatePickerDialog(fragment)
                    R.string.close -> CreateMessage.createMessage29(macAddress, userID, 0, 0).execute()

                }
            }.show()
        } else {
            showDatePickerDialog(fragment)
        }
    }

    private fun showDatePickerDialog(fragment: Fragment) {
        MaterialDialog.getDatePickerDialog(true, fragment.getString(R.string.title_lifecycle)).apply {
            addOnPositiveButtonClickListener {
                val start = ((this.selection as Pair<Long, Long>).first?.div(1000)
                    ?: 0) - 8 * 3600
                val end = ((this.selection as Pair<Long, Long>).second?.div(1000)
                    ?: 0) + 16 * 3600 - 1
                if (start > 3600 && end > 3600) {
                    CreateMessage.createMessage29(macAddress, userID, start.toInt(), end.toInt()).execute()
                }
            }
        }.show(fragment.childFragmentManager, "data")
    }

    fun onValidPeriodClick(fragment: Fragment, tag: Int) = View.OnClickListener { view ->
        MaterialDialog.getTimePeriodPickerDialog(
            fragment.requireContext(),
            startTime = mDeviceUser?.enablePeriodStart?.getIndex(tag) ?: 0,
            endTime = mDeviceUser?.enablePeriodEnd?.getIndex(tag) ?: 0,
            isEndLager = true
        ) { start, end ->
            if (start != 0 && start >= end) {
                UtilsMessage.displaySnackBar(view, R.string.end_blow_start_error)
            } else if (start != mDeviceUser?.enablePeriodStart?.getIndex(tag) ?: 0 ||
                end != mDeviceUser?.enablePeriodEnd?.getIndex(tag) ?: 0
            ) {
                val startList = mutableListOf(
                    mDeviceUser?.enablePeriodStart?.getIndex(0) ?: 0,
                    mDeviceUser?.enablePeriodStart?.getIndex(1) ?: 0,
                    mDeviceUser?.enablePeriodStart?.getIndex(2) ?: 0
                )
                val endList = mutableListOf(
                    mDeviceUser?.enablePeriodEnd?.getIndex(0) ?: 0,
                    mDeviceUser?.enablePeriodEnd?.getIndex(1) ?: 0,
                    mDeviceUser?.enablePeriodEnd?.getIndex(2) ?: 0
                )
                startList[tag] = start
                endList[tag] = end
                CreateMessage.createMessage1B(macAddress, userID, startList.toIntArray(), endList.toIntArray(), 0.toByte()).execute()
            }
        }.show()
    }

    fun onTipsClick(fragment: Fragment, tag: Int) = View.OnClickListener {
        MaterialDialog.getAlertDialog(
            fragment.requireContext(),
            icon = null,
            title = null,
            message = if (tag == 0) R.string.user_lifecycle_tips else R.string.user_valid_period_tips,
            isCancel = false
        ).show()
    }

}