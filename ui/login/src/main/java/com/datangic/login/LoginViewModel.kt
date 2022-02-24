package com.datangic.login

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.datangic.common.utils.isPhoneNumber
import com.datangic.login.data.LoginFormState
import com.datangic.login.data.LoginState

class LoginViewModel(application: Application, private val loginRepository: LoginRepository) : AndroidViewModel(application) {

    val state
        get() = LoginState

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)
        result.getOrNull()?.let {
            LoginState.loginResult.value = LoginResult(it)
        } ?: let {
            LoginState.loginResult.value = LoginResult(error = R.string.error_login)
        }

    }

    fun getUsernameDone() {
        Log.d("TAG", "Valid username =${state.username.value}")
        // can be launched in a separate asynchronous job
        if (!isUserNameValid(username = state.username.value)) {
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

    }

    fun getVerifyCode() {

    }

    fun onBackPressed(): Boolean {
        val result = state.loginStep.value in listOf(LoginState.LoginStep.START_LOGIN, LoginState.LoginStep.INPUT_USER)
        state.loginChange()
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
}




