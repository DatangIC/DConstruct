package com.datangic.login

import android.app.Application
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.datangic.api.login.LoginDataResult
import com.datangic.common.utils.isPhoneNumber
import com.datangic.libs.base.ApplicationProvider
import com.datangic.libs.base.ApplicationProvider.Companion.getCurrentActivity
import com.datangic.login.data.LoginFormState
import com.datangic.login.data.LoginState
import com.datangic.network.RequestStatus
import com.datangic.network.ResponseState
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, private val loginRepository: LoginRepository) : AndroidViewModel(application) {

    private val TAG = LoginViewModel::class.java.simpleName

    private val mActivity by lazy { getApplication<ApplicationProvider>().getCurrentActivity() as LoginActivity }

    val state
        get() = LoginState

    fun getUsernameDone() {
        Log.d("TAG", "Valid username =${state.userPhone.value}")
        // can be launched in a separate asynchronous job
        if (!isUserNameValid(username = state.userPhone.value)) {
            if (state.loginStep.value == LoginState.LoginStep.START_LOGIN) {
                state.mDialogError.change(R.string.error_phone_title, R.string.error_phone_explain)
            } else if (state.loginStep.value == LoginState.LoginStep.INPUT_USER) {
                state.mDialogError.change(R.string.error_username, R.string.error_username)
            }
            state.mDialogError.show()
        } else {
            state.goNext()
        }
    }

    fun goLogin() {
        if (state.loginStep.value in listOf(LoginState.LoginStep.INPUT_USER, LoginState.LoginStep.START_LOGIN)) {
            getUsernameDone()
        } else {

            if (state.userPhone.value.isNotEmpty() || state.email.value.isNotEmpty()) {
                if (state.password.value.isNotEmpty() || state.verifyCode.value.isNotEmpty()) {
                    loginRepository.login(
                        email = state.email.value,
                        username = state.userPhone.value,
                        password = state.password.value,
                        code = state.verifyCode.value
                    ).subscribe(object : Observer<ResponseState<LoginDataResult>> {
                        override fun onSubscribe(d: Disposable) {
                            state.showLoading(true)
                        }

                        override fun onNext(t: ResponseState<LoginDataResult>) {
                            Log.e(TAG, "result=${t}")
                        }

                        override fun onError(e: Throwable) {
                            runInMainThread {
                                Toast.makeText(mActivity, "Error", Toast.LENGTH_SHORT).show()
                            }
                            state.showLoading(false)
                        }

                        override fun onComplete() {
                            state.showLoading(false)
                        }
                    })
                } else return
            } else return
        }
    }

    fun getVerifyCode() {
        loginRepository.getVerifyCode("123434").doOnError {
            Log.e(TAG, "getVerifyCode =$it")
        }.subscribe {
            when (it.requestStatus) {
                RequestStatus.SUCCESS -> {

                }
                else -> {
                }
            }
        }
    }

    fun onDialogDismissed() {
        state.showLoading(false)
    }

    fun onBackPressed(): Boolean {
        val result = state.loginStep.value in listOf(LoginState.LoginStep.START_LOGIN, LoginState.LoginStep.INPUT_USER)
        state.stateChange(false)
        return result
    }


    fun loginDataChanged(username: String, password: String) {
        state.loginFormState.value = if (!isUserNameValid(username)) {
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




