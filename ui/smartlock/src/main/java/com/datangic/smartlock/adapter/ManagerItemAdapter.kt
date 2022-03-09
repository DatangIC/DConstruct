package com.datangic.smartlock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.smartlock.components.DeviceItem
import com.datangic.smartlock.databinding.ComponentManagerViewBinding

class ManagerItemAdapter(val mItemList: List<DeviceItem>) : RecyclerView.Adapter<ManagerItemAdapter.ManagerDeviceViewHolder>() {

    var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, item: DeviceItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagerDeviceViewHolder {
        return ManagerDeviceViewHolder(ComponentManagerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ManagerDeviceViewHolder, position: Int) {
        holder.bind(mItemList[position])
        holder.mBinding.itemOnClick = View.OnClickListener {
            mOnItemClickListener?.onItemClick(it, mItemList[position])
        }
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    class ManagerDeviceViewHolder(val mBinding: ComponentManagerViewBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: DeviceItem) {
            with(mBinding) {
                this.managerItem = item
                this.itemIcon.setStrokeColorResource(R.color.red)
            }
        }
    }
}