package com.datangic.smartlock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.parcelable.ExtendedBluetoothDevice
import com.datangic.smartlock.databinding.ComponentScannerDeviceBinding

class ScannerDeviceAdapter(itemList: ArrayList<ExtendedBluetoothDevice>) : RecyclerView.Adapter<ScannerDeviceAdapter.ScannerDeviceViewHolder>() {
    private val TAG = ScannerDeviceAdapter::class.java.simpleName
    var mItemList: MutableList<ExtendedBluetoothDevice> = ArrayList()
    private var mOnItemClickListener: OnItemClickListener? = null

    init {
        mItemList = itemList
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, device: ExtendedBluetoothDevice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannerDeviceViewHolder {
        return ScannerDeviceViewHolder(ComponentScannerDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ScannerDeviceViewHolder, position: Int) {
        holder.mBinding.onItemClick = View.OnClickListener { view: View -> mOnItemClickListener?.onItemClick(view, mItemList[position]) }
        holder.bind(mItemList[position])
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    class ScannerDeviceViewHolder(val mBinding: ComponentScannerDeviceBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ExtendedBluetoothDevice) {
            with(mBinding) {
                this.deviceItem = item
                executePendingBindings()
            }
        }

    }
}