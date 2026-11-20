package com.example.data.local

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class GameGenre(val label: String) {
    ALL("Todos"),
    FPS("FPS / Táctico"),
    MOBA("MOBA / Estrategia"),
    RPG("RPG / Aventura"),
    BATTLE_ROYALE("Battle Royale"),
    SPORTS_FIGHT("Deportes / Lucha"),
    SANDBOX_INDIE("Sandbox / Indie")
}

data class GameInfo(
    val id: String,
    val title: String,
    val genre: GameGenre,
    val primaryColor: Color,
    val secondaryColor: Color,
    val bgDark: Color,
    val badgeCode: String
)

object GameCatalog {
    val allGames: List<GameInfo> = listOf(
        GameInfo("valorant", "Valorant", GameGenre.FPS, Color(0xFFFF4655), Color(0xFFECE8E1), Color(0xFF0F1923), "VAL"),
        GameInfo("lol", "League of Legends", GameGenre.MOBA, Color(0xFFC89B3C), Color(0xFF0AC8B9), Color(0xFF091428), "LOL"),
        GameInfo("cs2", "Counter-Strike 2", GameGenre.FPS, Color(0xFFDE9B35), Color(0xFF5D79AE), Color(0xFF1B2838), "CS2"),
        GameInfo("fortnite", "Fortnite", GameGenre.BATTLE_ROYALE, Color(0xFF00D1FF), Color(0xFF9D4EDD), Color(0xFF141838), "FN"),
        GameInfo("minecraft", "Minecraft", GameGenre.SANDBOX_INDIE, Color(0xFF55A630), Color(0xFF8B5E34), Color(0xFF1A2E14), "MC"),
        GameInfo("elden_ring", "Elden Ring", GameGenre.RPG, Color(0xFFD4AF37), Color(0xFFF3E5AB), Color(0xFF17140D), "ER"),
        GameInfo("apex", "Apex Legends", GameGenre.BATTLE_ROYALE, Color(0xFFDA292A), Color(0xFFFFFFFF), Color(0xFF1A1012), "APEX"),
        GameInfo("warzone", "Call of Duty: Warzone", GameGenre.BATTLE_ROYALE, Color(0xFFA3E635), Color(0xFFE4E4E7), Color(0xFF18181B), "WZ"),
        GameInfo("rocket_league", "Rocket League", GameGenre.SPORTS_FIGHT, Color(0xFF0070F3), Color(0xFFFF6B00), Color(0xFF0A192F), "RL"),
        GameInfo("gtav", "Grand Theft Auto V", GameGenre.RPG, Color(0xFF52B788), Color(0xFFFFFFFF), Color(0xFF0F2117), "GTAV"),
        GameInfo("eafc25", "EA Sports FC 25", GameGenre.SPORTS_FIGHT, Color(0xFF39FF14), Color(0xFFFFFFFF), Color(0xFF0B1C0E), "FC25"),
        GameInfo("hollow_knight", "Hollow Knight", GameGenre.SANDBOX_INDIE, Color(0xFFE2E8F0), Color(0xFF818CF8), Color(0xFF14162E), "HK"),
        GameInfo("genshin", "Genshin Impact", GameGenre.RPG, Color(0xFFF5D061), Color(0xFF5EEAD4), Color(0xFF1B223C), "GI"),
        GameInfo("free_fire", "Free Fire", GameGenre.BATTLE_ROYALE, Color(0xFFFF7A00), Color(0xFFFFD166), Color(0xFF231308), "FF"),
        GameInfo("brawl_stars", "Brawl Stars", GameGenre.MOBA, Color(0xFFFFCC00), Color(0xFFFF3366), Color(0xFF1E1B4B), "BS"),
        GameInfo("overwatch2", "Overwatch 2", GameGenre.FPS, Color(0xFFFA9C1E), Color(0xFFFFFFFF), Color(0xFF1E2430), "OW2"),
        GameInfo("cyberpunk", "Cyberpunk 2077", GameGenre.RPG, Color(0xFFFCEE09), Color(0xFF00F0FF), Color(0xFF1A1806), "CP77"),
        GameInfo("dota2", "Dota 2", GameGenre.MOBA, Color(0xFFC23C2A), Color(0xFFFF7A59), Color(0xFF220C0A), "DOTA"),
        GameInfo("sf6", "Street Fighter 6", GameGenre.SPORTS_FIGHT, Color(0xFFEC4899), Color(0xFF22D3EE), Color(0xFF220E24), "SF6"),
        GameInfo("roblox", "Roblox", GameGenre.SANDBOX_INDIE, Color(0xFFE2231A), Color(0xFFFFFFFF), Color(0xFF1F1F23), "RBX"),
        GameInfo("pubg", "PUBG", GameGenre.BATTLE_ROYALE, Color(0xFFF2A900), Color(0xFFE5E7EB), Color(0xFF1C1912), "PUBG"),
        GameInfo("smash", "Super Smash Bros.", GameGenre.SPORTS_FIGHT, Color(0xFFEF4444), Color(0xFFFFFFFF), Color(0xFF240F14), "SSB"),
        GameInfo("mhwilds", "Monster Hunter Wilds", GameGenre.RPG, Color(0xFF10B981), Color(0xFFFBBF24), Color(0xFF0E241C), "MHW"),
        GameInfo("dbd", "Dead by Daylight", GameGenre.SANDBOX_INDIE, Color(0xFFDC2626), Color(0xFFE5E7EB), Color(0xFF1A0B0B), "DBD")
    )

    fun findByTitleOrId(query: String): GameInfo? {
        val clean = query.trim()
        return allGames.find {
            it.id.equals(clean, ignoreCase = true) || it.title.equals(clean, ignoreCase = true)
        }
    }

    fun parseSelectedGames(csv: String, primaryFallback: String): List<GameInfo> {
        val fromCsv = csv.split(",")
            .mapNotNull { findByTitleOrId(it) }
            .distinctBy { it.id }
        if (fromCsv.isNotEmpty()) return fromCsv
        val fallback = findByTitleOrId(primaryFallback)
        return if (fallback != null) listOf(fallback) else emptyList()
    }
}

@Composable
fun GameLogoBadge(
    game: GameInfo,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(11.dp),
        color = game.bgDark,
        border = BorderStroke(1.3.dp, game.primaryColor.copy(alpha = 0.85f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(game.primaryColor.copy(alpha = 0.22f), game.bgDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(5.dp)) {
                val w = this.size.width
                val h = this.size.height
                val cx = w / 2f
                val cy = h / 2f

                when (game.id) {
                    "valorant" -> {
                        val leftWing = Path().apply {
                            moveTo(w * 0.12f, h * 0.22f)
                            lineTo(w * 0.50f, h * 0.72f)
                            lineTo(w * 0.34f, h * 0.78f)
                            lineTo(w * 0.12f, h * 0.48f)
                            close()
                        }
                        val rightWing = Path().apply {
                            moveTo(w * 0.88f, h * 0.22f)
                            lineTo(w * 0.88f, h * 0.48f)
                            lineTo(w * 0.66f, h * 0.78f)
                            lineTo(w * 0.56f, h * 0.62f)
                            close()
                        }
                        drawPath(leftWing, color = game.primaryColor)
                        drawPath(rightWing, color = game.primaryColor)
                    }
                    "lol" -> {
                        drawCircle(
                            color = game.primaryColor,
                            radius = w * 0.42f,
                            style = Stroke(width = w * 0.08f)
                        )
                        val lPath = Path().apply {
                            moveTo(w * 0.36f, h * 0.22f)
                            lineTo(w * 0.48f, h * 0.22f)
                            lineTo(w * 0.48f, h * 0.66f)
                            lineTo(w * 0.70f, h * 0.66f)
                            lineTo(w * 0.70f, h * 0.78f)
                            lineTo(w * 0.36f, h * 0.78f)
                            close()
                        }
                        drawPath(lPath, color = game.primaryColor)
                    }
                    "minecraft" -> {
                        // Iconic Creeper pixel face
                        drawRoundRect(
                            color = game.primaryColor,
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                        val eyeSize = w * 0.22f
                        drawRect(Color(0xFF0A1408), Offset(w * 0.18f, h * 0.20f), Size(eyeSize, eyeSize))
                        drawRect(Color(0xFF0A1408), Offset(w * 0.60f, h * 0.20f), Size(eyeSize, eyeSize))
                        drawRect(Color(0xFF0A1408), Offset(w * 0.38f, h * 0.44f), Size(w * 0.24f, h * 0.22f))
                        drawRect(Color(0xFF0A1408), Offset(w * 0.26f, h * 0.58f), Size(w * 0.14f, h * 0.24f))
                        drawRect(Color(0xFF0A1408), Offset(w * 0.60f, h * 0.58f), Size(w * 0.14f, h * 0.24f))
                    }
                    "elden_ring" -> {
                        // Intertwining Great Rune rings
                        drawCircle(game.primaryColor, radius = w * 0.28f, center = Offset(cx, cy - h * 0.10f), style = Stroke(w * 0.06f))
                        drawCircle(game.primaryColor, radius = w * 0.24f, center = Offset(cx - w * 0.13f, cy + h * 0.12f), style = Stroke(w * 0.06f))
                        drawCircle(game.primaryColor, radius = w * 0.24f, center = Offset(cx + w * 0.13f, cy + h * 0.12f), style = Stroke(w * 0.06f))
                        drawLine(game.secondaryColor, Offset(cx, h * 0.08f), Offset(cx, h * 0.92f), strokeWidth = w * 0.06f)
                    }
                    "apex" -> {
                        // Apex Predator A-chevron
                        val chevron = Path().apply {
                            moveTo(cx, h * 0.10f)
                            lineTo(w * 0.88f, h * 0.86f)
                            lineTo(w * 0.68f, h * 0.86f)
                            lineTo(cx, h * 0.42f)
                            lineTo(w * 0.32f, h * 0.86f)
                            lineTo(w * 0.12f, h * 0.86f)
                            close()
                        }
                        drawPath(chevron, color = game.primaryColor)
                    }
                    "overwatch2" -> {
                        drawArc(
                            color = game.primaryColor,
                            startAngle = 155f,
                            sweepAngle = 230f,
                            useCenter = false,
                            style = Stroke(width = w * 0.12f, cap = StrokeCap.Round)
                        )
                        drawLine(game.secondaryColor, Offset(w * 0.28f, h * 0.68f), Offset(cx, h * 0.36f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
                        drawLine(game.secondaryColor, Offset(w * 0.72f, h * 0.68f), Offset(cx, h * 0.36f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
                    }
                    "smash" -> {
                        drawCircle(color = game.primaryColor, radius = w * 0.42f)
                        drawLine(game.bgDark, Offset(w * 0.34f, h * 0.05f), Offset(w * 0.34f, h * 0.95f), strokeWidth = w * 0.13f)
                        drawLine(game.bgDark, Offset(w * 0.05f, h * 0.64f), Offset(w * 0.95f, h * 0.64f), strokeWidth = w * 0.09f)
                    }
                    "genshin" -> {
                        // 4-point primogem star
                        val star = Path().apply {
                            moveTo(cx, h * 0.06f)
                            quadraticTo(cx, cy, w * 0.94f, cy)
                            quadraticTo(cx, cy, cx, h * 0.94f)
                            quadraticTo(cx, cy, w * 0.06f, cy)
                            quadraticTo(cx, cy, cx, h * 0.06f)
                            close()
                        }
                        drawPath(star, brush = Brush.linearGradient(listOf(game.primaryColor, game.secondaryColor)))
                    }
                    "hollow_knight" -> {
                        // Pale Vessel horned mask
                        val mask = Path().apply {
                            moveTo(w * 0.28f, h * 0.12f)
                            quadraticTo(w * 0.24f, h * 0.38f, w * 0.26f, h * 0.56f)
                            quadraticTo(cx, h * 0.92f, w * 0.74f, h * 0.56f)
                            quadraticTo(w * 0.76f, h * 0.38f, w * 0.72f, h * 0.12f)
                            quadraticTo(w * 0.60f, h * 0.30f, cx, h * 0.34f)
                            quadraticTo(w * 0.40f, h * 0.30f, w * 0.28f, h * 0.12f)
                            close()
                        }
                        drawPath(mask, color = game.primaryColor)
                        drawOval(game.bgDark, Offset(w * 0.33f, h * 0.48f), Size(w * 0.12f, h * 0.16f))
                        drawOval(game.bgDark, Offset(w * 0.55f, h * 0.48f), Size(w * 0.12f, h * 0.16f))
                    }
                    "dota2" -> {
                        drawRoundRect(color = game.primaryColor, cornerRadius = CornerRadius(6f, 6f))
                        drawLine(game.bgDark, Offset(w * 0.15f, h * 0.15f), Offset(w * 0.85f, h * 0.85f), strokeWidth = w * 0.16f, cap = StrokeCap.Square)
                        drawRect(game.secondaryColor, Offset(w * 0.16f, h * 0.66f), Size(w * 0.18f, h * 0.18f))
                        drawRect(game.secondaryColor, Offset(w * 0.66f, h * 0.16f), Size(w * 0.18f, h * 0.18f))
                    }
                    "dbd" -> {
                        // 4 slash marks + diagonal 5th
                        val gap = w * 0.17f
                        for (i in 0..3) {
                            val x = w * 0.22f + i * gap
                            drawLine(game.primaryColor, Offset(x, h * 0.18f), Offset(x - w * 0.05f, h * 0.82f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
                        }
                        drawLine(game.secondaryColor, Offset(w * 0.12f, h * 0.68f), Offset(w * 0.88f, h * 0.32f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
                    }
                    "rocket_league" -> {
                        val shield = Path().apply {
                            moveTo(w * 0.18f, h * 0.14f)
                            lineTo(w * 0.82f, h * 0.14f)
                            lineTo(w * 0.82f, h * 0.58f)
                            lineTo(cx, h * 0.88f)
                            lineTo(w * 0.18f, h * 0.58f)
                            close()
                        }
                        drawPath(shield, brush = Brush.linearGradient(listOf(game.primaryColor, game.secondaryColor)), style = Stroke(w * 0.09f))
                        drawCircle(game.secondaryColor, radius = w * 0.15f, center = Offset(cx, cy))
                    }
                    "roblox" -> {
                        val rectPath = Path().apply {
                            moveTo(w * 0.26f, h * 0.10f)
                            lineTo(w * 0.90f, h * 0.26f)
                            lineTo(w * 0.74f, h * 0.90f)
                            lineTo(w * 0.10f, h * 0.74f)
                            close()
                        }
                        drawPath(rectPath, color = game.primaryColor)
                        drawRect(game.bgDark, Offset(w * 0.41f, h * 0.41f), Size(w * 0.18f, h * 0.18f))
                    }
                    else -> {
                        // Distinctive emblem frame + bold monogram
                        drawRoundRect(
                            brush = Brush.linearGradient(listOf(game.primaryColor, game.secondaryColor)),
                            cornerRadius = CornerRadius(8f, 8f),
                            style = Stroke(width = w * 0.07f)
                        )
                    }
                }
            }

            // For monogram-style emblems (Fortnite, CS2, Warzone, GTA V, FC 25, Free Fire, Brawl, Cyberpunk, SF6, PUBG, MHW)
            val showMonogram = game.id in setOf(
                "fortnite", "cs2", "warzone", "gtav", "eafc25",
                "free_fire", "brawl_stars", "cyberpunk", "sf6", "pubg", "mhwilds"
            )
            if (showMonogram) {
                Text(
                    text = game.badgeCode,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (game.badgeCode.length >= 4) (size.value * 0.22f).sp else (size.value * 0.27f).sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = game.primaryColor
                )
            }
        }
    }
}
