package com.datangic.smartlock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.smartlock.components.DeviceKeyItem
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.view.ViewDeviceKey
import com.datangic.smartlock.databinding.ComponentKeysBinding
import com.datangic.smartlock.utils.UtilsFormat.toDateString

class DeviceKeyItemAdapter(
    keyItemList: List<ViewDeviceKey>, private val mType: DeviceEnum.KeyType,
    val mOnKeyClick: DeviceKeyPagerAdapter.OnKeyClick? = null
) : RecyclerView.Adapter<DeviceKeyItemAdapter.KeyItemViewHolder>() {
    private val TAG = DeviceKeyPagerAdapter::class.simpleName
    var mTypeItemsList: MutableList<ViewDeviceKey> = getList(keyItemList)

    private fun getList(keyItemList: List<ViewDeviceKey>): MutableList<ViewDeviceKey> {
        val mList: MutableList<ViewDeviceKey> = ArrayList()
        for (i in keyItemList) {
            if (mType == DeviceEnum.KeyType.FINGERPRINT) {
                if (i.keyType == DeviceEnum.KeyType.FINGERPRINT || i.keyType == DeviceEnum.KeyType.SEIZED_FINGERPRINT) {
                    mList.add(i)
                }
            } else if (i.keyType == mType) {
                mList.add(i)
            }
        }
        return mList
    }

    fun setKeyItemList(keyItemList: List<ViewDeviceKey>) {
        val mList: MutableList<ViewDeviceKey> = ArrayList()
        for (i in keyItemList) {
            if (mType == DeviceEnum.KeyType.FINGERPRINT) {
                if (i.keyType == DeviceEnum.KeyType.FINGERPRINT || i.keyType == DeviceEnum.KeyType.SEIZED_FINGERPRINT) {
                    mList.add(i)
                }
            } else if (i.keyType == mType) {
                mList.add(i)
            }
        }
        mTypeItemsList = mList
        notifyDataSetChanged()
    }


    inner class KeyItemViewHolder(private val mBinding: ComponentKeysBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ViewDeviceKey) {
            val deviceKeyItem = DeviceKeyItem(
                icon = when (item.keyType) {
                    DeviceEnum.KeyType.PASSWORD -> R.drawable.ic_password_36
                    DeviceEnum.KeyType.FINGERPRINT -> R.drawable.ic_fingerprint_36
                    DeviceEnum.KeyType.NFC -> R.drawable.ic_nfc_36
                    DeviceEnum.KeyType.FACE -> R.drawable.ic_face_36
                    DeviceEnum.KeyType.SEIZED_FINGERPRINT -> R.drawable.ic_seized_fingerprint_36
                    else -> R.drawable.ic_password_36
                },
                name = item.keyName ?: item.keyType.toString() + item.keyLockId,
                syncAt = item.createAt.toDateString(),
                keyId = item.keyLockId,
                type = item.keyType
            )
            with(mBinding) {
                this.deviceKeyItem = deviceKeyItem
                this.onDeleteClick = View.OnClickListener {
                    mOnKeyClick?.deleteKey(deviceKeyItem, item.keyLockId)
                    this.keysSwipe.smoothClose()
                }
                this.onEditNameClick = View.OnClickListener {
                    mOnKeyClick?.editNameKey(deviceKeyItem)
                }
                this.onModifyClick = View.OnClickListener {
                    mOnKeyClick?.modifyKey(deviceKeyItem, item.keyLockId)
                    this.keysSwipe.smoothClose()
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyItemViewHolder {
        return KeyItemViewHolder(ComponentKeysBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: KeyItemViewHolder, position: Int) {
        holder.bind(mTypeItemsList[position])
    }

    override fun getItemCount(): Int {
        return mTypeItemsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return mTypeItemsList[position].keyLockId
    }
}