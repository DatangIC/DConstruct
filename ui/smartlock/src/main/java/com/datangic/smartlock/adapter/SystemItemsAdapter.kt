package com.datangic.smartlock.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.components.SystemItem
import com.datangic.smartlock.databinding.ComponentSystemItemNextBinding
import com.datangic.smartlock.databinding.ComponentSystemItemNullBinding

class SystemItemsAdapter :
    ListAdapter<Any, RecyclerView.ViewHolder>(ListAdapterConfig.getAsyncDifferConfig(diffCallback)) {
    companion object {
        const val SYSTEM_ITEM_NEXT = 1
        const val SYSTEM_ITEM_NULL = 0
        private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return if (oldItem is SystemItem && newItem is SystemItem) {
                    newItem.type == newItem.type
                } else true
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return if (oldItem is SystemItem && newItem is SystemItem) {
                    newItem.title == newItem.title
                } else true
            }
        }
    }

    private var mOnSystemItemListener: OnSettingItemListener? = null

    interface OnSettingItemListener {
        fun onClick(systemItem: SystemItem)
    }

    fun setOnSettingItemListener(onSettingItemListener: OnSettingItemListener) {
        mOnSystemItemListener = onSettingItemListener
    }

    /**
     * Next
     */
    class SettingItemNextViewHolder(private val mBinding: ComponentSystemItemNextBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(systemItem: SystemItem, mOnSettingItemListener: OnSettingItemListener?) {
            with(mBinding) {
                this.settingItem = systemItem
                onItemClick = View.OnClickListener { mOnSettingItemListener?.onClick(systemItem) }
            }
        }

    }

    /**
     * Null View 空格
     */
    class SettingItemNullViewHolder(mBinding: ComponentSystemItemNullBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SYSTEM_ITEM_NEXT) {
            SettingItemNextViewHolder(ComponentSystemItemNextBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            SettingItemNullViewHolder(ComponentSystemItemNullBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItem(position) is SystemItem) {
            (holder as SettingItemNextViewHolder).bind(getItem(position) as SystemItem, mOnSystemItemListener)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is SystemItem) {
            (getItem(position) as SystemItem).type
        } else {
            SYSTEM_ITEM_NULL
        }

    }

}