package com.datangic.smartlock.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.smartlock.adapter.viewHolder.BottomLineViewHolder
import com.datangic.data.database.view.ViewDeviceKey
import com.datangic.smartlock.databinding.ComponentBottomLineBinding
import com.datangic.smartlock.databinding.ComponentTempPasswordBinding
import com.datangic.smartlock.utils.UtilsFormat.toDateString

class TempPwdAdapter(tempList: List<ViewDeviceKey> = ArrayList()) :
    ListAdapter<ViewDeviceKey, RecyclerView.ViewHolder>(ListAdapterConfig.getAsyncDifferConfig(diffCallback)) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ViewDeviceKey>() {
            override fun areItemsTheSame(oldItem: ViewDeviceKey, newItem: ViewDeviceKey): Boolean {
                return oldItem.keyValue == newItem.keyValue
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: ViewDeviceKey, newItem: ViewDeviceKey): Boolean {
                return oldItem == newItem
            }
        }
    }

    private val ITEM_CONTENT = 1
    private val BOTTOM_LINE = 0

    init {
        submitList(tempList)
    }

    var mOnClickListener: TempPwdAdapterListener? = null

    interface TempPwdAdapterListener {
        fun onItemClick(item: ViewDeviceKey)
        fun onShareClick(item: ViewDeviceKey)
        fun onDeleteClick(item: ViewDeviceKey)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_CONTENT) {
            TempPwdViewHolder(ComponentTempPasswordBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            BottomLineViewHolder(
                ComponentBottomLineBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.resources.getDimension(R.dimen.height_86dp).toInt()
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TempPwdViewHolder) {
            getItem(position).let { item ->
                with(holder.mBinding) {
                    onItemClick = View.OnClickListener { mOnClickListener?.onItemClick(item) }
                    onShareClick = View.OnClickListener { mOnClickListener?.onShareClick(item) }
                    onDeleteClick = View.OnClickListener {
                        mOnClickListener?.onDeleteClick(item)
                    }
                    valid = item.deadTime > System.currentTimeMillis() / 1000
                    isOnce = item.keyLockId % 2 != 0
                    keyDeadTime.text = this.root.resources.getString(R.string.dead_time).format(item.deadTime.toDateString())
                    keyValue.text = this.root.resources.getString(R.string.password_value).format(
                        "${item.keyValue?.subSequence(0, 2)}***${
                            item.keyValue?.substring(
                                item.keyValue!!.length - 2,
                                item.keyValue!!.length
                            )
                        }"
                    )
                    keyName.text = item.keyName
                }
            }
        }
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

    inner class TempPwdViewHolder(val mBinding: ComponentTempPasswordBinding) : RecyclerView.ViewHolder(mBinding.root)
}