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
import com.datangic.components.rememberRandomSampleImageUrl
import com.datangic.components.themes.DConstructTheme
import com.datangic.components.themes.TypeSize
import com.datangic.components.ui.SplashNavigation
import com.datangic.libs.base.Router

@Route(path = Router.SPLASH_ACTIVITY)
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DConstructTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    SplashNavigation(
                        data = listOf(
                            rememberRandomSampleImageUrl(width = 1080),
                            rememberRandomSampleImageUrl(width = 1080),
                            rememberRandomSampleImageUrl(width = 1080),
                            rememberRandomSampleImageUrl(width = 1080),
                        ),
                        vertical = TypeSize.large_128,
                        margin = TypeSize.large_36,
                    ) {
                        ARouter.getInstance().build(Router.MAIN_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).navigation()
                        finish()
                    }
                }
            }
        }
    }
}