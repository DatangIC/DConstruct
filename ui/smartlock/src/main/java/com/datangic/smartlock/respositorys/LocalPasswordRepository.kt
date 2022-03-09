package com.datangic.smartlock.respositorys

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.datangic.localLock.*
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.dialog.MaterialDialog.setTitle
import com.datangic.smartlock.preference.SharePreferenceUtils
import com.datangic.smartlock.utils.REQUEST_VERIFICATION_PASSWORD


class LocalPasswordRepository(val mContext: Context) {

    private val TAG = "PasswordRepository"
    private var mLastTime = 0L
    val mLocalPassword: Triple<Int, String, Boolean>?
        get() = SharePreferenceUtils.getLocalPassword(mContext)

    val mLocalPasswordLiveData: MutableLiveData<Triple<Int, String, Boolean>?> = MutableLiveData(mLocalPassword)
    private var mPasswordDialog: AlertDialog? = null


    fun startVerificationForResult(activity: Activity, isBack: Boolean) {
        if (mLastTime == 0L || System.currentTimeMillis() - mLastTime > 30 * 60 * 1000) {
            mLocalPassword?.let {
                if (it.first != 0 && it.second != "") {
                    mLastTime = System.currentTimeMillis()
                    startActivity(
                        activity, LocalLockExtra(
                            action = LockAction.VERIFICATION,
                            type = it.first,
                            password = it.second,
                            secondType = if (it.third) LockType.FINGERPRINT else 0,
                            isBackPressed = isBack
                        )
                    )
                }
            }
        }
    }


    private fun startNewPassword(activity: Activity, @LockType type: Int, isBack: Boolean) {
        startActivity(
            activity, LocalLockExtra(
                action = LockAction.NEW_PASSWORD,
                type = type,
                password = "",
                secondType = 0,
                isBackPressed = isBack
            )
        )
    }

    @SuppressLint("StringFormatInvalid")
    fun getNewPasswordDialog(activity: Activity, title: Any, isBack: Boolean = false) {
        if (mPasswordDialog?.isShowing == true) return
        if (mLocalPassword == null || mLocalPassword?.second == "" && mLocalPassword?.first == 0) {
            val mItemType = arrayOf(R.string.password_pattern, R.string.password_number)
            mPasswordDialog?.let { dialog ->
                dialog.setTitle(title)
                dialog.setCanceledOnTouchOutside(isBack)
            } ?: let {
                mPasswordDialog = MaterialDialog.getItemDialog(
                    activity,
                    items = mItemType
                ) { which ->
                    startNewPassword(
                        activity,
                        when (mItemType[which]) {
                            R.string.password_pattern -> LockType.PATTERN
                            else -> LockType.PASSWORD
                        },
                        isBack = isBack
                    )
                }.apply {
                    setTitle(title)
                    setCanceledOnTouchOutside(isBack)
                }
            }
            if (mPasswordDialog?.isShowing == false) {
                mPasswordDialog?.show()
            }
        }
    }

    fun getChangePasswordDialog(activity: Activity, title: Any) {
        mLocalPassword?.let {
            val mItemType = arrayOf(R.string.password_pattern, R.string.password_number)
            MaterialDialog.getItemDialog(
                activity,
                items = mItemType
            ) { which ->
                startChangePassword(
                    activity,
                    it.first,
                    secondType = when (mItemType[which]) {
                        R.string.password_pattern -> LockType.PATTERN
                        else -> LockType.PASSWORD
                    },
                    password = it.second,
                    isBack = true
                )
            }.apply {
                setTitle(title)
                setCanceledOnTouchOutside(true)
            }.show()
        }
    }

    private fun startChangePassword(activity: Activity, @LockType type: Int, @LockType secondType: Int, password: String, isBack: Boolean) {
        startActivity(
            activity, LocalLockExtra(
                action = LockAction.CHANGE_PASSWORD,
                type = type,
                password = password,
                secondType = secondType,
                isBackPressed = isBack
            )
        )
    }

    fun saveLocalPassword(extra: LocalLockResult) {
        if (extra.action in listOf(LockAction.CHANGE_PASSWORD, LockAction.NEW_PASSWORD)) {
            Log.e(TAG, "type=${extra.type}")
            SharePreferenceUtils.saveLocalPassword(mContext, Pair(extra.type, extra.password))
            mLocalPasswordLiveData.postValue(mLocalPassword)
        }
    }

    fun setBiometric(value: Boolean) {
        SharePreferenceUtils.saveLocalPassword(mContext, value)
        mLocalPasswordLiveData.postValue(mLocalPassword)
    }

    private fun startActivity(activity: Activity, extra: LocalLockExtra) {
        val intent = Intent(activity, LockerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).apply {
            this.putExtra(LOCKER_TYPE, extra)
        }
        activity.startActivityForResult(intent, REQUEST_VERIFICATION_PASSWORD)
    }
}