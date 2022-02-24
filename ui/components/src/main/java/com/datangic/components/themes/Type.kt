package com.datangic.components.themes

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.datangic.themes.R

/**
 * https://fonts.google.com/specimen/Montserrat
 */
private val Montserrat = FontFamily(
    Font(R.font.montserrat_regular),
    Font(R.font.montserrat_medium, FontWeight.W500),
    Font(R.font.montserrat_semibold, FontWeight.W600)
)

/**
 * https://fonts.google.com/specimen/Domine
 */
private val Domine = FontFamily(
    Font(R.font.domine_regular),
    Font(R.font.domine_bold, FontWeight.Bold)
)

@Composable
fun AnnotatedString.withDefaultColor(alpha: Float = LocalContentAlpha.current): AnnotatedString {
    val mColor = LocalContentColor.current.copy(alpha)
    val spanStyles = mutableListOf<AnnotatedString.Range<SpanStyle>>()
    return if (this.spanStyles.isEmpty()) {
        spanStyles.add(AnnotatedString.Range(SpanStyle(mColor), 0, text.length))
        AnnotatedString(this.text, spanStyles, this.paragraphStyles)
    } else {
        var start = 0
        buildAnnotatedString {
            append(text = this@withDefaultColor)
            for ((index, span) in this@withDefaultColor.spanStyles.withIndex()) {
                if (start < span.start) {
                    addStyle(SpanStyle(mColor), start, span.start)
                    start = span.end
                }
                if (index == this@withDefaultColor.spanStyles.lastIndex && span.end < this@withDefaultColor.text.length) {
                    addStyle(SpanStyle(mColor), start, this@withDefaultColor.text.length)
                }
            }
        }
    }
}

// Set of Material typography styles to start with
val Typography = Typography(
    h4 = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W600,
        fontSize = 30.sp
    ),
    h5 = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = Domine,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = Montserrat,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp
    )
)