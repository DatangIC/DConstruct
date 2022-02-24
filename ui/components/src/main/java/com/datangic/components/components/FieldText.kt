package com.datangic.components.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import com.datangic.components.themes.DConstructTheme
import com.datangic.components.themes.TypeSize
import com.datangic.themes.R


@Composable
fun Title(text: String, color: Color = Color.Unspecified, modifier: Modifier = Modifier) {
    Text(text = text, style = MaterialTheme.typography.h5, color = color, modifier = modifier)
}

@Composable
fun AlphaText(alpha: Float = ContentAlpha.high, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentAlpha provides alpha, content = content)
}


@Composable
fun PhoneNumber(
    text: String,
    onValueChange: (String) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        onValueChange = { onValueChange(it) },
        label = { Text(text = stringResource(id = R.string.uphone)) },
        leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = "PhoneIcon") },
        trailingIcon = {
            if (text.isNotEmpty()) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "CloseIcon", modifier = Modifier.clickable { onValueChange("") })
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        keyboardActions = keyboardActions,
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        ),
        modifier = modifier,
    )
}

@Composable
fun TextInput(
    text: String,
    onValueChange: (String) -> Unit,
    enable: Boolean = true,
    clear: Boolean = true,
    label: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Default,
    keyboardType: KeyboardType = KeyboardType.Text,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
    keyboardActions: KeyboardActions = KeyboardActions(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        onValueChange = { onValueChange(it) },
        label = { Text(text = label) },
        leadingIcon = leadingIcon,
        trailingIcon = {
            trailingIcon?.let {
                it()
            } ?: if (clear && text.isNotEmpty()) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "CloseIcon", modifier = Modifier.clickable { onValueChange("") })
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        visualTransformation = if (keyboardType == KeyboardType.Password || keyboardType == KeyboardType.NumberPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardActions = keyboardActions,
        singleLine = true,
        colors = colors,
        modifier = modifier,
        enabled = enable,
    )

}
//
//@Composable
//fun TextInput(
//    text: String,
//    onValueChange: (String) -> Unit,
//    keyboardType: KeyboardType = KeyboardType.Text,
//    label: String = "",
//    placeholder: @Composable (() -> Unit)? = null,
//    leadingIcon: @Composable (() -> Unit)? = null,
//    trailingIcon: @Composable (() -> Unit)? = null,
//    modifier: Modifier = Modifier
//        .padding(TypeSize.middle_12)
//        .fillMaxWidth()
//) {
//    TextField(
//        value = text,
//        onValueChange = onValueChange,
//        label = { Text(text = label) },
//        placeholder = placeholder,
//        leadingIcon = leadingIcon,
//        trailingIcon = trailingIcon ?: {
//            if (text.isNotEmpty()) {
//                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", modifier = Modifier.clickable { onValueChange("") })
//            }
//        },
//        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
//        visualTransformation =
//        singleLine = true,
//        shape = RoundedCornerShape(topStart = TypeSize.middle_10, topEnd = TypeSize.middle_10),
//        modifier = modifier,
//    )
//}
//
//
//@Preview("Title", backgroundColor = 0xffffff)
//@Composable
//private fun TitlePreview() {
//    DConstructTheme(false) {
//        Column(modifier = Modifier
//            .fillMaxWidth()
//            .clickable {
//            }) {
//            Title(
//                text = "Login",
//                Modifier
//                    .padding(horizontal = TypeSize.middle_10, vertical = TypeSize.size_4)
//                    .fillMaxWidth()
//            )
//            Spacer(Modifier.height(TypeSize.middle_10))
//            TextInput(
//                text = "User",
//                label = "Phone",
//                onValueChange = {},
//                leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = "phone") },
//                modifier = Modifier
//                    .padding(horizontal = TypeSize.large_36)
//                    .fillMaxWidth(),
//            )
//            Spacer(Modifier.height(TypeSize.middle_12))
//            TextInput(
//                text = "Password",
//                label = "Password",
//                onValueChange = {},
//                keyboardType = KeyboardType.Password,
//                leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "password") },
//                modifier = Modifier
//                    .padding(horizontal = TypeSize.large_36)
//                    .fillMaxWidth()
//            )
//            Spacer(Modifier.height(TypeSize.middle_12))
//            TextInput(
//                text = "验证码",
//                label = "UCode",
//                onValueChange = {},
//                keyboardType = KeyboardType.Number,
//                leadingIcon = { Icon(imageVector = Icons.Rounded.DateRange, contentDescription = "password") },
//                modifier = Modifier
//                    .padding(horizontal = TypeSize.large_36)
//                    .fillMaxWidth()
//            )
//        }
//    }
//}
