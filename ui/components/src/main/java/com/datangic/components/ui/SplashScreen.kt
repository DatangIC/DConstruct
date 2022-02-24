package com.datangic.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.datangic.components.components.ImagePagerItem
import com.datangic.components.themes.TypeSize
import com.datangic.themes.R
import com.google.accompanist.pager.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SplashNavigation(
    data: List<Any>,
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
    margin: Dp = TypeSize.large_128,
    pagerItem: @Composable PagerScope.(page: Int, modifier: Modifier) -> Unit = { page, modifier ->
        ImagePagerItem(
            data[page],
            modifier = modifier
        )
    },
    onStart: () -> Unit
) {
    rememberSystemUiController().isStatusBarVisible = false
    ConstraintLayout(
        modifier = Modifier
            .background(Brush.verticalGradient(listOf<Color>(Color.Gray, Color.Red, Color.Cyan, Color.DarkGray)))
            .padding(horizontal = horizontal, vertical = vertical)
    ) {
        val pagerState = rememberPagerState()
        val pagerIndicator = createRef()
        HorizontalPager(
            count = data.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = TypeSize.size_4),
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            pagerItem(
                page,
                Modifier.fillMaxSize()
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(pagerIndicator) {
                    bottom.linkTo(parent.bottom, margin = margin)
                }) {
            if (pagerState.pageCount - 1 == pagerState.currentPage) {
                Button(
                    onClick = { onStart() },
                    Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.start_use),
                        Modifier.padding(horizontal = TypeSize.middle_28, vertical = TypeSize.size_4)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(vertical = TypeSize.middle_10))
            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = MaterialTheme.colors.primary,
                inactiveColor = MaterialTheme.colors.background
            )
        }
    }
}