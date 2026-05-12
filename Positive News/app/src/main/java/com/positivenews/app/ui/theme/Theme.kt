package com.positivenews.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightFallback = lightColorScheme(
    primary = Color(0xFF1F6E43),
    onPrimary = Color.White,
    secondary = Color(0xFFE8A04A),
    background = Color(0xFFFBFCF7),
    surface = Color(0xFFFFFFFF),
)

private val DarkFallback = darkColorScheme(
    primary = Color(0xFF8FD3A8),
    onPrimary = Color(0xFF003820),
    secondary = Color(0xFFFFC07A),
    background = Color(0xFF0A0F0C),  // near-black for AMOLED
    surface = Color(0xFF111814),
)

@Composable
fun PositiveNewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkFallback
        else -> LightFallback
    }
    MaterialTheme(colorScheme = colors, typography = AppTypography, content = content)
}
