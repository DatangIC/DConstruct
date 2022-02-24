package com.datangic.components.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController


/**
 * colorPrimary：您的应用的主要品牌颜色，主要用于主题
 * colorPrimaryVariant：您的主要品牌颜色的较浅/较暗变体，在主题中很少使用
 * colorOnPrimary：用于显示在原色上方的元素的颜色（例如，文本和图标，根据可访问性，通常为白色或半透明的黑色）
 * colorSecondary：您应用程式的次要品牌色彩，主要用于强调某些需要突出的小部件
 * colorSecondaryVariant：您的次要品牌颜色的较浅/较深变体，在主题中很少使用
 * colorOnSecondary：用于显示在辅助颜色顶部的元素的颜色
 * colorError：用于错误的颜色（通常为红色阴影）
 * colorOnError：用于显示在错误颜色顶部的元素的颜色
 * colorSurface：用于表面的颜色（即材料“纸张”）
 * colorOnSurface：用于显示在表面颜色顶部的元素的颜色
 * android:colorBackground：所有其他屏幕内容后面的颜色
 * colorOnBackground：用于显示在背景色上方的元素的颜色
 **/
private val DarkColorPalette = darkColors(
    primary = Blue400,
    primaryVariant = Blue500,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    onPrimary = Blue400,
    primary = Blue100,
    primaryVariant = Blue400,
    secondary = Teal200,
    onSurface = Blue400,
)

@Composable
fun DConstructTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    systemUiController.setSystemBarsColor(
        color = colors.primary
    )
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun LoginTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    systemUiController.setSystemBarsColor(
        color = colors.background
    )
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}