package com.datangic.localLock.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.datangic.localLock.*
import com.datangic.localLock.utils.SystemUtils
import com.datangic.localLock.widgets.NumericKeyboardView
import com.datangic.localLock.widgets.PatternLockView
import com.google.android.material.textview.MaterialTextView
import kotlin.properties.Delegates

class PasswordLockFragment : Fragment() {
    lateinit var mLockerView: NumericKeyboardView
    lateinit var mPasswordTips: MaterialTextView
    lateinit var mDelete: Button
    lateinit var mLockTips: TextView
    var secondType: Int = 0
    var mMode by Delegates.notNull<Int>()
    var verifyPassword: String = ""
    private var curMode: Int = 0
    private var newPassword: String? = null

    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.password_lock_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mLockerView = view.findViewById(R.id.password_view)
        mPasswordTips = view.findViewById(R.id.password_tips)
        mLockerView.addPasswordLockListener(onListener)

        val activity = requireActivity()
        if (activity is LockerActivity) {
            mMode = activity.extra?.action ?: LockAction.VERIFICATION
            verifyPassword = activity.extra?.password ?: ""
            secondType = activity.extra?.secondType ?: 0
            mLockTips = activity.findViewById(R.id.lock_tips)



            when (mMode) {
                LockAction.NEW_PASSWORD -> {
                    mLockTips.apply {
                        setOnClickListener {
                            curMode = LockStatus.INPUT_PASSWORD
                            mLockTips.visibility = View.INVISIBLE
                            newPassword = null
                            mPasswordTips.setText(R.string.new_password)
                            mLockerView.clearNumber()
                        }
                        setText(R.string.reset_password)
                    }
                }
                LockAction.CHANGE_PASSWORD -> {
                    mLockTips.apply {
                        setOnClickListener {
                            curMode = LockStatus.INPUT_PASSWORD
                            mLockTips.visibility = View.INVISIBLE
                            newPassword = null
                            mPasswordTips.setText(R.string.new_password)
                            mLockerView.clearNumber()
                        }
                        setText(R.string.reset_password)
                    }
                    if (activity.extra?.type != LockType.PASSWORD && activity.extra?.secondType == LockType.PASSWORD) {
                        curMode = LockStatus.INPUT_PASSWORD
                        mPasswordTips.setText(R.string.new_password)
                    } else {
                        mPasswordTips.setText(R.string.old_password)
                    }
                }
            }
        }

        mDelete = view.findViewById<Button>(R.id.delete).apply {
            setOnClickListener {
                mLockerView.deleteLastNumber()
            }
            visibility = View.INVISIBLE
        }
    }

    private val onListener = object : PasswordLockViewListener {
        override fun onNumberClick(number: Int, position: Int) {
            mDelete.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
            Log.e("onNumberClick", "Number=$number position=$position")
        }

        override fun onRemove(position: Int) {
            mDelete.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
        }

        override fun onInputDone(password: List<Int>): Boolean {

            val tempPwd = getPassword(password)
            when (mMode) {
                LockAction.NEW_PASSWORD -> {
                    when (curMode) {
                        in listOf(LockStatus.INPUT_PASSWORD, 0) -> {
                            newPassword = tempPwd
                            curMode = LockStatus.CONFIRM_PASSWORD
                            mPasswordTips.setText(R.string.confirm_password)
                        }
                        else -> {
                            mLockTips.visibility = View.VISIBLE
                            if (newPassword == tempPwd) {
                                curMode = LockStatus.DONE
                                setResult(tempPwd, true)
                            } else {
                                wrongTips(R.string.confirm_password)
                            }
                        }
                    }
                }
                LockAction.CHANGE_PASSWORD -> {
                    when (curMode) {
                        in listOf(LockStatus.VERIFICATION_PASSWORD, 0) -> {
                            if (verifyPassword == tempPwd) {
                                curMode = LockStatus.INPUT_PASSWORD
                                mHandler.removeCallbacks(runnable(R.string.old_password))
                                setTips(R.string.new_password)
                                if (secondType != 0 && secondType != LockType.PASSWORD) {
                                    (requireActivity() as LockerActivity).changeVerified(secondType)
                                }
                            } else {
                                wrongTips(R.string.old_password)
                            }
                        }
                        LockStatus.INPUT_PASSWORD -> {
                            mLockTips.visibility = View.VISIBLE
                            curMode = LockStatus.CONFIRM_PASSWORD
                            newPassword = tempPwd
                            setTips(R.string.confirm_password)
                        }
                        else -> {
                            if (newPassword == tempPwd) {
                                curMode = LockStatus.DONE
                                setResult(tempPwd, true)
                            } else {
                                wrongTips(R.string.confirm_password)
                            }
                        }
                    }
                }
                else -> {
                    if (verifyPassword == tempPwd) {
                        setResult(tempPwd, true)
                    } else {
                        SystemUtils.starVibrate(requireContext())
                        setTips(R.string.password_is_wrong, true)
                        (requireActivity() as LockerActivity).authError()
                    }
                }
            }
            return false
        }

    }

    private fun getPassword(pattern: List<Int>): String {
        var tempStr = ""
        for (i in pattern) {
            tempStr += i
        }
        Log.e("Pattern", "Pwd=$tempStr")
        return tempStr
    }

    private fun setResult(password: String, successful: Boolean) {
        (requireActivity() as LockerActivity).setResult(LocalLockResult(
                mMode,
                LockType.PASSWORD,
                password = password,
                successful
        ))
    }

    private fun setTips(@StringRes resId: Int, wrong: Boolean = false) {
        mPasswordTips.setText(resId)
        mHandler.removeCallbacks(runnable(resId))
    }

    private fun wrongTips(@StringRes resId: Int) {
        mPasswordTips.setText(R.string.password_is_wrong)
        mHandler.postDelayed(runnable(resId), 2000)
    }

    private fun runnable(@StringRes resId: Int) = Runnable { mPasswordTips.setText(resId) }
}