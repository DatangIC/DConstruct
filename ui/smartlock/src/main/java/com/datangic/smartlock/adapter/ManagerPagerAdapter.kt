package com.datangic.smartlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.smartlock.components.CardSetting
import com.datangic.smartlock.components.DeviceItem
import com.datangic.data.database.view.ViewManagerDevice
import com.datangic.smartlock.databinding.PagerDevicesBinding
import com.datangic.smartlock.layoutManager.NoScrollGridLayoutManager
import com.datangic.smartlock.utils.UtilsFormat.toDateString

class ManagerPagerAdapter(private val mContext: Context, deviceViewManagerDevices: List<ViewManagerDevice>, cardSettingList: List<CardSetting>) : RecyclerView.Adapter<ManagerPagerAdapter.DevicePagerViewHolder>() {

    var mDeviceViewManagerDevices: List<ViewManagerDevice> = deviceViewManagerDevices
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var mOnManagerPagerClick: OnManagerPagerClick? = null
    var mCardSettingList: List<CardSetting> = cardSettingList

    fun setDevicesAndCards(deviceViewManagerDevices: List<ViewManagerDevice>, cardSettingList: List<CardSetting>) {
        if (deviceViewManagerDevices.size != mDeviceViewManagerDevices.size) {
            mDeviceViewManagerDevices = deviceViewManagerDevices
            mCardSettingList = cardSettingList
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicePagerViewHolder {
        return DevicePagerViewHolder(PagerDevicesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DevicePagerViewHolder, position: Int) {
        holder.bind(
                mContext,
                mDeviceViewManagerDevices[position].administrator,
                mDeviceViewManagerDevices[position],
                getManagerOperation(mDeviceViewManagerDevices[position].administrator, mDeviceViewManagerDevices[position].face),
                getCardSetting(position),
                mOnManagerPagerClick
        )
    }

    private fun getCardSetting(position: Int): CardSetting {
        if (mCardSettingList[position].macAddress == mDeviceViewManagerDevices[position].macAddress) {
            return mCardSettingList[position]
        } else {
            for (i in mCardSettingList) {
                if (mDeviceViewManagerDevices[position].macAddress == i.macAddress) {
                    return i
                }
            }
        }
        return CardSetting(
                lockName = mDeviceViewManagerDevices[position].name,
                macAddress = mDeviceViewManagerDevices[position].macAddress
        )
    }


    override fun getItemCount(): Int {
        return mDeviceViewManagerDevices.size
    }

    interface OnManagerPagerClick {
        fun onLockNameClick(view: View, deviceViewManagerDevice: ViewManagerDevice, cardSetting: CardSetting)
        fun onSettingsClick(view: View, deviceViewManagerDevice: ViewManagerDevice)
        fun onBleClick(view: View, deviceViewManagerDevice: ViewManagerDevice)
        fun onManagerOperation(view: View, deviceViewManagerDevice: ViewManagerDevice, item: DeviceItem)
    }

    inner class DevicePagerViewHolder(private val mBinding: PagerDevicesBinding) : RecyclerView.ViewHolder(mBinding.root) {


        fun bind(context: Context,
                 isAdministrator: Boolean,
                 deviceViewManagerDevice: ViewManagerDevice,
                 managementList: List<DeviceItem>,
                 cardSetting: CardSetting,
                 mOnManagerPagerClick: OnManagerPagerClick?) {
            val spanCount = if (isAdministrator) 3 else 2
            mBinding.managerView.layoutManager = NoScrollGridLayoutManager(context, spanCount)
            mBinding.managerView.adapter = ManagerItemAdapter(managementList).apply {
                mOnItemClickListener = object : ManagerItemAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, item: DeviceItem) {
                        mOnManagerPagerClick?.onManagerOperation(view, deviceViewManagerDevice, item)
                    }
                }
            }
            if (cardSetting.syncAt.length <= 2) {
                cardSetting.syncAt = deviceViewManagerDevice.updateAt.toDateString()
            }
            mBinding.cardSetting = cardSetting
            mBinding.lockClick = View.OnClickListener {
                mOnManagerPagerClick?.onLockNameClick(it, deviceViewManagerDevice, cardSetting)
                cardSetting.apply {
                    open = !this.open
                }
            }
            mBinding.settingsClick = View.OnClickListener {
                mOnManagerPagerClick?.onSettingsClick(it, deviceViewManagerDevice)
            }
            mBinding.bleClick = View.OnClickListener {
                mOnManagerPagerClick?.onBleClick(it, deviceViewManagerDevice)
            }
        }
    }

    private fun getManagerOperation(isAdministrator: Boolean, isFace: Boolean = false): List<DeviceItem> {
        val managerItem = mutableListOf(
                DeviceItem(R.drawable.ic_management_password, R.string.management_password),
                DeviceItem(R.drawable.ic_management_fingerprint, R.string.management_fingerprint),
                if (isFace) DeviceItem(R.drawable.ic_management_face, R.string.management_face) else DeviceItem(R.drawable.ic_management_nfc, R.string.management_nfc),
                DeviceItem(R.drawable.ic_management_record, R.string.management_record)
        )
        if (isAdministrator) {
            managerItem.add(DeviceItem(R.drawable.ic_management_temp_pwd, R.string.management_temp_pwd))
            managerItem.add(DeviceItem(R.drawable.ic_management_users, R.string.management_user))
        }
        return managerItem
    }
}