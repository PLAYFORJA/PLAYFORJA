package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Core Obsidian & Carbon Surfaces
val ObsidianBg = Color(0xFF07090E)
val CarbonSurface = Color(0xFF0E121B)
val CarbonElevated = Color(0xFF151B28)
val CarbonHighest = Color(0xFF1E2638)
val CarbonBorder = Color(0xFF28334A)

// Text Hierarchy
val TextPrimary = Color(0xFFF3F6FC)
val TextSecondary = Color(0xFFA0AECB)
val TextMuted = Color(0xFF6B7999)

// PlayForja Signature Neon Palette
val ForgeEmber = Color(0xFFFF5522)
val ForgeEmberGlow = Color(0xFFFF8844)
val ForgeEmberDeep = Color(0xFF3D1408)

val CyberCyan = Color(0xFF00E5FF)
val CyberCyanGlow = Color(0xFF66F2FF)
val CyberCyanDeep = Color(0xFF062C36)

val PlasmaViolet = Color(0xFFA855F7)
val PlasmaVioletGlow = Color(0xFFC084FC)
val PlasmaVioletDeep = Color(0xFF2B1247)

val VenomLime = Color(0xFF39FF14)
val VenomLimeGlow = Color(0xFF7BFF61)
val VenomLimeDeep = Color(0xFF0D3306)

val ChampionGold = Color(0xFFFFB800)
val ChampionGoldGlow = Color(0xFFFFD454)
val ChampionGoldDeep = Color(0xFF382702)

val CrimsonAlert = Color(0xFFFF3355)

enum class AccentTheme(
    val key: String,
    val label: String,
    val primary: Color,
    val secondary: Color,
    val container: Color,
    val glow: Color
) {
    FORJA("FORJA", "Forja Magma", ForgeEmber, CyberCyan, ForgeEmberDeep, ForgeEmberGlow),
    CIBER("CIBER", "Ciber Neón", CyberCyan, ForgeEmber, CyberCyanDeep, CyberCyanGlow),
    PLASMA("PLASMA", "Plasma Void", PlasmaViolet, CyberCyan, PlasmaVioletDeep, PlasmaVioletGlow),
    VENOM("VENOM", "Venom Tóxico", VenomLime, CyberCyan, VenomLimeDeep, VenomLimeGlow),
    ORO("ORO", "Campeón Oro", ChampionGold, ForgeEmber, ChampionGoldDeep, ChampionGoldGlow);

    companion object {
        fun fromKey(key: String?): AccentTheme =
            entries.find { it.key.equals(key, ignoreCase = true) } ?: FORJA
    }
}
