package com.datangic.smartlock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.adapter.viewHolder.ItemNullViewHolder
import com.datangic.smartlock.adapter.viewHolder.WifiViewHolder
import com.datangic.smartlock.databinding.ComponentScannerWifiBinding
import com.datangic.smartlock.databinding.ComponentSystemItemNullBinding

class RecycleItemAdapter(itemList: List<Any> = ArrayList(), private val itemType: ItemType) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG by lazy { RecycleItemAdapter::class.simpleName }

    enum class ItemType {
        WiFiItem
    }

    var mItemList: List<Any> = itemList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var mOnClickListener: ItemClickListener? = null

    private val WIFI_VIEW = 1234

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            WIFI_VIEW -> WifiViewHolder(ComponentScannerWifiBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> ItemNullViewHolder(ComponentSystemItemNullBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WifiViewHolder) {
            holder.bind(mItemList[position], mOnClickListener)
        }
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemType) {
            ItemType.WiFiItem -> WIFI_VIEW
        }
    }
}
