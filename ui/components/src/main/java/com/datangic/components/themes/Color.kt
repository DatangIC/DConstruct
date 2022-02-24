package com.datangic.components.themes

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


val LocalColorSystem = staticCompositionLocalOf {
    ColorSystem(
        color = Color.Unspecified,
        gradient = emptyList()
    )
}

@Immutable
data class ColorSystem(
    val color: Color,
    val gradient: List<Color>
    /* ... */
)

val Purple200 = Color(0xFFCE93D8)
val Purple500 = Color(0xFF9C27B0)
val Purple700 = Color(0xFF7B1FA2)
val Teal200 = Color(0xFF03DAC5)
val vols=Color(0xFF018786)
val Blue50 = Color(0xFFE3F2FD)
val Blue100 = Color(0xFFBBDEFB)
val Blue200 = Color(0xFF90CAF9)
val Blue300 = Color(0xFF64B5F6)
val Blue400 = Color(0xFF42A5F5)
val Blue500 = Color(0xFF2196F3)
val Blue600 = Color(0xFF1E88E5)
val Blue700 = Color(0xFF1976D2)
val Blue800 = Color(0xFF1565C0)
val Blue900 = Color(0xFF0D47A1)
val BlueA100 = Color(0xFF1E88E5)
val BlueA200 = Color(0xFF82B1FF)
val BlueA300 = Color(0xFF448AFF)
val BlueA400 = Color(0xFF2979FF)
val BlueA700 = Color(0xFF2962FF)

val Red = Color(0xFF111111)

val Black = Color(0xFF000000)
val Grey900 = Color(0xFF212121)
val Grey850 = Color(0xFF303030)
val Grey800 = Color(0xFF424242)

val Grey300 = Color(0xFFE0E0E0)
val Grey100 = Color(0xFFF5F5F5)
val Grey50 = Color(0xFFFAFAFA)
val White = Color(0xFFFFFFFF)


/**
 * Dark Text
 */
val DarkPrimaryText = White
val DarkSecondaryText = Color(0xC5FFFFFF) // 70%
val DarkHintText = Color(0x7FFFFFFF) // 50%
val DarkDivider = Color(0x1FFFFFFF) // 12%

/**
 * Dark Text
 */
val LightPrimaryText = Color(0xDE000000) // 87%
val LightSecondaryText = Color(0x84000000) // 54%
val LightHintText = Color(0x61000000) // 38%
val LightDivider = Color(0x1F000000) // 12%





