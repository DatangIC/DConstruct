package com.datangic.localLock

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.datangic.localLock.LockType.Companion.FINGERPRINT
import com.datangic.localLock.LockType.Companion.PASSWORD
import com.datangic.localLock.LockType.Companion.PATTERN
import com.datangic.localLock.utils.SharePreferenceUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textview.MaterialTextView
import kotlin.properties.Delegates

const val LOCKER_TYPE = "LOCKER_TYPE"
const val LOCKER_RESULT = "LOCKER_RESULT"

class LockerActivity : AppCompatActivity() {

    private lateinit var mNavController: NavController
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var bottomSheet: BottomSheetBehavior<*>
    lateinit var mLockTips: TextView
    var extra: LocalLockExtra? = null

    var curMode by Delegates.notNull<Int>()

    private var mDefaultTimes = 4
    var mErrorCount = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locker_activity)
        mNavController = Navigation.findNavController(this, R.id.nav_locker_fragment)
        mLockTips = this.findViewById<TextView>(R.id.lock_tips)
    }

    private fun startFragment(extra: LocalLockExtra) {
        val time = System.currentTimeMillis() / 1000 - SharePreferenceUtils.getLongValue(this, SharePreferenceUtils.AUTH_ERROR) <= 60

        if (time) {
            mLockTips.visibility = View.GONE
            mNavController.navigate(R.id.lockedFragment)
        } else {
            changeVerified(if (extra.secondType != 0 && extra.action == LockAction.VERIFICATION) extra.secondType else extra.type)
        }
    }

    fun changeVerified(@LockType mode: Int = extra?.type ?: PATTERN) {
        curMode = mode
        if (extra?.secondType != 0 && extra?.action == LockAction.VERIFICATION) {
            mLockTips.apply {
                setOnClickListener {
                    extra?.let {
                        changeVerified(if (it.secondType == curMode) it.type else it.secondType)
                    }
                }
                visibility = View.VISIBLE
            }
        }
        when (mode) {
            PATTERN -> {
                mNavController.navigate(R.id.patternLockFragment)
            }
            PASSWORD -> {
                mNavController.navigate(R.id.passwordLockFragment)
            }
            FINGERPRINT -> {
                mNavController.navigate(R.id.fingerprintLockFragment)
            }
        }
    }

    fun setResult(extra: LocalLockResult) {
        setResult(RESULT_OK,
            Intent().apply {
                putExtra(LOCKER_RESULT, extra)
            })
        finish()
    }

    fun getTipText(): TextView {
        return this.mLockTips
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onResume() {
        super.onResume()
        extra = intent.getParcelableExtra(LOCKER_TYPE)
        extra?.let { it ->
            startFragment(it)
            if (it.isBackPressed) {
                findViewById<MaterialToolbar>(R.id.toolbar).apply {
                    this@LockerActivity.setSupportActionBar(this)
                    this@LockerActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    this.setNavigationOnClickListener {
                        this@LockerActivity.onBackPressed()
                    }
                    this.navigationIcon = getDrawable(R.drawable.ic_close_24)
                    this.title = ""
                }
            } else {
                findViewById<AppBarLayout>(R.id.appbar_layout).visibility = View.GONE
            }
        }
        mDefaultTimes = extra?.errorCount ?: SharePreferenceUtils.DEFAULT_COUNT
        mErrorCount = 0

        nestedScrollView = findViewById(R.id.nested_scroll_view)
        bottomSheet = BottomSheetBehavior.from(nestedScrollView)
        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    fun authError() {
        if (++mErrorCount >= mDefaultTimes) {
            SharePreferenceUtils.storeValue(this, SharePreferenceUtils.AUTH_ERROR, System.currentTimeMillis() / 1000)
            mLockTips.visibility = View.GONE
            mNavController.navigate(R.id.lockedFragment)
        }
    }


    fun reAuth() {
        extra?.let {
            mErrorCount = 0
            changeVerified(if (it.secondType != 0 && it.action == LockAction.VERIFICATION) it.secondType else it.type)
        }
    }

    override fun onBackPressed() {
        if (extra?.isBackPressed == false) {
            return
        }
        super.onBackPressed()
    }

}