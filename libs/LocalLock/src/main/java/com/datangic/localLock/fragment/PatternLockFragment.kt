package com.datangic.localLock.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.datangic.localLock.*
import com.datangic.localLock.utils.SystemUtils
import com.datangic.localLock.widgets.PatternLockView
import com.google.android.material.textview.MaterialTextView
import java.util.logging.Logger
import kotlin.properties.Delegates

class PatternLockFragment : Fragment() {
    lateinit var mLockerView: PatternLockView
    lateinit var mPatternTips: MaterialTextView
    lateinit var mLockTips: TextView
    var mMode by Delegates.notNull<Int>()
    var verifyPassword: String = ""
    var secondType: Int = 0
    private var curMode: Int = 0
    private var newPassword: String? = null

    private val mHandler by lazy { Handler(Looper.getMainLooper()) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pattern_lock_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mLockerView = view.findViewById(R.id.locker_view)
        mPatternTips = view.findViewById(R.id.pattern_tips)
        val activity = requireActivity()
        if (activity is LockerActivity) {
            mMode = activity.extra?.action ?: LockAction.VERIFICATION
            verifyPassword = activity.extra?.password ?: ""
            secondType = activity.extra?.secondType ?: 0
        }

        mLockerView.addPatternLockListener(onListener)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mActivity: LockerActivity = requireActivity() as LockerActivity
        mLockTips = mActivity.getTipText()

        when (mMode) {
            LockAction.NEW_PASSWORD -> {
                mLockTips.apply {
                    setOnClickListener {
                        curMode = LockStatus.INPUT_PASSWORD
                        mLockTips.visibility = View.INVISIBLE
                        newPassword = null
                        mPatternTips.setText(R.string.new_password)
                        mLockerView.clearPattern()
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
                        mPatternTips.setText(R.string.new_password)
                        mLockerView.clearPattern()
                    }
                    setText(R.string.reset_password)
                }
                if (mActivity.extra?.type != LockType.PATTERN && mActivity.extra?.secondType == LockType.PATTERN) {
                    curMode = LockStatus.INPUT_PASSWORD
                    mPatternTips.setText(R.string.new_password)
                } else {
                    mPatternTips.setText(R.string.old_password)
                }
            }
        }
    }

    private val onListener = object : PatternLockViewListener {
        override fun onStarted() {}

        override fun onProgress(progressPattern: List<PatternLockView.Dot>) {}

        override fun onComplete(pattern: List<PatternLockView.Dot>) {
            if (pattern.size < 4) {
                mLockerView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                setTips(R.string.password_pattern_last_node, true)
                mHandler.postDelayed({
                    setTips(R.string.password_pattern)
                }, 2000)
                return
            }
            val tempPwd = getPassword(pattern)
            when (mMode) {
                LockAction.NEW_PASSWORD -> {
                    when (curMode) {
                        in listOf(LockStatus.INPUT_PASSWORD, 0) -> {
                            newPassword = tempPwd
                            curMode = LockStatus.CONFIRM_PASSWORD
                            mPatternTips.setText(R.string.confirm_password)
                            mLockerView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
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
                                mPatternTips.setText(R.string.new_password)
                                mLockerView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                                mHandler.removeCallbacks(runnable(R.string.old_password))
                                if (secondType != 0 && secondType != LockType.PATTERN) {
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
                            mPatternTips.setText(R.string.confirm_password)
                            mLockerView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
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
                        setTips(R.string.password_is_wrong, true)
                        SystemUtils.starVibrate(requireContext())
                        mLockerView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                        (requireActivity() as LockerActivity).authError()
                    }
                }
            }
        }

        override fun onCleared() {}
    }

    private fun getPassword(pattern: List<PatternLockView.Dot>): String {
        var tempStr = ""
        for (i in pattern) {
            tempStr += i.mCount
        }
        Log.e("Pattern", "Pwd=$tempStr")
        return tempStr
    }


    private fun setResult(password: String, successful: Boolean) {
        (requireActivity() as LockerActivity).setResult(
            LocalLockResult(
                mMode,
                LockType.PATTERN,
                password = password,
                successful
            )
        )
    }

    private fun setTips(@StringRes resId: Int, wrong: Boolean = false) {
        mPatternTips.setText(resId)
    }

    private fun wrongTips(@StringRes resId: Int) {
        mPatternTips.setText(R.string.password_is_wrong)
        mLockerView.setViewMode(PatternLockView.PatternViewMode.WRONG)
        mHandler.postDelayed(runnable(resId), 2000)
    }

    private fun runnable(@StringRes resId: Int) = Runnable { mPatternTips.setText(resId) }

}