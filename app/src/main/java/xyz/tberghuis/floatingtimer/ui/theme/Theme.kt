package xyz.tberghuis.floatingtimer.ui.theme

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

// Modern Dark Color Scheme - Premium app look
private val ModernDarkColorScheme = darkColorScheme(
  primary = AccentOrange,
  onPrimary = Color.White,
  primaryContainer = AccentOrange.copy(alpha = 0.2f),
  onPrimaryContainer = AccentOrangeLight,
  secondary = AccentBlue,
  onSecondary = Color.White,
  secondaryContainer = AccentBlue.copy(alpha = 0.2f),
  onSecondaryContainer = AccentCyan,
  tertiary = AccentPurple,
  onTertiary = Color.White,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = TextMuted,
  outlineVariant = DarkCard
)

private val LightColorScheme = lightColorScheme(
  primary = Purple40,
  secondary = PurpleGrey40,
  tertiary = Pink40
)

@Composable
fun FloatingTimerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Disable dynamic color for consistent branding
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> ModernDarkColorScheme
    else -> LightColorScheme
  }
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}