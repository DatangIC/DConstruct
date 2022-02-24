package com.datangic.login.data

import androidx.compose.runtime.mutableStateOf
import com.datangic.components.data.TipsDialogState
import com.datangic.login.LoginResult

object LoginState {
    enum class LoginStep {
        START_LOGIN, START_REGISTER, EDIT_DETAIL,
        INPUT_USER,
        INPUT_PHONE_DONE, INPUT_USER_DONE,
        GET_VERIFY_CODE, GET_VERIFY_CODE_DONE,
    }

    val loginStep = mutableStateOf(LoginStep.START_LOGIN)
    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val verifyCode = mutableStateOf("")
    val mDialogError = TipsDialogState()

    val loginFormState = mutableStateOf(LoginFormState())
    val loginResult = mutableStateOf(LoginResult())

    fun loginChange() {
        when (loginStep.value) {
            LoginStep.START_LOGIN, LoginStep.INPUT_PHONE_DONE -> {
                loginStep.value = LoginStep.INPUT_USER
            }
            LoginStep.INPUT_USER, LoginStep.INPUT_USER_DONE -> {
                loginStep.value = LoginStep.START_LOGIN
            }
            else -> loginStep.value = LoginStep.START_LOGIN
        }
        username.value = ""
        password.value = ""
        verifyCode.value = ""
    }

    fun goNext() {
        when (loginStep.value) {
            LoginStep.START_LOGIN -> {
                loginStep.value = LoginStep.INPUT_PHONE_DONE
            }
            LoginStep.INPUT_USER -> {
                loginStep.value = LoginStep.INPUT_USER_DONE
            }
            else -> {
            }
        }
    }
}