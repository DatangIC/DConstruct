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
import com.datangic.smartlock.components.UpdateItem
import com.datangic.smartlock.components.UserItem
import com.datangic.data.database.table.DeviceEnum
import com.datangic.smartlock.databinding.ComponentBottomLineBinding
import com.datangic.smartlock.databinding.ComponentManagerUserBinding

class ManagerUserAdapter : ListAdapter<UserItem, RecyclerView.ViewHolder>(ListAdapterConfig.getAsyncDifferConfig(diffCallback)) {

    private val ITEM_CONTENT = 1
    private val BOTTOM_LINE = 0
    var onUserItemClick: OnUserItemClick? = null

    companion object {

        val diffCallback = object : DiffUtil.ItemCallback<UserItem>() {
            override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnUserItemClick {
        fun onPauseClick(item: UserItem)
        fun onStartClick(item: UserItem)
        fun onMoreClick(item: UserItem)
        fun onItemClick(item: UserItem)
        fun onEditNameClick(item: UserItem)
        fun onItemLongClick(item: UserItem)
    }

    inner class UserItemViewHolder(val mBinding: ComponentManagerUserBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_CONTENT) {
            UserItemViewHolder(ComponentManagerUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            BottomLineViewHolder(
                ComponentBottomLineBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.resources.getDimension(R.dimen.height_110dp).toInt()
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserItemViewHolder) {
            getItem(position).let { item ->
                with(holder.mBinding) {
                    if (position == 0) {
                        this.userSwipe.setSwipeEnable(false)
                    } else {
                        this.userSwipe.setSwipeEnable(true)
                    }
                    this.userItem = item
                    this.userStatus.setText(getUserStatus(item.userStatus))
                    onMoreClick = View.OnClickListener {
                        onUserItemClick?.onMoreClick(item)
                        this.userSwipe.smoothClose()
                    }
                    onPauseClick = View.OnClickListener {
                        onUserItemClick?.onPauseClick(item)
                        this.userSwipe.smoothClose()
                    }
                    onStartClick = View.OnClickListener {
                        onUserItemClick?.onStartClick(item)
                        this.userSwipe.smoothClose()
                    }
                    onItemClick = View.OnClickListener {
                        onUserItemClick?.onItemClick(item)
                        this.userSwipe.smoothClose()
                    }
                    onEditNameClick = View.OnClickListener {
                        onUserItemClick?.onEditNameClick(item)
                    }
                    this.userLayout.setOnLongClickListener {
                        onUserItemClick?.onItemLongClick(item)
                        this.userSwipe.smoothClose()
                        return@setOnLongClickListener true
                    }
                }
            }
        }
    }

    private fun getUserStatus(status: DeviceEnum.DeviceUserStatus) =
        when (status) {
            DeviceEnum.DeviceUserStatus.PAUSE -> R.string.user_pause
            DeviceEnum.DeviceUserStatus.ACTIVATED -> R.string.status_activated
            DeviceEnum.DeviceUserStatus.UNKNOWN,
            DeviceEnum.DeviceUserStatus.INACTIVATED -> R.string.status_inactivated
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
}