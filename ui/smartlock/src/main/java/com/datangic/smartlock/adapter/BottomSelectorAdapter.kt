package com.datangic.smartlock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.components.SelectorItem
import com.datangic.smartlock.databinding.ComponentSelectorItemBinding

class BottomSelectorAdapter(private val mSelectorItemList: List<SelectorItem>, private val selectedAction: ((SelectorItem) -> Unit)? = null) : RecyclerView.Adapter<BottomSelectorAdapter.SelectorItemViewHolder>() {

    inner class SelectorItemViewHolder(val mBinding: ComponentSelectorItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(selectorItem: SelectorItem) {
            with(mBinding) {
                this.selectorItem = selectorItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectorItemViewHolder {
        return SelectorItemViewHolder(ComponentSelectorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SelectorItemViewHolder, position: Int) {
        holder.bind(mSelectorItemList[position])
        holder.mBinding.onItemClick = View.OnClickListener {
            selectedAction?.let {
                it(mSelectorItemList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mSelectorItemList.size
    }
}