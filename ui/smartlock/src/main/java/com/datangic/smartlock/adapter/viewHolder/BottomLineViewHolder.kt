package com.datangic.smartlock.adapter.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.databinding.ComponentBottomLineBinding

class BottomLineViewHolder(mBinding: ComponentBottomLineBinding, height: Int = 60) : RecyclerView.ViewHolder(mBinding.root) {
    init {
        mBinding.bottomLayout.minHeight = height
    }
}