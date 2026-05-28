package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BluePrimary,
    primaryContainer = Color(0xFF00447C),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF2E323E),
    tertiary = OrangeTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = Color(0xFFE3E2E6),
    onSurface = Color(0xFFE3E2E6),
    outline = DarkOutline,
    onSurfaceVariant = Color(0xFFC4C6D0)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BluePrimary,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = OnBluePrimaryContainer,
    secondary = BlueSecondary,
    tertiary = OrangeTertiary,
    background = PolishBackground,
    surface = PolishSurface,
    onBackground = Color(0xFF1B1B1F),
    onSurface = Color(0xFF1B1B1F),
    outline = PolishOutline,
    onSurfaceVariant = PolishOnSurfaceVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false, // Set false to prioritize our gorgeous designed branding palette!
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = {
      androidx.compose.runtime.CompositionLocalProvider(
        content = content
      )
    }
  )
}
