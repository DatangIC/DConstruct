package com.datangic.components.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.datangic.common.utils.CounterDown
import com.datangic.components.themes.DConstructTheme
import com.datangic.themes.R
import com.datangic.components.themes.TypeSize
import com.datangic.components.ui.LoginPost

//@Composable
//fun LoginPost(
//    post: LoginPost,
//    onLoginClick: (String, String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val username = remember {
//        mutableStateOf(post.uid)
//    }
//    val password = remember {
//        mutableStateOf(post.upwd)
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.Transparent),
//    ) {
//        Spacer(Modifier.height(TypeSize.middle_10))
//        TextInput(
//            text = username.value,
//            onValueChange = { username.value = it },
//            label = stringResource(id = R.string.uphone),
//            keyboardType = KeyboardType.Phone,
//            leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = stringResource(id = R.string.uphone)) },
//            modifier = Modifier
//                .padding(horizontal = TypeSize.large_36)
//                .fillMaxWidth()
//        )
//        Spacer(Modifier.height(TypeSize.middle_10))
//        TextInput(
//            text = password.value,
//            onValueChange = { password.value = it },
//            label = stringResource(id = R.string.upassword),
//            keyboardType = KeyboardType.Password,
//            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = stringResource(id = R.string.upassword)) },
//            modifier = Modifier
//                .padding(horizontal = TypeSize.large_36)
//                .fillMaxWidth()
//        )
//        Button(
//            onClick = { onLoginClick(username.value, password.value) },
//            modifier = Modifier
//                .padding(horizontal = TypeSize.large_36, vertical = TypeSize.middle_24)
//                .height(TypeSize.large_44)
//                .fillMaxWidth()
//        ) {
//            Text(stringResource(id = R.string.login))
//        }
//    }
//}

@Composable
fun GetVerifyCode(delay: Int = 30, onClick: () -> Unit) {
    val mHasGet = remember {
        mutableStateOf(false)
    }
    val mDelay = remember {
        mutableStateOf(delay)
    }
    TextButton(
        onClick = {
            onClick()
            mHasGet.value = !mHasGet.value
            if (mHasGet.value) {
                CounterDown.countDownCoroutines(
                    mDelay.value,
                    onTick = { mDelay.value = it },
                    onFinish = {
                        mDelay.value = delay
                        mHasGet.value = false
                    }
                )
            }
        },
        enabled = !mHasGet.value
    ) {
        Text(
            text = if (mHasGet.value) {
                stringResource(id = R.string.delay_s, mDelay.value)
            } else stringResource(id = R.string.get_verify_code),
            color = if (mHasGet.value) Color.Red.copy(alpha = 0.5F) else Color.Unspecified,
        )
    }
}

@Composable
fun RegisterPost(
    post: LoginPost,
    onRegisterClick: () -> Unit,
    onGetVerifyCode: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
    ) {
        var mCode: Boolean = false
        Title(text = stringResource(id = R.string.register))
        Spacer(Modifier.height(TypeSize.middle_10))
        TextInput(
            text = post.uid,
            label = stringResource(id = R.string.uphone),
            onValueChange = { },
            keyboardType = KeyboardType.Phone,
            leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = stringResource(id = R.string.uphone)) },
            modifier = Modifier
                .padding(horizontal = TypeSize.large_36)
                .fillMaxWidth()
        )
        if (onGetVerifyCode != null) {
            Spacer(Modifier.height(TypeSize.middle_10))
            TextInput(
                text = post.ucode,
                onValueChange = { },
                label = stringResource(id = R.string.verification_code),
                keyboardType = KeyboardType.Number,
                leadingIcon = { Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = stringResource(id = R.string.upassword)) },
                modifier = Modifier
                    .padding(horizontal = TypeSize.large_36)
                    .fillMaxWidth(),
                trailingIcon = { GetVerifyCode(12) { mCode = !mCode } }
            )
        }
        Spacer(Modifier.height(TypeSize.middle_10))
        TextInput(
            text = post.upwd,
            onValueChange = { },
            label = stringResource(id = R.string.upassword),
            keyboardType = KeyboardType.Password,
            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = stringResource(id = R.string.upassword)) },
            modifier = Modifier
                .padding(horizontal = TypeSize.large_36)
                .fillMaxWidth()
        )
        Button(
            onClick = { onRegisterClick() },
            modifier = Modifier
                .padding(horizontal = TypeSize.large_36, vertical = TypeSize.middle_24)
                .height(TypeSize.large_44)
                .fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.login))
        }
    }
}


@Preview(name = "Register")
@Composable
fun RegisterPostPreview() {
    DConstructTheme() {
        RegisterPost(post = LoginPost(
            "",
            "126345@3"
        ),
            onRegisterClick = { },
            onGetVerifyCode = {})
    }
}