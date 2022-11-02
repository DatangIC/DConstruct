package com.datangic.login


import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.datangic.components.components.AlphaText
import com.datangic.components.components.GetVerifyCode
import com.datangic.components.components.TextInput
import com.datangic.components.components.Title
import com.datangic.components.themes.BlueA400
import com.datangic.components.themes.TypeSize
import com.datangic.components.themes.withDefaultColor
import com.datangic.login.data.LoginState
import com.datangic.themes.R
import org.koin.androidx.compose.get

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginPage(
    viewModel: LoginViewModel = get(),
    horizontal: Dp = TypeSize.middle_10,
    vertical: Dp = TypeSize.large_36
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(horizontal = horizontal, vertical = vertical)
            .fillMaxSize()
    ) {
        val (titleIndicator, emailIndicator, textExplain, buttonIndicator) = createRefs()
        AnimatedVisibility(visible = viewModel.state.loginStep.value != LoginState.LoginStep.START_LOGIN) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .padding(TypeSize.middle_10)
                    .clickable { viewModel.onBackPressed() },
                tint = MaterialTheme.colors.primary
            )
        }
        val titleModifier = Modifier
            .padding(top = TypeSize.large_64, start = TypeSize.middle_14, end = TypeSize.middle_12)
            .animateContentSize()
            .constrainAs(titleIndicator) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
            }
        when (viewModel.state.loginStep.value) {
            LoginState.LoginStep.START_LOGIN, LoginState.LoginStep.INPUT_PHONE_DONE -> InputPhone(viewModel, titleModifier)
            else -> InputUser(viewModel, titleModifier)
        }
        ClickableText(
            text = AnnotatedString(
                stringResource(
                    id = if (viewModel.state.loginStep.value in listOf(
                            LoginState.LoginStep.START_LOGIN,
                            LoginState.LoginStep.INPUT_PHONE_DONE
                        )
                    ) R.string.login_with_email_explain else R.string.login_with_phone_explain
                ),
                SpanStyle(color = BlueA400.copy(ContentAlpha.medium), fontWeight = FontWeight.Bold)
            ).withDefaultColor(),
            modifier = Modifier
                .padding(top = TypeSize.middle_10, start = TypeSize.middle_14)
                .constrainAs(emailIndicator) {
                    top.linkTo(titleIndicator.bottom)
                },
            onClick = { viewModel.state.stateChange(true) },
        )
        AlphaText(ContentAlpha.disabled) {
            Text(
                text = stringResource(id = R.string.username_explain, stringResource(id = R.string.uphone)),
                modifier = Modifier.constrainAs(textExplain) {
                    bottom.linkTo(buttonIndicator.top)
                    centerHorizontallyTo(parent)
                },
                style = MaterialTheme.typography.overline
            )
        }
        Button(
            onClick = { viewModel.goLogin() },
            modifier = Modifier
                .padding(bottom = TypeSize.large_40, top = TypeSize.middle_14)
                .constrainAs(buttonIndicator) {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                }) {
            Text(
                text = stringResource(id = R.string.go_on),
                Modifier.padding(horizontal = TypeSize.large_48, vertical = TypeSize.size_4)
            )
        }
    }
}

@Composable
fun InputPhone(mViewModel: LoginViewModel, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Title(
            text = stringResource(id = R.string.login_with_phone),
            color = MaterialTheme.colors.primary
        )
        TextInput(
            text = mViewModel.state.userPhone.value,
            onValueChange = { mViewModel.state.userPhone.value = it },
            clear = mViewModel.state.loginStep.value == LoginState.LoginStep.START_LOGIN,
            label = stringResource(id = R.string.uphone),
            keyboardActions = KeyboardActions() {
                mViewModel.getUsernameDone()
            },
            leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone") },
            imeAction = ImeAction.Go,
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.fillMaxWidth(),
            enable = mViewModel.state.loginStep.value == LoginState.LoginStep.START_LOGIN
        )
        AnimatedVisibility(
            mViewModel.state.loginStep.value == LoginState.LoginStep.INPUT_PHONE_DONE,
            enter = expandVertically(
                // Expand from the top.
                expandFrom = Alignment.Top
            ) + fadeIn(
                // Fade in with the initial alpha of 0.3f.
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            TextInput(
                text = mViewModel.state.verifyCode.value,
                onValueChange = { mViewModel.state.verifyCode.value = it },
                label = stringResource(id = R.string.verification_code),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = stringResource(id = R.string.verification_code)
                    )
                },
                keyboardActions = KeyboardActions() {
                    mViewModel.goLogin()
                },
                imeAction = ImeAction.Go,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { GetVerifyCode(60, onClick = { mViewModel.getVerifyCode() }) }
            )
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InputUser(mViewModel: LoginViewModel, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Title(
            text = stringResource(id = R.string.login_with_email),
            color = MaterialTheme.colors.primary
        )
        TextInput(
            text = mViewModel.state.email.value,
            onValueChange = { mViewModel.state.email.value = it },
            clear = mViewModel.state.loginStep.value == LoginState.LoginStep.INPUT_USER,
            label = stringResource(id = R.string.email),
            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Email") },
            keyboardType = KeyboardType.Email,
            modifier = Modifier
                .fillMaxWidth(),
            enable = mViewModel.state.loginStep.value == LoginState.LoginStep.INPUT_USER
        )

        TextInput(
            text = mViewModel.state.password.value,
            onValueChange = { mViewModel.state.password.value = it },
            label = stringResource(id = R.string.upassword),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Password,
                    contentDescription = stringResource(id = R.string.upassword)
                )
            },
            imeAction = ImeAction.Go,
            keyboardType = KeyboardType.Password,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun LoginPageView() {
    LoginPage()
}