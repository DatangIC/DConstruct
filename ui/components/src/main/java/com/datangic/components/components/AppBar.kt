package com.datangic.components.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppBar(
    title: String,
    hasBack: Boolean = false,
    backgroundColor: Color? = null
) {
    TopAppBar(
        navigationIcon = if (hasBack) {
            return Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        } else {
            null
        },
        title = {
            Text(text = title)
        },
        backgroundColor = backgroundColor ?: MaterialTheme.colors.primary
    )
}
