package com.datangic.smartlock.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.smartlock.components.DeviceWithBluetooth
import com.datangic.smartlock.databinding.ComponentSwipeRedGreenBinding

class DeviceSettingAdapter : ListAdapter<DeviceWithBluetooth, DeviceSettingAdapter.DeviceSettingAdapterViewHolder>(
    ListAdapterConfig.getAsyncDifferConfig(diffUtil)
) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DeviceWithBluetooth>() {
            override fun areItemsTheSame(oldItem: DeviceWithBluetooth, newItem: DeviceWithBluetooth): Boolean {
                return oldItem.serialNumber == newItem.serialNumber
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DeviceWithBluetooth, newItem: DeviceWithBluetooth): Boolean {
                return oldItem.serialNumber == newItem.serialNumber && oldItem.isSelected == newItem.isSelected && oldItem.connect == newItem.connect
            }
        }
    }


    var mSwipeClickListener: SwipeRedGreenClickListener<DeviceWithBluetooth>? = null

    var mOnEditNameClick: ((DeviceWithBluetooth) -> Unit)? = null

    var mOnItemClick: ((DeviceWithBluetooth) -> Unit)? = null

    inner class DeviceSettingAdapterViewHolder(val mBinding: ComponentSwipeRedGreenBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(deviceWithBluetooth: DeviceWithBluetooth) {
            mBinding.apply {
                swipeContext.deviceWithBluetooth = deviceWithBluetooth
                swipeGreen.visibility = View.GONE
                swipeRed.setText(R.string.unbind)
                swipeContext.onEditNameClick = View.OnClickListener {
                    mOnEditNameClick?.let { it(deviceWithBluetooth) }
                }
                swipeContext.onItemClick = View.OnClickListener {
                    mOnItemClick?.let {
                        it(deviceWithBluetooth)
                    }
                }
                onRedClick = View.OnClickListener {
                    swipeLayout.smoothClose()
                    mSwipeClickListener?.onRedClick(deviceWithBluetooth)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceSettingAdapterViewHolder {
        return DeviceSettingAdapterViewHolder(ComponentSwipeRedGreenBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DeviceSettingAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}