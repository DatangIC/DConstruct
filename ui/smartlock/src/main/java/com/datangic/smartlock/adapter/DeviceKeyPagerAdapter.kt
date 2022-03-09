package com.datangic.smartlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.smartlock.components.DeviceKeyItem
import com.datangic.data.database.table.DeviceEnum
import com.datangic.data.database.view.ViewDeviceKey
import com.datangic.smartlock.databinding.PagerListViewBinding

class DeviceKeyPagerAdapter(mContext: Context, hasNFC: Boolean = true, hasFace: Boolean = false) :
    RecyclerView.Adapter<DeviceKeyPagerAdapter.KeyPageViewHolder>() {

    private val TAG = DeviceKeyPagerAdapter::class.simpleName

    val mDeviceKeyTypeList = getManagerOperation(hasNFC, hasFace)
    var mKeysItemList: List<ViewDeviceKey> = ArrayList()
        set(value) {
            field = value
            mPasswordAdapter.setKeyItemList(value)
            mFingerprintAdapter.setKeyItemList(value)
            mNfcAdapter.setKeyItemList(value)
            mFaceAdapter.setKeyItemList(value)
        }
    var mOnKeyClick: OnKeyClick? = null

    interface OnKeyClick {
        fun newKey(key: DeviceKeyItem)
        fun editNameKey(key: DeviceKeyItem)
        fun deleteKey(key: DeviceKeyItem, keyId: Int)
        fun modifyKey(key: DeviceKeyItem, keyId: Int)
        fun onLongClick(key: DeviceKeyItem)
    }

    private val mPasswordAdapter by lazy { DeviceKeyItemAdapter(mKeysItemList, DeviceEnum.KeyType.PASSWORD, mOnKeyClick) }
    private val mFingerprintAdapter by lazy { DeviceKeyItemAdapter(mKeysItemList, DeviceEnum.KeyType.FINGERPRINT, mOnKeyClick) }
    private val mNfcAdapter by lazy { DeviceKeyItemAdapter(mKeysItemList, DeviceEnum.KeyType.NFC, mOnKeyClick) }
    private val mFaceAdapter by lazy { DeviceKeyItemAdapter(mKeysItemList, DeviceEnum.KeyType.FACE, mOnKeyClick) }

    inner class KeyPageViewHolder(private val mBinding: PagerListViewBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(item: DeviceKeyItem) {
            mBinding.newItem = item
            mBinding.keysList.adapter = when (item.icon) {
                R.drawable.ic_password_36 -> mPasswordAdapter
                R.drawable.ic_fingerprint_36 -> mFingerprintAdapter
                R.drawable.ic_nfc_36 -> mNfcAdapter
                R.drawable.ic_face_36 -> mFaceAdapter
                else -> mPasswordAdapter
            }
            mBinding.apply {
                newKeyOnClick = View.OnClickListener {
                    mOnKeyClick?.newKey(item)
                }
                keyType.setOnLongClickListener {
                    mOnKeyClick?.onLongClick(item)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyPageViewHolder {
        return KeyPageViewHolder(PagerListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: KeyPageViewHolder, position: Int) {
        holder.bind(mDeviceKeyTypeList[position])
    }

    override fun getItemCount(): Int {
        return mDeviceKeyTypeList.size
    }

    private fun getManagerOperation(hasFp: Boolean = true, hasFace: Boolean = false): List<DeviceKeyItem> {
        return mutableListOf(
            DeviceKeyItem(R.drawable.ic_password_36, R.string.management_password, keyId = 0, type = DeviceEnum.KeyType.UNKNOWN),
            DeviceKeyItem(R.drawable.ic_fingerprint_36, R.string.management_fingerprint, keyId = 0, type = DeviceEnum.KeyType.UNKNOWN)
        ).apply {
            if (hasFace)
                this.add(DeviceKeyItem(R.drawable.ic_face_36, R.string.management_face, keyId = 0, type = DeviceEnum.KeyType.UNKNOWN))
            if (hasFp)
                this.add(DeviceKeyItem(R.drawable.ic_nfc_36, R.string.management_nfc, keyId = 0, type = DeviceEnum.KeyType.UNKNOWN))
        }
    }
}