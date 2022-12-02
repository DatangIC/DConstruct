package com.datangic.dconstruct

import android.annotation.SuppressLint
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.datangic.common.RouterList
import com.datangic.common.RouterList.LOGIN_ACTIVITY
import com.datangic.common.RouterList.MAIN_ACTIVITY
import com.datangic.common.utils.Logger
import com.datangic.components.themes.*
import com.datangic.data.DatabaseRepository
import com.datangic.data.LogStatus
import com.datangic.libs.base.dataSource.UserSource
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@Route(path = RouterList.SPLASH_ACTIVITY)
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    private val mHandler = Handler(Looper.getMainLooper())
    private val mUseSource: UserSource by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUseSource.getUserInfo()
        Log.e("SplashScreenActivity", "onCreate")
        mUseSource.mDatabase.setLogUserObservable(this) { logUser ->
            when (logUser.status) {
                LogStatus.INIT,
                LogStatus.LOGGING -> {
                }
                LogStatus.NO_NET,
                LogStatus.LOGGED,
                LogStatus.NOT_AUTH -> {
                    navigateActivity(MAIN_ACTIVITY)
                }
                LogStatus.NOT_LOGIN -> {
                    navigateActivity(LOGIN_ACTIVITY)
                }
            }
        }
        setContent {
            MaterialTheme(
                colors = TransparentColorPalette,
                typography = Typography,
                shapes = Shapes,
            ) {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent
                )
            }
        }
    }


    private val navigationMain = Runnable {
        loginWithAuth()
    }

    private fun loginWithAuth() {

        navigateActivity(LOGIN_ACTIVITY)
    }

    private fun navigateActivity(path: String) {
        MainScope().launch(Dispatchers.Main) {
            Logger.e("SplashScreenActivity", "path=${path}")
            if (path == LOGIN_ACTIVITY)
                ARouter.getInstance()
                    .build(path)
                    .addFlags(FLAG_ACTIVITY_NO_HISTORY)
                    .navigation()
            else
                ARouter.getInstance()
                    .build(path)
                    .navigation()
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onDestroy() {
        mHandler.removeCallbacks(navigationMain)
        super.onDestroy()
    }
}