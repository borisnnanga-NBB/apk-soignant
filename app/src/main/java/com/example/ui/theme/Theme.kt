package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HealerColorScheme = lightColorScheme(
    primary = DeepVegetationGreen,
    onPrimary = OnPrimaryText,
    primaryContainer = ClearPrimaryContainer,
    onPrimaryContainer = LightPrimaryContainer,
    secondary = SkyBlueSoothe,
    onSecondary = OnSecondaryText,
    secondaryContainer = SkyBlueContainer,
    tertiary = EarthClayBrown,
    onTertiary = OnTertiaryText,
    background = WarmBeigeBg,
    onBackground = CharcoalDark,
    surface = WarmBeigeBg,
    onSurface = CharcoalDark,
    surfaceVariant = WarmBeigeHigh,
    onSurfaceVariant = MutedGreenGrey,
    outline = OutlineGreenGrey,
    outlineVariant = OutlineVariantLight,
    error = ErrorRed,
    onError = OnErrorText,
    errorContainer = ErrorContainerRed
)

@Composable
fun HealerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HealerColorScheme,
        typography = Typography,
        content = content
    )
}
