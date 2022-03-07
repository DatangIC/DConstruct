package com.datangic.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.alibaba.android.arouter.facade.annotation.Route
import com.datangic.components.components.LoadingDialog
import com.datangic.components.components.TipsDialog
import com.datangic.components.themes.LoginTheme
import com.datangic.libs.base.LOGIN_ACTIVITY
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = LOGIN_ACTIVITY)
class LoginActivity : ComponentActivity() {

    private val mViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    if (mViewModel.state.mDialogError.isShow()) {
                        TipsDialog(mViewModel.state.mDialogError, onDismiss = { mViewModel.state.userPhone.value = "" })
                    }
                    if (mViewModel.state.mLoadingDialog.value) {
                        LoadingDialog(dismissOnBackPress = true) { mViewModel.onDialogDismissed() }
                    }
                    LoginPage(mViewModel)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (mViewModel.onBackPressed())
            super.onBackPressed()
    }
}

