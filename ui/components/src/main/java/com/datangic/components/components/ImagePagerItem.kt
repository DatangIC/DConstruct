package com.datangic.components.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.datangic.components.rememberRandomSampleImageUrl
import com.datangic.components.themes.TypeSize

/**
 * Simple pager item which displays an image
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun ImagePagerItem(
    data: Any,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        // Our page content, displaying a random image
        Image(
            painter = rememberImagePainter(data = data),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .clip(shape = RoundedCornerShape(TypeSize.middle_18)),
        )
    }
}

