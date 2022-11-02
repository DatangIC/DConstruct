package com.datangic.smartlock.viewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cn.dttsh.dts1586.DTS1586
import cn.dttsh.dts1586.TEMP_PWD
import com.datangic.common.Config.TEMP_PASSWORD_LIMIT
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.TempPwdAdapter
import com.datangic.smartlock.components.DeviceKeyItem
import com.datangic.data.database.table.DeviceEnum.KeyType
import com.datangic.data.database.table.DeviceKey
import com.datangic.data.database.view.ViewDeviceKey
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.data.DatabaseRepository
import com.datangic.common.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class FragmentManagerTempPwdViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    val userID: Int,
    val mDatabase: DatabaseRepository
) : AndroidViewModel(application) {
    private val TAG = FragmentManagerTempPwdViewModel::class.simpleName
    val mTitle = DeviceKeyItem(R.drawable.ic_management_temp_pwd, name = R.string.management_temp_pwd)
    private val mTempPwdListLiveData = mDatabase.appDatabase.deviceKeyDao().getViewDeviceTempKeys(
        serialNumber, macAddress, deviceUserId = Pair(userID, serialNumber)
    ).asLiveData()

    private val mDeviceLiveData = mDatabase.getDeviceLiveData()
    private var mTempSecretCode: String? = null
    private var mWithoutAsterisk: Boolean = false

    private var onClickListener: TempPwdAdapter.TempPwdAdapterListener? = null
    val mAdapter by lazy { TempPwdAdapter().apply { mOnClickListener = onClickListener } }
    private val mLogList: ArrayList<Int> = ArrayList()


    fun addNewTemp(context: Context) = View.OnClickListener {
        if (mLogList.size > TEMP_PASSWORD_LIMIT) {
            MaterialDialog.getAlertDialog(context, message = R.string.dialog_error_temp_pwd_limit, isError = true, isCancel = false).show()
        } else {
            MaterialDialog.getTempPwdCreateDialog(context) { isOnce, period ->
                createTempPassword(context, isOnce, period)
            }.show()
        }
    }


    private fun createTempPassword(context: Context, once: Boolean, p: Int) {
        mTempSecretCode?.let { secretCode ->
            val temp = TEMP_PWD().apply {
                isOnce = once
                period = p.toByte()
                tempSecret = secretCode
                oldPwdList = mLogList.toIntArray()
            }
            DTS1586.additionCmd(temp)
            if (temp.logID != 0) {
                val deviceKey = DeviceKey(
                    macAddress = macAddress,
                    serialNumber = serialNumber,
                    deviceUserId = Pair(userID, serialNumber),
                    deadTime = temp.deadline.toLong(),
                    keyType = KeyType.TEMPORARY_PASSWORD,
                    keyValue = (if (mWithoutAsterisk) "" else "*") + temp.tempPwd,
                    keyLockId = temp.logID
                )

                MaterialDialog.getShareDialog(
                    context,
                    icon = R.drawable.ic_temp_pwd_36,
                    title = R.string.title_temp_pwd,
                    message = context.getString(R.string.password_value).format(deviceKey.keyValue)
                ).show()

                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        mDatabase.appDatabase.deviceKeyDao()
                            .insertOrUpdate(deviceKey, mDatabase.getDeviceKeyName(KeyType.TEMPORARY_PASSWORD, mLogList.size))
                    } catch (e: Exception) {
                        Logger.e(TAG, "Add Temp Pwd Error =$e")
                    }
                }
            } else {
                MaterialDialog.getAlertDialog(
                    context,
                    message = if (temp.errCode == -1) R.string.dialog_error_temp_pwd_limit else R.string.dialog_error_temp_pwd_create,
                    isError = true,
                    isCancel = false
                ).show()
            }
        } ?: let {
            MaterialDialog.getAlertDialog(context, message = R.string.dialog_error_temp_secret_code, isError = true, isCancel = false).show()
        }
    }

    fun setObserver(fragment: Fragment) {
        mDeviceLiveData.observe(fragment.viewLifecycleOwner) {
            mTempSecretCode = it.temporaryPasswordSecretCode
            mWithoutAsterisk = it.temporaryPasswordWithoutAsterisk
        }
        mTempPwdListLiveData.observe(fragment.viewLifecycleOwner) {
            mAdapter.submitList(it)
            viewModelScope.launch {
                mLogList.clear()
                for (i in it) {
                    if (i.deadTime > System.currentTimeMillis() / 1000) {
                        mLogList.add(i.keyLockId)
                    }
                }
            }
        }
        onClickListener = object : TempPwdAdapter.TempPwdAdapterListener {
            override fun onItemClick(item: ViewDeviceKey) {
                MaterialDialog.getShareDialog(
                    fragment.requireContext(),
                    icon = R.drawable.ic_temp_pwd_36,
                    title = R.string.title_temp_pwd,
                    message = fragment.requireContext().getString(R.string.password_value).format(item.keyValue)
                ).show()
            }

            override fun onShareClick(item: ViewDeviceKey) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, item.keyValue)
                fragment.startActivity(Intent.createChooser(intent, "Choose a channel to share your text..."))
            }

            override fun onDeleteClick(item: ViewDeviceKey) {
                viewModelScope.launch(Dispatchers.IO) {
                    mDatabase.appDatabase.deviceKeyDao().deleteTempPwd(
                        item.serialNumber,
                        item.macAddress,
                        item.deviceUserID,
                        item.keyLockId,
                    )
                }
            }
        }
    }


}