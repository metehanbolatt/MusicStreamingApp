package com.metehanbolat.musicstreamingapp.ui.theme

import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val dialogColor = Color(0xFF5F5F5F)

sealed class ThemeColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val text: Color,
) {
    object Dark : ThemeColors(
        background = Color(0xFF000000),
        surface = Color(0xFF000000),
        primary = Color(0xFF4FB64C),
        text = Color.White
    )
}