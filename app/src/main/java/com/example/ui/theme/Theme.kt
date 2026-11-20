package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

@Immutable
data class PlayForjaExtras(
    val accentTheme: AccentTheme = AccentTheme.FORJA,
    val bgDeep: Color = ObsidianBg,
    val cardBg: Color = CarbonElevated,
    val cardHighest: Color = CarbonHighest,
    val borderSubtle: Color = CarbonBorder,
    val neonPrimary: Color = ForgeEmber,
    val neonSecondary: Color = CyberCyan,
    val neonGlow: Color = ForgeEmberGlow,
    val onlineGreen: Color = VenomLime,
    val gold: Color = ChampionGold,
    val alert: Color = CrimsonAlert
)

val LocalPlayForjaExtras = staticCompositionLocalOf { PlayForjaExtras() }

private val PlayForjaShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun PlayForjaTheme(
    accentTheme: AccentTheme = AccentTheme.FORJA,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = accentTheme.primary,
        onPrimary = Color(0xFF080A0F),
        primaryContainer = accentTheme.container,
        onPrimaryContainer = accentTheme.glow,
        secondary = accentTheme.secondary,
        onSecondary = Color(0xFF080A0F),
        secondaryContainer = CarbonHighest,
        onSecondaryContainer = TextPrimary,
        tertiary = PlasmaViolet,
        onTertiary = Color.White,
        background = ObsidianBg,
        onBackground = TextPrimary,
        surface = CarbonSurface,
        onSurface = TextPrimary,
        surfaceVariant = CarbonElevated,
        onSurfaceVariant = TextSecondary,
        surfaceContainerLowest = ObsidianBg,
        surfaceContainerLow = CarbonSurface,
        surfaceContainer = CarbonElevated,
        surfaceContainerHigh = CarbonHighest,
        surfaceContainerHighest = Color(0xFF252F45),
        outline = CarbonBorder,
        outlineVariant = Color(0xFF1D2536),
        error = CrimsonAlert,
        onError = Color.White
    )

    val extras = PlayForjaExtras(
        accentTheme = accentTheme,
        bgDeep = ObsidianBg,
        cardBg = CarbonElevated,
        cardHighest = CarbonHighest,
        borderSubtle = CarbonBorder,
        neonPrimary = accentTheme.primary,
        neonSecondary = accentTheme.secondary,
        neonGlow = accentTheme.glow,
        onlineGreen = VenomLime,
        gold = ChampionGold,
        alert = CrimsonAlert
    )

    CompositionLocalProvider(LocalPlayForjaExtras provides extras) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PlayForjaTypography,
            shapes = PlayForjaShapes,
            content = content
        )
    }
}
