package com.datangic.dconstruct

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.datangic.api.login.LoginApi
import com.datangic.common.RouterList
import com.datangic.components.themes.DConstructTheme

@Route(path = RouterList.SPLASH_ACTIVITY)
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DConstructTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    ARouter.getInstance().build(RouterList.LOGIN_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).navigation()
//                    ARouter.getInstance().build(RouterList.MAIN_ACTIVITY).navigation()
//                    finish()
                }
            }
        }
    }

    private val navigationToMain = Runnable {
//        LoginApi.create().getAuthCode() /
    }
}