package com.tien.piholeconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse

private val DarkColorPalette = darkColors(
    primary = Red700Light,
    primaryVariant = Red700Dark,
    onPrimary = Color.White,
    secondary = Green400,
    secondaryVariant = Green400,
    onSecondary = Color.White,
    onError = Color.White
)

private val LightColorPalette = lightColors(
    primary = Red700,
    primaryVariant = Red700Dark,
    onPrimary = Color.White,
    secondary = Green400Dark,
    secondaryVariant = Green400Dark,
    onSecondary = Color.White,
    onError = Color.White
)

@get:Composable
@get:ReadOnlyComposable
val Colors.info: Color
    get() = if (isLight) Blue500Dark else Blue500

@get:Composable
@get:ReadOnlyComposable
val Colors.success: Color
    get() = if (isLight) Green400Dark else Green400

@get:Composable
@get:ReadOnlyComposable
val Colors.warning: Color
    get() = if (isLight) Amber500Dark else Amber500

@Composable
@ReadOnlyComposable
fun contentColorFor(backGroundColor: Color) =
    MaterialTheme.colors.contentColorFor(backGroundColor).takeOrElse {
        return when (backGroundColor) {
            MaterialTheme.colors.info -> Color.White
            MaterialTheme.colors.success -> Color.White
            MaterialTheme.colors.warning -> Color.White
            else -> LocalContentColor.current
        }
    }

@Composable
fun PiHoleConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}