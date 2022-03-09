package com.datangic.smartlock.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.components.UpdateItem
import com.datangic.smartlock.databinding.ComponentSystemItemNullBinding
import com.datangic.smartlock.databinding.ComponentUpdateItemBinding

class UpgradeItemAdapter : ListAdapter<UpdateItem, RecyclerView.ViewHolder>(
    ListAdapterConfig.getAsyncDifferConfig(
        diffCallback
    )
) {
    companion object {

        val diffCallback = object : DiffUtil.ItemCallback<UpdateItem>() {
            override fun areItemsTheSame(oldItem: UpdateItem, newItem: UpdateItem): Boolean {
                return oldItem.icon == newItem.icon
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: UpdateItem, newItem: UpdateItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var mOnUpgradeItemListener: OnUpgradeItemListener? = null

    interface OnUpgradeItemListener {
        fun onClick(updateItem: UpdateItem)
    }

    fun setOnUpgradeItemListener(onUpgradeItemListener: OnUpgradeItemListener) {
        mOnUpgradeItemListener = onUpgradeItemListener
    }

    /**
     * UpgradeItem
     */
    class UpgradeItemNextViewHolder(private val mBinding: ComponentUpdateItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(updateItem: UpdateItem, mOnUpgradeItemListener: OnUpgradeItemListener?) {
            with(mBinding) {
                this.upgradeItem = updateItem
                onItemClick = View.OnClickListener { mOnUpgradeItemListener?.onClick(updateItem) }
            }
        }
    }

    /**
     * Null View 空格
     */
    class SettingItemNullViewHolder(mBinding: ComponentSystemItemNullBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UpgradeItemNextViewHolder(ComponentUpdateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UpgradeItemNextViewHolder).bind(getItem(position), mOnUpgradeItemListener)

    }


}