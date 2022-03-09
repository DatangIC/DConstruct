package com.datangic.smartlock.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.adapter.viewHolder.BottomLineViewHolder
import com.datangic.smartlock.adapter.viewHolder.ItemNullViewHolder
import com.datangic.smartlock.components.LockStatusItem
import com.datangic.smartlock.components.SwitchItem
import com.datangic.smartlock.components.SystemItem
import com.datangic.smartlock.databinding.*
import com.datangic.smartlock.utils.Logger

class SettingItemAdapter(lockItems: MutableList<Any>) : ListAdapter<Any, RecyclerView.ViewHolder>(
    ListAdapterConfig.getAsyncDifferConfig(
        diffCallback
    )
) {
    private val TAG = SettingItemAdapter::class.simpleName

    init {
        submitList(lockItems)
    }

    companion object {
        const val LOCK_ITEM_SWITCH = 1
        const val LOCK_ITEM_STATUS = 2
        const val LOCK_ITEM_NEXT = 3
        const val LOCK_ITEM_NULL = 4
        const val LOCK_ITEM_BOTTOM = 5
        const val NULL_TYPE = 1F
        const val BOTTOM_TYPE = 7f
        private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return when {
                    oldItem is SwitchItem && newItem is SwitchItem -> {
                        oldItem.itemName == newItem.itemName
                    }
                    oldItem is LockStatusItem && newItem is LockStatusItem -> {
                        oldItem.itemName == newItem.itemName
                    }
                    else -> oldItem == newItem
                }
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return when {
                    oldItem is SwitchItem && newItem is SwitchItem -> {
                        oldItem.checked == newItem.checked
                    }
                    oldItem is LockStatusItem && newItem is LockStatusItem -> {
                        oldItem.itemStatus == newItem.itemStatus
                    }
                    else -> oldItem == newItem
                }
            }
        }
    }

    private var mOnLockItemListener: OnSettingItemListener? = null

    interface OnSettingItemListener {
        fun onClick(systemItem: Any)
    }

    fun setOnSettingItemListener(onSettingItemListener: OnSettingItemListener) {
        mOnLockItemListener = onSettingItemListener
    }

    /**
     * Next
     */
    class SettingItemNextViewHolder(private val mBinding: ComponentLockNextButtonBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(nextItem: Any, mOnSettingItemListener: OnSettingItemListener?) {
            with(mBinding) {
                this.itemName = nextItem
                onItemClick = View.OnClickListener { mOnSettingItemListener?.onClick(nextItem) }
            }
        }

    }

    /**
     * Switch
     */
    class SettingItemSwitchViewHolder(val mBinding: ComponentLockSwitchButtonBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(switchItem: SwitchItem, mOnSettingItemListener: OnSettingItemListener?) {
            with(mBinding) {
                this.switchItem = switchItem
                onItemClick = View.OnClickListener { mOnSettingItemListener?.onClick(switchItem) }
            }
        }

    }

    /**
     * Status
     */
    class SettingItemStatusViewHolder(val mBinding: ComponentLockStatusButtonBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(statusItem: LockStatusItem, mOnSettingItemListener: OnSettingItemListener?) {
            with(mBinding) {
                this.itemStatus = statusItem
                onItemClick = View.OnClickListener { mOnSettingItemListener?.onClick(statusItem) }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LOCK_ITEM_SWITCH -> SettingItemSwitchViewHolder(
                ComponentLockSwitchButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            LOCK_ITEM_STATUS -> SettingItemStatusViewHolder(
                ComponentLockStatusButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            LOCK_ITEM_NEXT -> SettingItemNextViewHolder(ComponentLockNextButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            LOCK_ITEM_BOTTOM -> BottomLineViewHolder(ComponentBottomLineBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            else -> ItemNullViewHolder(ComponentSystemItemNullBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position)) {
            is LockStatusItem ->
                (holder as SettingItemStatusViewHolder).bind(getItem(position) as LockStatusItem, mOnLockItemListener)
            is SwitchItem ->
                (holder as SettingItemSwitchViewHolder).bind(getItem(position) as SwitchItem, mOnLockItemListener)
            is Int, is String ->
                (holder as SettingItemNextViewHolder).bind(getItem(position), mOnLockItemListener)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SwitchItem ->
                LOCK_ITEM_SWITCH
            is LockStatusItem ->
                LOCK_ITEM_STATUS
            is Int, is String -> LOCK_ITEM_NEXT
            BOTTOM_TYPE -> LOCK_ITEM_BOTTOM
            else -> LOCK_ITEM_NULL
        }

    }
}