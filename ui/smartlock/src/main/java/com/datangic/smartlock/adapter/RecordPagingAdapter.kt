package com.datangic.smartlock.adapter

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.view.ViewDeviceLog
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.viewHolder.BottomLineViewHolder
import com.datangic.smartlock.databinding.ComponentBottomLineBinding
import com.datangic.smartlock.databinding.ComponentRecordItemBinding
import com.datangic.smartlock.utils.UtilsFormat.toDateString

class RecordPagingAdapter() : PagingDataAdapter<ViewDeviceLog, RecyclerView.ViewHolder>(LOG_COMPARATOR) {
    inner class RecordItemViewHolder(val mBinding: ComponentRecordItemBinding) : RecyclerView.ViewHolder(mBinding.root)


    private val ITEM_CONTENT = 1
    private val BOTTOM_LINE = 0

    //    private var mLogList: MutableList<ViewDeviceLog> = logList

    companion object {
        private val PAYLOAD_SCORE = Any()
        val LOG_COMPARATOR = object : DiffUtil.ItemCallback<ViewDeviceLog>() {
            override fun areContentsTheSame(oldItem: ViewDeviceLog, newItem: ViewDeviceLog): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ViewDeviceLog, newItem: ViewDeviceLog): Boolean =
                oldItem.logId == newItem.logId

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecordItemViewHolder)
            getItem(position)?.let {
                with(holder.mBinding) {
                    this.recordSelected.visibility = View.GONE
                    itemData = it
                    isError = it.logState in listOf(
                        DeviceEnum.LogState.DELETE_USER,
                        DeviceEnum.LogState.WARING,
                        DeviceEnum.LogState.DELETE_KEY
                    )
                    time = it.logCreateAt.toDateString(com.datangic.smartlock.utils.UtilsFormat.DATE_WITH_YEAR_SECOND)
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

}