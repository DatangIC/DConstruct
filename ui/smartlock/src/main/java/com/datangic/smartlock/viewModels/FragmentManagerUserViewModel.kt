package com.datangic.smartlock.viewModels

import android.app.Application
import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import cn.dttsh.dts1586.*
import com.datangic.common.utils.Logger
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.BottomSelectorAdapter
import com.datangic.smartlock.adapter.ManagerUserAdapter
import com.datangic.smartlock.ble.CreateMessage
import com.datangic.smartlock.components.DeviceKeyItem
import com.datangic.smartlock.components.SelectorItem
import com.datangic.smartlock.components.UserItem
import com.datangic.data.database.table.DeviceUser
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.liveData.LockBleReceivedLiveData
import com.datangic.smartlock.parcelable.IntentExtra
import com.datangic.smartlock.respositorys.BleManagerApiRepository
import com.datangic.smartlock.ui.manager.UserFragmentDirections
import com.datangic.smartlock.utils.*
import com.datangic.zxing.CodeUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class FragmentManagerUserViewModel(
    application: Application,
    val macAddress: String,
    val serialNumber: String,
    val userID: Int,
    mBleManagerApi: BleManagerApiRepository
) : BaseViewModel(application, mBleManagerApi) {

    private val TAG = FragmentManagerUserViewModel::class.simpleName
    val mTitle = DeviceKeyItem(R.drawable.ic_management_users, name = R.string.management_user)
    var mUserWithChildUsersLiveData = mBleManagerApi.getUserWithChildUsersLiveData(userID)
    var mDevice: ViewManagerDevice? = mBleManagerApi.mDefaultDeviceView
    var mShareUser: DeviceUser? = null
    val mAdapter by lazy { ManagerUserAdapter() }
    fun addNewUser(context: Context) = View.OnClickListener {
        getAddUserDialog(context).show()
    }

     fun setMessageListener(fragment: Fragment) {
        mUserWithChildUsersLiveData.observe(fragment.viewLifecycleOwner) {
            val userList = ArrayList<UserItem>()
            it.childUsers.forEach { child ->
                userList.add(
                    UserItem(
                        child.deviceUsername,
                        child.deviceUserId.first,
                        child.userStatus
                    )
                )
            }
            userList.add(
                0, UserItem(
                    it.user.deviceUsername,
                    it.user.deviceUserId.first,
                    it.user.userStatus
                )
            )
            userList.sortWith { o1, o2 ->
                if (o2.userId < o1.userId) 1 else -1
            }
            mAdapter.submitList(userList)
        }
        mAdapter.onUserItemClick = getUserItemClickListener(fragment)
    }


    private fun getUserItemClickListener(fragment: Fragment) = object : ManagerUserAdapter.OnUserItemClick {
        override fun onPauseClick(item: UserItem) {
            Logger.e(TAG, "Pause Id=${item.userId}")
            CreateMessage.createMessage11(macAddress, MSG11_SuspendUser, item.userId).execute()
        }

        override fun onStartClick(item: UserItem) {
            Logger.e(TAG, "Start Id=${item.userId}")
            CreateMessage.createMessage11(macAddress, MSG11_EnableUser, item.userId).execute()
        }

        override fun onMoreClick(item: UserItem) {
            Logger.e(TAG, "More Id=${item.userId}")
            getHandleDialog(fragment, item).show()
        }

        override fun onItemClick(item: UserItem) {
            Logger.e(TAG, "Click Id=${item.userId}")
            CreateMessage.createMessage25(macAddress, item.userId).execute().also {
                if (it == CreateMessage.State.SUCCESS)
                    fragment.findNavController().navigate(
                        UserFragmentDirections.actionNavigationManagerUserToNavigationManagerKeys(
                            IntentExtra(
                                macAddress, serialNumber, item.userId, hasFace = mDevice?.face ?: false
                            )
                        )
                    )
            }

        }

        override fun onEditNameClick(item: UserItem) {
            getEditNameDialog(context = fragment.requireContext(), item).show()
        }

        override fun onItemLongClick(item: UserItem) {
            Logger.e(TAG, "Long Click Id=${item.userId}")
            getHandleDialog(fragment, item).show()
        }
    }


    private fun getEditNameDialog(context: Context, item: UserItem): AlertDialog {
        return MaterialDialog.getInputStringDialog(
            context = context,
            title = R.string.dialog_name_input_title,
            icon = R.drawable.ic_user_32,
            hint = item.userName,
            tips = R.string.dialog_name_input_tips
        ) { newName ->
            viewModelScope.launch {
                mBleManagerApi.updateDeviceUserName(newName, item.userId)
            }
        }
    }

    private fun getAddUserDialog(context: Context): BottomSheetDialog {
        val dialog = MaterialDialog.getBottomSheetDialogWithLayout(context, R.layout.bottom_sheet_menu, R.string.bottom_dialog_add_user)
        val mSelectorItemList = listOf(
            SelectorItem(R.string.bottom_dialog_new_admin),
            SelectorItem(R.string.bottom_dialog_new_user),
        )
        dialog.findViewById<RecyclerView>(R.id.dialog_recycle_view)?.adapter = BottomSelectorAdapter(mSelectorItemList) { it ->
            CreateMessage.createMessage11(macAddress, if (it.itemName == R.string.bottom_dialog_new_user) MSG11_NewUser else MSG11_NewAdmin, 0)
                .execute().let { state ->
                    if (state == CreateMessage.State.SUCCESS) dialog.cancel()
                }
        }
        return dialog
    }

    private fun getHandleDialog(fragment: Fragment, userItem: UserItem): BottomSheetDialog {
        val dialog = MaterialDialog.getBottomSheetDialogWithLayout(fragment.requireContext(), R.layout.bottom_sheet_menu, userItem.userName)
        val mSelectorItemList = mutableListOf(
            SelectorItem(R.string.bottom_dialog_user_share),
            SelectorItem(R.string.bottom_dialog_user_delete),
//                SelectorItem(R.string.bottom_dialog_user_batch_deletion),
            SelectorItem(R.string.bottom_dialog_user_user_lifecycle),
            SelectorItem(R.string.bottom_dialog_user_user_edit_name)
        )
        mDevice?.let {
            if (it.permissionSwitch) {
                mSelectorItemList.add(SelectorItem(R.string.bottom_dialog_user_user_set_as_admin))
            }
        }
        dialog.findViewById<RecyclerView>(R.id.dialog_recycle_view)?.adapter = BottomSelectorAdapter(mSelectorItemList) {
            when (it.itemName) {
                R.string.bottom_dialog_user_share -> {
                    mUserWithChildUsersLiveData.value?.childUsers?.forEach { user ->
                        if (user.deviceUserId.first == userItem.userId) {
                            mShareUser = user
                            return@forEach
                        }
                    }
                    mShareUser?.let { user ->
                        if (user.authCode?.length == 60) {
                            shareUser(user.authCode!!, user.administrator, user.deviceUsername)
                        } else {
//                            mSendMessage(macAddress) { bluetoothDevice, sendMessage ->
//                                sendMessage.sendMessage11(bluetoothDevice, MSG11_QueryAuthCode, userItem.userId)
//                                showLoadingDialog()
//                            }
                            CreateMessage.createMessage25(macAddress, userItem.userId).execute().also { state ->
                                if (state == CreateMessage.State.SUCCESS) {
                                    showLoadingDialog()
                                }
                            }
                        }
//                        user.authCode?.let { authCode ->
//                            Logger.e(TAG, "userID = ${userItem.userId} authCode=$authCode")
//                            shareUser(authCode, user.administrator, user.deviceUsername)
//                        } ?: mSendMessage(macAddress) { bluetoothDevice, sendMessage ->
//                            sendMessage.sendMessage11(bluetoothDevice, MSG11_QueryAuthCode, userItem.userId)
//                        }
                    }
                }
                R.string.bottom_dialog_user_delete -> {
                    CreateMessage.createMessage11(macAddress, MSG11_DeleteUser, userItem.userId).execute()
                }
                R.string.bottom_dialog_user_batch_deletion -> {
//                    MaterialDialog.getDatePickerDialog(true, "sdu").show(fragment.childFragmentManager, "data")
                }
                R.string.bottom_dialog_user_user_lifecycle -> {
                    try {
                        val action = UserFragmentDirections.actionNavigationManagerUserToNavigationUserLifecycle(
                            IntentExtra(
                                macAddress,
                                serialNumber,
                                userItem.userId
                            )
                        )
                        findNavController(fragment).navigate(action)
                    } catch (e: Exception) {
                        Logger.e(TAG, "Lifecycle error=${e.message}")
                    }
                }
                R.string.bottom_dialog_user_user_edit_name -> {
                    getEditNameDialog(fragment.requireContext(), item = userItem).show()
                }
                R.string.bottom_dialog_user_user_set_as_admin -> {
                }
            }
            dialog.cancel()
        }
        return dialog
    }

    override fun handle14(msg14: MSG14) {
        Logger.e(TAG, "authCode=${msg14.authCode}")
        cancelLoadingDialog()
        shareUser(
            authCode = msg14.authCode,
            mShareUser?.administrator ?: false,
            mShareUser?.deviceUsername ?: mApplication.getString(R.string.dialog_share)
        )
    }

    override fun handle26(msg: LockBleReceivedLiveData) {
        val mCmdInfo = DTS1586.getCmdInfo(msg.mark)
        mShareUser?.let { user ->
            if (msg.msg is MSG26) {
                val msg26 = msg.msg as MSG26
                DTS1586.getAuthCode(mCmdInfo.operateUserID, mCmdInfo.imei, mCmdInfo.macAddress, mCmdInfo.dynCode, msg26.ts)?.let { authCode ->
                    shareUser(authCode, user.administrator, user.deviceUsername)
                }
            }
        }
        cancelLoadingDialog()
        mShareUser = null
    }

    private fun shareUser(authCode: String, administrator: Boolean, username: String?) {
        val mShareMsg = SHARE_USER().also { share ->
            share.type = if (administrator) 0 else 1
            share.authCode = authCode
            share.period = 120
        }
        if (DTS1586.additionCmd(mShareMsg) == 0) {
            CodeUtils.createQRCode(mShareMsg.shareCode)?.let { bitmap ->
                MaterialDialog.getBitmapDialog(
                    mActivity,
                    title = username,
                    bitmap = bitmap
                ).show()
            }
        }
    }

}