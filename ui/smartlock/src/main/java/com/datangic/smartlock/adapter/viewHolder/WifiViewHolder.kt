package com.datangic.smartlock.adapter.viewHolder

import android.net.wifi.ScanResult
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.adapter.ItemClickListener
import com.datangic.smartlock.databinding.ComponentScannerWifiBinding

class WifiViewHolder(val mBinding: ComponentScannerWifiBinding) : RecyclerView.ViewHolder(mBinding.root) {
    private val TAG = WifiViewHolder::class.simpleName
    fun bind(wifiItem: Any, mItemClickListener: ItemClickListener?) {
        if (wifiItem is ScanResult) {
            with(mBinding) {
                this.wifiItem = wifiItem
                onItemClick = View.OnClickListener {
                    mItemClickListener?.onItemClick(wifiItem)
                }
            }

        }
    }
}