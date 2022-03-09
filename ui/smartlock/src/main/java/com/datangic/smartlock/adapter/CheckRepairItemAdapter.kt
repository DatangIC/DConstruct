package com.datangic.smartlock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.components.CheckRepairItem
import com.datangic.smartlock.databinding.ComponentCheckRepairItemBinding

class CheckRepairItemAdapter(val mItemList: List<CheckRepairItem>) : RecyclerView.Adapter<CheckRepairItemAdapter.CheckRepairViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckRepairViewHolder {
        return CheckRepairViewHolder(ComponentCheckRepairItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CheckRepairViewHolder, position: Int) {
        holder.bind(mItemList[position])

    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    inner class CheckRepairViewHolder(val mBinding: ComponentCheckRepairItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: CheckRepairItem) {
            with(mBinding) {
                this.checkRepairItem = item
            }
        }
    }
}