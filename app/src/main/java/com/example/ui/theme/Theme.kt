package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = MatteGold,
  onPrimary = Color(0xFF2D2200),
  primaryContainer = IslamicGreenDark,
  onPrimaryContainer = MatteGoldLight,
  secondary = IslamicGreenLight,
  onSecondary = Color.White,
  secondaryContainer = CardDark,
  onSecondaryContainer = IslamicGreenContainer,
  tertiary = MatteGoldLight,
  onTertiary = Color(0xFF382C00),
  background = BackgroundDark,
  onBackground = TextPrimaryDark,
  surface = SurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = CardDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = CardBorderDark,
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
  primary = IslamicGreen,
  onPrimary = Color.White,
  primaryContainer = IslamicGreenContainer,
  onPrimaryContainer = OnIslamicGreenContainer,
  secondary = MatteGoldDark,
  onSecondary = Color.White,
  secondaryContainer = MatteGoldContainer,
  onSecondaryContainer = OnMatteGoldContainer,
  tertiary = MatteGold,
  onTertiary = Color(0xFF2D2200),
  background = BackgroundLight,
  onBackground = TextPrimaryLight,
  surface = SurfaceLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = Color(0xFFEFF5F1),
  onSurfaceVariant = TextSecondaryLight,
  outline = CardBorderLight,
  error = ErrorRed,
  onError = Color.White
)

@Composable
fun MasjidManagerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
