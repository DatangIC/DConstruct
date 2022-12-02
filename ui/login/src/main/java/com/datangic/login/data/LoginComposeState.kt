package com.datangic.login.data

import androidx.compose.runtime.mutableStateOf
import com.datangic.components.data.TipsDialogState

object LoginComposeState {
    enum class LoginStep {
        START_LOGIN, START_REGISTER, EDIT_DETAIL,
        INPUT_USER,
        INPUT_PHONE_DONE, INPUT_USER_DONE,
        GET_VERIFY_CODE, GET_VERIFY_CODE_DONE,
    }

    val loginStep = mutableStateOf(LoginStep.START_LOGIN)
    val email = mutableStateOf("")
    val userPhone = mutableStateOf("")
    val password = mutableStateOf("")
    val verifyCode = mutableStateOf("")
    val mDialogError = TipsDialogState()
    val mLoadingDialog = mutableStateOf(false)

    val loginFormState = mutableStateOf(LoginFormState())

    init {
        loginStep.value = LoginStep.START_LOGIN
        email.value = ""
        userPhone.value = ""
        password.value = ""
        verifyCode.value = ""
        mLoadingDialog.value = false
    }

    fun showLoading(boolean: Boolean) {
        mLoadingDialog.value = boolean
    }

    fun stateChange(isLogin: Boolean = false) {
        when (loginStep.value) {
            LoginStep.START_LOGIN, LoginStep.INPUT_PHONE_DONE -> {
                loginStep.value = if (isLogin) LoginStep.INPUT_USER else LoginStep.START_LOGIN
            }
            LoginStep.INPUT_USER, LoginStep.INPUT_USER_DONE -> {
                loginStep.value = if (isLogin) LoginStep.START_LOGIN else LoginStep.INPUT_USER
            }
            else -> loginStep.value = LoginStep.START_LOGIN
        }
        userPhone.value = ""
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

    fun clearData() {
        loginStep.value = LoginStep.START_LOGIN
        email.value = ""
        userPhone.value = ""
        password.value = ""
        verifyCode.value = ""
        mLoadingDialog.value = false
    }
}