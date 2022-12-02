package com.datangic.smartlock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datangic.smartlock.R
import com.datangic.data.database.table.DeviceEnum.LogState
import com.datangic.data.database.view.ViewDeviceLog
import com.datangic.smartlock.databinding.ComponentRecordBinding
import com.datangic.common.utils.Logger

class RecordPagerAdapter(val mTabList: List<Any>) : RecyclerView.Adapter<RecordPagerAdapter.RecordPageViewHolder>() {
    inner class RecordPageViewHolder(val mBinding: ComponentRecordBinding) : RecyclerView.ViewHolder(mBinding.root)

    var mCheckCount: OnCheckCount? = null

    enum class Count {
        ALL,
        CLEAR,
        COUNT,
    }

    private var mUserLogList: MutableList<ViewDeviceLog> = mutableListOf()
    private var mLockLogList: MutableList<ViewDeviceLog> = mutableListOf()

    private val mUserAdapter by lazy { RecordItemAdapter(mSelectedList, mCheckCount) }
    private val mLockAdapter by lazy { RecordItemAdapter(mSelectedList, mCheckCount) }

    private val mSelectedList: MutableList<ViewDeviceLog> = ArrayList()


    interface OnCheckCount {
        fun onCheck(count: Count)
    }

    fun setLogList(logs: List<ViewDeviceLog>) {
        mUserLogList.clear()
        mLockLogList.clear()
        logs.forEach {
            if (it.logState in listOf(
                    LogState.ADD_KEY,
                    LogState.ADD_USER,
                    LogState.DELETE_KEY,
                    LogState.DELETE_USER
                )
            ) {
                if (!mUserLogList.contains(it)) {
                    mUserLogList.add(0, it)
                    mUserAdapter.setLogList(mUserLogList)
                }
            } else {
                if (!mLockLogList.contains(it)) {
                    mLockLogList.add(0, it)
                    mLockAdapter.setLogList(mLockLogList)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordPageViewHolder {
        return RecordPageViewHolder(ComponentRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecordPageViewHolder, position: Int) {
        with(holder.mBinding) {
            this.itemView.adapter = if (mTabList[position] == R.string.record_user) {
                mUserAdapter
            } else {
                mLockAdapter
            }
        }
    }

    override fun getItemCount(): Int {
        return mTabList.size
    }

    fun setDelete(lock: Boolean) {
        mSelectedList.clear()
        mCheckCount?.onCheck(Count.CLEAR)
        if (lock) {
            mLockAdapter.setDelete()
        } else {
            mUserAdapter.setDelete()
        }
    }

    fun setCheckAll(lock: Boolean, check: Boolean) {
        if (lock) {
            mLockAdapter.setCheckAll(check)
        } else {
            mUserAdapter.setCheckAll(check)
        }
    }

    fun setClose(lock: Boolean) {
        mSelectedList.clear()
        mCheckCount?.onCheck(Count.CLEAR)
        if (lock) {
            mLockAdapter.setLogList(mLockLogList)
            mLockAdapter.setClose()
        } else {
            mUserAdapter.setLogList(mUserLogList)
            mUserAdapter.setClose()
        }
    }

    fun getSelectedItems(position: Int): List<ViewDeviceLog> {
        return if (mTabList[position] == R.string.record_user) {
            mUserAdapter.mSelectedList
        } else {
            mLockAdapter.mSelectedList
        }
    }
}