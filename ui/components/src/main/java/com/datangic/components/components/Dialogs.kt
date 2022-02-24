package com.datangic.components.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.datangic.components.data.TipsDialogState
import com.datangic.components.themes.TypeSize
import com.datangic.themes.R

@Composable
fun TipsDialog(
    dialogState: TipsDialogState,
    onDismiss: (() -> Unit) = {},
    buttons: (@Composable () -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
            dialogState.close()
        },
        buttons = {
            buttons?.let {
                buttons
            }
            if (buttons == null) {
                TextButton(
                    onClick = {
                        onDismiss()
                        dialogState.close()
                    },
                    Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = TypeSize.size_6)
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(id = dialogState.title.value),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = stringResource(id = dialogState.text.value),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        shape = RoundedCornerShape(0),
        modifier = modifier
            .fillMaxWidth()
    )
}

@Preview
@Composable
fun TipDialogView() {
    TipsDialog(TipsDialogState())
}
