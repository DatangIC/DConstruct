package com.datangic.smartlock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.components.DeviceWithBluetooth
import com.datangic.smartlock.databinding.ComponentConnectDeviceBinding

class DeviceWithBluetoothAdapter(private val mDeviceWithBluetoothList: List<DeviceWithBluetooth>, private val selectAction: ((DeviceWithBluetooth) -> Unit)? = null) : RecyclerView.Adapter<DeviceWithBluetoothAdapter.DeviceWithBluetoothViewHolder>() {

    private val TAG = DeviceWithBluetoothAdapter::class.simpleName

    inner class DeviceWithBluetoothViewHolder(val mBinding: ComponentConnectDeviceBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(deviceWithBluetooth: DeviceWithBluetooth) {
            with(mBinding) {
                this.deviceWithBluetooth = deviceWithBluetooth
                this.deviceNameEdit.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceWithBluetoothViewHolder {
        return DeviceWithBluetoothViewHolder(ComponentConnectDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DeviceWithBluetoothViewHolder, position: Int) {
        holder.bind(mDeviceWithBluetoothList[position])
        holder.mBinding.onItemClick = View.OnClickListener {
            selectAction?.let {
                it(mDeviceWithBluetoothList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mDeviceWithBluetoothList.size
    }
}