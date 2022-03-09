package com.datangic.smartlock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.datangic.localLock.*
import com.datangic.smartlock.preference.LanguageHelper
import com.datangic.smartlock.respositorys.LocalPasswordRepository
import com.datangic.smartlock.utils.REQUEST_VERIFICATION_PASSWORD
import org.koin.android.ext.android.inject

//@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val localPasswordRepository: LocalPasswordRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onStart() {
        super.onStart()
        localPasswordRepository.mLocalPassword?.let {
            localPasswordRepository.startVerificationForResult(this, false)
        } ?: navigateActivity()
    }

    private fun navigateActivity() {
        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    private fun navigateLockActivity(local: Triple<Int, String, Boolean>) {
        val intent = Intent(this@SplashScreenActivity, LockerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).apply {
            this.putExtra(
                LOCKER_TYPE, LocalLockExtra(
                    LockAction.VERIFICATION,
                    local.first,
                    local.second,
                    secondType = if (local.third) LockType.FINGERPRINT else 0,
                    isBackPressed = false
                )
            )
        }
        startActivityForResult(intent, 1023)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("TAG", "RESULT requestCode=$requestCode, resultCode=$resultCode")

        if (requestCode == REQUEST_VERIFICATION_PASSWORD) {
            data?.getParcelableExtra<LocalLockResult>(LOCKER_RESULT)?.let {
                if (it.success) {
                    navigateActivity()
                } else {
                    finish()
                }
            }
        } else finish()
    }

}