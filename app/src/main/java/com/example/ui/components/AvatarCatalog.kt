package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class AvatarPreset(
    val key: String,
    val label: String,
    val bgStart: Color,
    val bgEnd: Color,
    val accent: Color,
    val icon: ImageVector? = null
)

data class CustomAvatarColor(
    val key: String,
    val label: String,
    val bgStart: Color,
    val bgEnd: Color,
    val accent: Color
)

data class CustomAvatarEmblem(
    val key: String,
    val label: String,
    val icon: ImageVector
)

object AvatarCatalog {
    val presets: List<AvatarPreset> = listOf(
        AvatarPreset("preset_knight", "Caballero Forja", Color(0xFF1F100B), Color(0xFF0A0D14), Color(0xFFFF5522)),
        AvatarPreset("preset_cyber", "Visor Ciber", Color(0xFF06283D), Color(0xFF0B132B), Color(0xFF00E5FF), Icons.Filled.SmartToy),
        AvatarPreset("preset_plasma", "Mago Plasma", Color(0xFF2E1065), Color(0xFF1E1B4B), Color(0xFFC084FC), Icons.Filled.AutoAwesome),
        AvatarPreset("preset_venom", "Bio Hacker", Color(0xFF052E16), Color(0xFF091510), Color(0xFF39FF14), Icons.Filled.Terminal),
        AvatarPreset("preset_gold", "Rey Dorado", Color(0xFF451A03), Color(0xFF1C1917), Color(0xFFFFB800), Icons.Filled.MilitaryTech),
        AvatarPreset("preset_forge", "Llama Titan", Color(0xFF431407), Color(0xFF111827), Color(0xFFFF6B35), Icons.Filled.LocalFireDepartment),
        AvatarPreset("preset_wolf", "Lobo Alpha", Color(0xFF1E293B), Color(0xFF090D16), Color(0xFF38BDF8), Icons.Filled.Pets),
        AvatarPreset("preset_rocket", "Piloto Turbo", Color(0xFF0C4A6E), Color(0xFF1E1B4B), Color(0xFF22D3EE), Icons.Filled.RocketLaunch),
        AvatarPreset("preset_samurai", "Samurái Void", Color(0xFF3B0764), Color(0xFF09090B), Color(0xFFF472B6), Icons.Filled.Shield),
        AvatarPreset("preset_diamond", "Rango Diamante", Color(0xFF0F172A), Color(0xFF164E63), Color(0xFF67E8F9), Icons.Filled.Diamond),
        AvatarPreset("preset_mecha", "Mecha 01", Color(0xFF31102F), Color(0xFF111827), Color(0xFFFB7185), Icons.Filled.Engineering),
        AvatarPreset("preset_storm", "Voltaje X", Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFFFACC15), Icons.Filled.Bolt),
        AvatarPreset("preset_glitch", "Glitch Zero", Color(0xFF18181B), Color(0xFF042F2E), Color(0xFF2DD4BF), Icons.Filled.BugReport),
        AvatarPreset("preset_arcade", "Pro Arcade", Color(0xFF3F1D2B), Color(0xFF0F172A), Color(0xFFFF4D6D), Icons.Filled.SportsEsports),
        AvatarPreset("preset_psy", "Mente Táctica", Color(0xFF172554), Color(0xFF2E1065), Color(0xFFA78BFA), Icons.Filled.Psychology),
        AvatarPreset("preset_inferno", "Fénix Solar", Color(0xFF450A0A), Color(0xFF1F100B), Color(0xFFFB923C), Icons.Filled.Whatshot),
        AvatarPreset("preset_guardian", "Guardián", Color(0xFF064E3B), Color(0xFF0F172A), Color(0xFF34D399), Icons.Filled.Security),
        AvatarPreset("preset_nova", "Estrella Nova", Color(0xFF4A044E), Color(0xFF172554), Color(0xFFFDE047), Icons.Filled.Stars)
    )

    val customColors: List<CustomAvatarColor> = listOf(
        CustomAvatarColor("ember", "Magma", Color(0xFF431407), Color(0xFF0F0A0A), Color(0xFFFF5522)),
        CustomAvatarColor("cyan", "Ciber", Color(0xFF083344), Color(0xFF090D16), Color(0xFF00E5FF)),
        CustomAvatarColor("plasma", "Plasma", Color(0xFF3B0764), Color(0xFF0F0B1E), Color(0xFFA855F7)),
        CustomAvatarColor("venom", "Venom", Color(0xFF052E16), Color(0xFF07130C), Color(0xFF39FF14)),
        CustomAvatarColor("gold", "Oro", Color(0xFF451A03), Color(0xFF171207), Color(0xFFFFB800)),
        CustomAvatarColor("crimson", "Carmesí", Color(0xFF4C0519), Color(0xFF14070B), Color(0xFFFF3355))
    )

    val customEmblems: List<CustomAvatarEmblem> = listOf(
        CustomAvatarEmblem("gamepad", "Mando", Icons.Filled.SportsEsports),
        CustomAvatarEmblem("fire", "Llama", Icons.Filled.LocalFireDepartment),
        CustomAvatarEmblem("bolt", "Rayo", Icons.Filled.Bolt),
        CustomAvatarEmblem("shield", "Escudo", Icons.Filled.Shield),
        CustomAvatarEmblem("robot", "Mecha", Icons.Filled.SmartToy),
        CustomAvatarEmblem("wolf", "Bestia", Icons.Filled.Pets),
        CustomAvatarEmblem("rocket", "Cohete", Icons.Filled.RocketLaunch),
        CustomAvatarEmblem("diamond", "Diamante", Icons.Filled.Diamond),
        CustomAvatarEmblem("terminal", "Hacker", Icons.Filled.Terminal),
        CustomAvatarEmblem("star", "Estrella", Icons.Filled.Stars)
    )

    fun buildCustomKey(emblemKey: String, colorKey: String): String = "custom:$emblemKey:$colorKey"

    fun findPreset(key: String): AvatarPreset? = presets.find { it.key == key }
}

@Composable
fun IllustratedAvatarContent(
    avatarKey: String,
    modifier: Modifier = Modifier
) {
    val preset = AvatarCatalog.findPreset(avatarKey)
    if (avatarKey.startsWith("custom:")) {
        val parts = avatarKey.split(":")
        val emblemKey = parts.getOrNull(1) ?: "gamepad"
        val colorKey = parts.getOrNull(2) ?: "ember"
        val colorDef = AvatarCatalog.customColors.find { it.key == colorKey } ?: AvatarCatalog.customColors.first()
        val emblemDef = AvatarCatalog.customEmblems.find { it.key == emblemKey } ?: AvatarCatalog.customEmblems.first()
        AvatarGlyphLayer(colorDef.bgStart, colorDef.bgEnd, colorDef.accent, emblemDef.icon, modifier)
    } else if (preset?.icon != null) {
        AvatarGlyphLayer(preset.bgStart, preset.bgEnd, preset.accent, preset.icon, modifier)
    } else {
        val fallback = preset ?: AvatarCatalog.presets.first()
        AvatarGlyphLayer(fallback.bgStart, fallback.bgEnd, fallback.accent, Icons.Filled.SportsEsports, modifier)
    }
}

@Composable
private fun AvatarGlyphLayer(
    bgStart: Color,
    bgEnd: Color,
    accent: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(bgStart, bgEnd))),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            // Subtle cyber hexagon halo
            val hex = Path().apply {
                val r = w * 0.42f
                for (i in 0..5) {
                    val angle = Math.toRadians((60 * i - 30).toDouble())
                    val px = cx + (r * kotlin.math.cos(angle)).toFloat()
                    val py = cy + (r * kotlin.math.sin(angle)).toFloat()
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            drawPath(hex, color = accent.copy(alpha = 0.25f), style = Stroke(width = 2.dp.toPx()))
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(accent.copy(alpha = 0.28f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = w * 0.42f
                )
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier
                .fillMaxSize(0.56f)
                .padding(2.dp)
        )
    }
}
