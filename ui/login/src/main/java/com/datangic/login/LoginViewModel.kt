package com.datangic.login

import android.app.Application
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.launcher.ARouter
import com.datangic.common.RouterList
import com.datangic.common.utils.isPhoneNumber
import com.datangic.libs.base.ApplicationProvider
import com.datangic.libs.base.ApplicationProvider.Companion.getCurrentActivity
import com.datangic.libs.base.dataSource.UserSource
import com.datangic.login.data.LoginComposeState
import com.datangic.login.data.LoginFormState
import com.datangic.network.RequestStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, private val mUserSource: UserSource) : AndroidViewModel(application) {

    private val TAG = LoginViewModel::class.java.simpleName

    private val mActivity by lazy { getApplication<ApplicationProvider>().getCurrentActivity() as LoginActivity }

    val loginComposeState
        get() = LoginComposeState

    fun getUsernameDone() {
        // can be launched in a separate asynchronous job
        if (!isUserNameValid(username = loginComposeState.userPhone.value)) {
            if (loginComposeState.loginStep.value == LoginComposeState.LoginStep.START_LOGIN) {
                loginComposeState.mDialogError.change(R.string.error_phone_title, R.string.error_phone_explain)
            } else if (loginComposeState.loginStep.value == LoginComposeState.LoginStep.INPUT_USER) {
                loginComposeState.mDialogError.change(R.string.error_username, R.string.error_username)
            }
            loginComposeState.mDialogError.show()
        } else {
            loginComposeState.goNext()
        }
    }

    fun goLogin() {
        if (loginComposeState.loginStep.value in listOf(LoginComposeState.LoginStep.INPUT_USER, LoginComposeState.LoginStep.START_LOGIN)) {
            getUsernameDone()
        } else {
            if (loginComposeState.userPhone.value.isNotEmpty() || loginComposeState.email.value.isNotEmpty()) {
                if (loginComposeState.password.value.isNotEmpty() || loginComposeState.verifyCode.value.isNotEmpty()) {
                    mUserSource.loginOrRegister(
                        email = loginComposeState.email.value,
                        username = loginComposeState.userPhone.value,
                        password = loginComposeState.password.value,
                        code = loginComposeState.verifyCode.value
                    ) { res ->
                        when (res.requestStatus) {
                            RequestStatus.SUCCESS -> {
                                ARouter.getInstance().build(RouterList.MAIN_ACTIVITY)
                                    .withTransition(org.koin.android.R.anim.abc_popup_enter, org.koin.android.R.anim.abc_popup_exit)
//                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .navigation()
//                                Activity.finish()
                                loginComposeState.showLoading(false)
                            }
                            RequestStatus.ERROR -> {
                                loginComposeState.showLoading(false)
                                runInMainThread {
                                    Toast.makeText(mActivity, "Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else -> loginComposeState.showLoading(true)
                        }


                    }
                } else return
            }

        }
    }

    fun getVerifyCode() {
        mUserSource.getVerifyCode(loginComposeState.userPhone.value) {
            when (it.requestStatus) {
                RequestStatus.SUCCESS -> {
                    loginComposeState.showLoading(false)
                    it.data?.smsCode?.let { smsCode ->
                        loginComposeState.verifyCode.value = smsCode
                    }
                }
                RequestStatus.LOADING -> {
                    loginComposeState.showLoading(true)
                }
                else -> loginComposeState.showLoading(false)
            }
        }

    }

    fun onDialogDismissed() {
        loginComposeState.showLoading(false)
    }

    fun onBackPressed(): Boolean {
        val result =
            loginComposeState.loginStep.value in listOf(LoginComposeState.LoginStep.START_LOGIN, LoginComposeState.LoginStep.INPUT_USER)
        loginComposeState.stateChange(false)
        return result
    }


    fun loginDataChanged(username: String, password: String) {
        loginComposeState.loginFormState.value = if (!isUserNameValid(username)) {
            LoginFormState(usernameError = R.string.error_username)
        } else if (!isPasswordValid(password)) {
            LoginFormState(passwordError = R.string.error_password)
        } else {
            LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return when {
            username.contains('@') -> {
                Patterns.EMAIL_ADDRESS.matcher(username).matches()
            }
            username.isPhoneNumber() -> {
                true
            }
            else -> {
                false
            }
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun runInMainThread(run: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) { run() }
    }
}




