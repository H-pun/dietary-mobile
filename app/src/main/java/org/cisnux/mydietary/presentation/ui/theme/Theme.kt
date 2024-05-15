package org.cisnux.mydietary.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.glance.GlanceTheme
import androidx.glance.color.ColorProvider
import androidx.glance.color.colorProviders


private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
)

val widgetColorScheme = colorProviders(
    primary = ColorProvider(day = primaryLight, night = primaryDark),
    onPrimary = ColorProvider(day = onPrimaryLight, night = onPrimaryDark),
    primaryContainer = ColorProvider(
        day = primaryContainerLight,
        night = primaryContainerDark
    ),
    onPrimaryContainer = ColorProvider(
        day = onPrimaryContainerLight,
        night = onPrimaryDark
    ),
    secondary = ColorProvider(day = secondaryLight, night = secondaryDark),
    onSecondary = ColorProvider(
        day = onSecondaryLight,
        night = onSecondaryDark
    ),
    secondaryContainer = ColorProvider(
        day = secondaryContainerLight,
        night = secondaryContainerDark
    ),
    onSecondaryContainer = ColorProvider(
        day = onSecondaryContainerLight,
        night = onSecondaryContainerDark
    ),
    tertiary = ColorProvider(day = tertiaryLight, night = tertiaryDark),
    onTertiary = ColorProvider(day = onTertiaryLight, night = onTertiaryDark),
    tertiaryContainer = ColorProvider(
        day = tertiaryContainerLight,
        night = tertiaryContainerDark
    ),
    onTertiaryContainer = ColorProvider(
        day = onTertiaryContainerLight,
        night = onTertiaryContainerDark
    ),
    error = ColorProvider(day = errorLight, night = errorDark),
    errorContainer = ColorProvider(
        day = errorContainerLight,
        night = errorContainerDark
    ),
    onError = ColorProvider(day = onErrorLight, night = onErrorDark),
    onErrorContainer = ColorProvider(
        day = onErrorContainerLight,
        night = onErrorContainerDark
    ),
    background = ColorProvider(day = backgroundLight, night = backgroundDark),
    onBackground = ColorProvider(
        day = onBackgroundLight,
        night = onBackgroundDark
    ),
    surface = ColorProvider(day = surfaceLight, night = surfaceDark),
    onSurface = ColorProvider(day = onSurfaceLight, night = onSurfaceDark),
    surfaceVariant = ColorProvider(
        day = surfaceVariantLight,
        night = surfaceVariantDark
    ),
    onSurfaceVariant = ColorProvider(
        day = onSurfaceVariantLight,
        night = onSurfaceVariantDark
    ),
    outline = ColorProvider(day = outlineLight, night = outlineDark),
    inverseOnSurface = ColorProvider(
        day = inverseOnSurfaceLight,
        night = inverseOnSurfaceDark
    ),
    inverseSurface = ColorProvider(
        day = inverseSurfaceLight,
        night = inverseSurfaceDark
    ),
    inversePrimary = ColorProvider(
        day = inversePrimaryLight,
        night = inversePrimaryDark
    ),
)

@Composable
fun DietaryGlanceTheme(
    content: @Composable () -> Unit,
) {
    GlanceTheme(colors = widgetColorScheme, content = content)
}

@Composable
fun DietaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
