package com.tien.piholeconnect.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors =
    lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        errorContainer = md_theme_light_errorContainer,
        onError = md_theme_light_onError,
        onErrorContainer = md_theme_light_onErrorContainer,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        outline = md_theme_light_outline,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inverseSurface = md_theme_light_inverseSurface,
        inversePrimary = md_theme_light_inversePrimary,
        surfaceTint = md_theme_light_surfaceTint,
        outlineVariant = md_theme_light_outlineVariant,
        scrim = md_theme_light_scrim,
    )

private val DarkColors =
    darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        secondary = md_theme_dark_secondary,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        tertiary = md_theme_dark_tertiary,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        error = md_theme_dark_error,
        errorContainer = md_theme_dark_errorContainer,
        onError = md_theme_dark_onError,
        onErrorContainer = md_theme_dark_onErrorContainer,
        background = md_theme_dark_background,
        onBackground = md_theme_dark_onBackground,
        surface = md_theme_dark_surface,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        outline = md_theme_dark_outline,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inverseSurface = md_theme_dark_inverseSurface,
        inversePrimary = md_theme_dark_inversePrimary,
        surfaceTint = md_theme_dark_surfaceTint,
        outlineVariant = md_theme_dark_outlineVariant,
        scrim = md_theme_dark_scrim,
    )

val ColorScheme.isDark
    get() = this.toString() == DarkColors.toString()

@get:Composable
@get:ReadOnlyComposable
val ColorScheme.info
    get() = if (this.isDark) dark_Info else light_Info

@get:Composable
@get:ReadOnlyComposable
val ColorScheme.infoContainer
    get() = if (this.isDark) dark_InfoContainer else light_InfoContainer

@get:Composable
@get:ReadOnlyComposable
val ColorScheme.success
    get() = if (this.isDark) dark_Success else light_Success

@get:Composable
@get:ReadOnlyComposable
val ColorScheme.successContainer
    get() = if (this.isDark) dark_SuccessContainer else light_SuccessContainer

@get:Composable
@get:ReadOnlyComposable
val ColorScheme.warning
    get() = if (this.isDark) dark_Warning else light_Warning

@get:Composable
@get:ReadOnlyComposable
val ColorScheme.warningContainer
    get() = if (this.isDark) dark_WarningContainer else light_WarningContainer

@Composable
@ReadOnlyComposable
fun ColorScheme.contentColorFor(backGroundColor: Color) =
    MaterialTheme.colorScheme.contentColorFor(backGroundColor).takeIf { it != Color.Unspecified }
        ?: when (backGroundColor) {
            MaterialTheme.colorScheme.info -> if (this.isDark) dark_onInfo else light_onInfo
            MaterialTheme.colorScheme.infoContainer ->
                if (this.isDark) dark_onInfoContainer else light_onInfoContainer
            MaterialTheme.colorScheme.success ->
                if (this.isDark) dark_onSuccess else light_onSuccess
            MaterialTheme.colorScheme.successContainer ->
                if (this.isDark) dark_onSuccessContainer else light_onSuccessContainer
            MaterialTheme.colorScheme.warning ->
                if (this.isDark) dark_onWarning else light_onWarning
            MaterialTheme.colorScheme.warningContainer ->
                if (this.isDark) dark_onWarningContainer else light_onWarningContainer
            else -> Color.Unspecified
        }

@Composable
fun PiHoleConnectTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val dynamicColor = useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme =
        when {
            dynamicColor && useDarkTheme -> {
                dynamicDarkColorScheme(LocalContext.current)
            }

            dynamicColor && !useDarkTheme -> {
                dynamicLightColorScheme(LocalContext.current)
            }

            useDarkTheme -> DarkColors
            else -> LightColors
        }

    val view = LocalView.current
    LaunchedEffect(useDarkTheme) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            !useDarkTheme
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
