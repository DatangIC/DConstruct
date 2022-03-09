package com.datangic.smartlock.adapter

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.viewHolder.BottomLineViewHolder
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.view.ViewDeviceLog
import com.datangic.smartlock.databinding.ComponentBottomLineBinding
import com.datangic.smartlock.databinding.ComponentRecordItemBinding
import com.datangic.smartlock.utils.UtilsFormat.DATE_WITH_YEAR_SECOND
import com.datangic.smartlock.utils.UtilsFormat.toDateString

class RecordItemAdapter(
    val mSelectedList: MutableList<ViewDeviceLog>,
    val mCheckCount: RecordPagerAdapter.OnCheckCount?
) : ListAdapter<ViewDeviceLog, RecyclerView.ViewHolder>(ListAdapterConfig.getAsyncDifferConfig(diffCallback = diffCallback)) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ViewDeviceLog>() {
            override fun areItemsTheSame(oldItem: ViewDeviceLog, newItem: ViewDeviceLog): Boolean {
                return oldItem.logId == newItem.logId && oldItem.serialNumber == newItem.serialNumber
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: ViewDeviceLog, newItem: ViewDeviceLog): Boolean {
                return oldItem == newItem
            }
        }
    }

    private val ITEM_CONTENT = 1
    private val BOTTOM_LINE = 0

    //    private var mLogList: MutableList<ViewDeviceLog> = logList
    private var mClose: Boolean = true
    private var mCheckAll: Boolean = false

    fun setLogList(value: MutableList<ViewDeviceLog>) {
        submitList(value)
    }

    inner class RecordItemViewHolder(val mBinding: ComponentRecordItemBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_CONTENT) {
            RecordItemViewHolder(ComponentRecordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            BottomLineViewHolder(
                ComponentBottomLineBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.resources.getDimension(R.dimen.height_86dp).toInt()
            )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecordItemViewHolder) {
            getItem(position).let {
                with(holder.mBinding) {
                    this.recordSelected.visibility = if (mClose) View.GONE else View.VISIBLE
                    recordSelected.isChecked = mCheckAll
                    this.setOnItemClick { _ ->
                        if (!mClose) {
                            if (mSelectedList.contains(it)) {
                                this.recordSelected.isChecked = false
                                mSelectedList.remove(it)
                                if (mSelectedList.isNotEmpty()) {
                                    mCheckCount?.onCheck(RecordPagerAdapter.Count.COUNT)
                                } else {
                                    mCheckCount?.onCheck(RecordPagerAdapter.Count.CLEAR)
                                }
                            } else {
                                mSelectedList.add(it)
                                this.recordSelected.isChecked = true
                                if (mSelectedList.size == currentList.size) {
                                    mCheckCount?.onCheck(RecordPagerAdapter.Count.ALL)
                                } else {
                                    mCheckCount?.onCheck(RecordPagerAdapter.Count.COUNT)
                                }
                            }

                        }
                    }
                    itemData = it
                    isError = it.logState in listOf(DeviceEnum.LogState.DELETE_USER, DeviceEnum.LogState.WARING, DeviceEnum.LogState.DELETE_KEY)
                    time = it.logCreateAt.toDateString(DATE_WITH_YEAR_SECOND)
                    when (it.logState) {
                        DeviceEnum.LogState.UNLOCK -> {
                            title = R.string.event_unlock
                            it.deviceUserName?.let { name ->
                                recordTips.text = this.root.resources.getString(R.string.event_unlock_tips).format(
                                    name,
                                    getLockType(this.root.resources, it.logUnlockType),
                                    it.deviceName
                                )
                            } ?: let { _ ->
                                recordTips.text = this.root.resources.getString(R.string.event_unlock_tips_temp_ped).format(
                                    it.deviceName,
                                    getLockType(this.root.resources, it.logUnlockType)
                                )
                            }
                            recordIcon.setImageDrawable(getLockIcon(this.root.resources, it.logUnlockType))
                        }
                        DeviceEnum.LogState.LOCK -> {
                            title = R.string.locked
                            recordTips.text = this.root.resources.getString(R.string.locked)
                        }
                        DeviceEnum.LogState.ADD_KEY -> {
                            title = R.string.event_add_key
                            recordTips.text = this.root.resources.getString(R.string.event_add_key_tips).format(
                                it.deviceUserName,
                                getLockType(this.root.resources, it.logUnlockType)
                            )
                            recordIcon.setImageResource(R.drawable.ic_add_info)
                        }
                        DeviceEnum.LogState.DELETE_KEY -> {
                            title = R.string.event_delete_key
                            recordTips.text = this.root.resources.getString(R.string.event_delete_key_tips).format(
                                it.deviceUserName,
                                getLockType(this.root.resources, it.logUnlockType)
                            )
                            recordIcon.setImageResource(R.drawable.ic_delete_info)
                        }
                        DeviceEnum.LogState.ADD_USER -> {
                            title = R.string.event_add_user
                            recordTips.text = this.root.resources.getString(R.string.event_add_user_tips).format(
                                it.deviceUserName
                            )
                            recordIcon.setImageResource(R.drawable.ic_add_user)
                        }
                        DeviceEnum.LogState.DELETE_USER -> {
                            title = R.string.event_delete_user
                            recordTips.text = this.root.resources.getString(R.string.event_delete_user_tips).format(
                                it.deviceUserName
                            )
                            recordIcon.setImageResource(R.drawable.ic_delete_user)
                        }
                        DeviceEnum.LogState.DOORBELL -> {
                            title = R.string.event_doorbell
                            recordIcon.setImageResource(R.drawable.ic_baseline_notifications_active_24)
                        }
                        else -> {
                            isError = true
                            if (it.logUnlockType == DeviceEnum.UnlockType.SEIZED_FINGERPRINT) {
                                title = R.string.wrong
                                recordIcon.setImageResource(R.drawable.ic_seized_fingerprint_36)
                                it.deviceUserName?.let { name ->
                                    recordTips.text = this.root.resources.getString(R.string.event_unlock_tips).format(
                                        name,
                                        getLockType(this.root.resources, it.logUnlockType),
                                        it.deviceName
                                    )
                                } ?: let { _ ->
                                    recordTips.text = this.root.resources.getString(R.string.event_unlock_tips_temp_ped).format(
                                        it.deviceName,
                                        getLockType(this.root.resources, it.logUnlockType)
                                    )
                                }
                            } else {
                                title = R.string.error
                                recordIcon.setImageResource(R.drawable.ic_tips)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getLockType(resource: Resources, type: DeviceEnum.UnlockType) = resource.getString(
        when (type) {
            DeviceEnum.UnlockType.PASSWORD -> R.string.password
            DeviceEnum.UnlockType.FINGERPRINT -> R.string.management_fingerprint
            DeviceEnum.UnlockType.NFC -> R.string.management_nfc
            DeviceEnum.UnlockType.REMOTE -> R.string.remote_unlock
            DeviceEnum.UnlockType.TEMPORARY_PASSWORD -> R.string.management_temp_pwd
            DeviceEnum.UnlockType.COMBINATION -> R.string.combined_unlock
            DeviceEnum.UnlockType.FACE -> R.string.management_face
            DeviceEnum.UnlockType.SEIZED_FINGERPRINT -> R.string.seized_fingerprint
            DeviceEnum.UnlockType.SEIZED_PASSWORD -> R.string.seized_password
            DeviceEnum.UnlockType.UNKNOWN -> R.string.unknown
        }
    )


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getLockIcon(resource: Resources, type: DeviceEnum.UnlockType) = resource.getDrawable(
        when (type) {
            DeviceEnum.UnlockType.PASSWORD -> R.drawable.ic_password_36
            DeviceEnum.UnlockType.FINGERPRINT -> R.drawable.ic_fingerprint_36
            DeviceEnum.UnlockType.NFC -> R.drawable.ic_nfc_36
            DeviceEnum.UnlockType.REMOTE -> R.drawable.ic_baseline_phonelink_ring_24
            DeviceEnum.UnlockType.TEMPORARY_PASSWORD -> R.drawable.ic_temp_pwd_36
            DeviceEnum.UnlockType.COMBINATION -> R.drawable.ic_tips
            DeviceEnum.UnlockType.FACE -> R.drawable.ic_face_36
            DeviceEnum.UnlockType.SEIZED_FINGERPRINT -> R.drawable.ic_seized_fingerprint_36
            DeviceEnum.UnlockType.SEIZED_PASSWORD -> R.drawable.ic_password2_36
            DeviceEnum.UnlockType.UNKNOWN -> R.drawable.ic_tips
        }
    )

    private fun getErrorTips(lockID: Int) = when (lockID) {
        0xF4 -> R.string.event_error_smashed
        0xF5 -> R.string.event_error_pwd
        0xF6 -> R.string.event_error_nfc
        0xF7 -> R.string.event_error_fingerprint
        0xF8 -> R.string.event_error_sized_fingerprint
        0xF9 -> R.string.event_error_demolished
        0xFA -> R.string.event_error_sized_fingerprint
        0xFD -> R.string.event_error_try
        else -> R.string.error
    }


    override fun getItemCount(): Int {
        return if (currentList.size > 8)
            currentList.size + 1
        else {
            currentList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < currentList.size) ITEM_CONTENT else BOTTOM_LINE
    }

    fun setDelete() {
        mClose = false
        mCheckAll = false
        for (i in 0 until currentList.size) {
            notifyItemChanged(i)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setClose() {
        mClose = true
        mCheckAll = false
        notifyDataSetChanged()
    }

    fun setCheckAll(check: Boolean) {
        mCheckAll = check
        for (i in 0 until currentList.size) {
            if (check) {
                mSelectedList.add(getItem(i))
            } else {
                mSelectedList.remove(getItem(i))
            }
            notifyItemChanged(i)
        }
    }
}