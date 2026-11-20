package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.FrameStyle
import com.example.data.local.GamerStatus
import com.example.data.local.LevelSystem
import com.example.data.local.ProfileBgStyle
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ForgeEmber
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PlasmaViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VenomLime

fun frameBrush(frameKey: String): Brush {
    return when (FrameStyle.fromKey(frameKey)) {
        FrameStyle.EMBER -> Brush.linearGradient(listOf(ForgeEmber, ChampionGold, ForgeEmber))
        FrameStyle.CYAN -> Brush.linearGradient(listOf(CyberCyan, Color(0xFF3B82F6), CyberCyan))
        FrameStyle.PLASMA -> Brush.linearGradient(listOf(PlasmaViolet, CyberCyan, PlasmaViolet))
        FrameStyle.LIME -> Brush.linearGradient(listOf(VenomLime, CyberCyan, VenomLime))
        FrameStyle.GOLD -> Brush.linearGradient(listOf(ChampionGold, Color(0xFFFFF3B0), ForgeEmber))
        FrameStyle.CRIMSON -> Brush.linearGradient(listOf(CrimsonAlert, ForgeEmber, Color(0xFFFF6B9D)))
        FrameStyle.HOLO -> Brush.sweepGradient(listOf(CyberCyan, PlasmaViolet, ForgeEmber, VenomLime, CyberCyan))
        FrameStyle.VOID -> Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFF64748B), Color(0xFF94A3B8)))
    }
}

fun statusColor(statusKey: String): Color {
    return when (GamerStatus.fromKey(statusKey)) {
        GamerStatus.ONLINE -> VenomLime
        GamerStatus.INGAME -> CyberCyan
        GamerStatus.STREAMING -> PlasmaViolet
        GamerStatus.DND -> CrimsonAlert
        GamerStatus.INVISIBLE -> TextMuted
    }
}

fun platformIcon(platform: String): ImageVector {
    return when {
        platform.contains("PC", ignoreCase = true) -> Icons.Filled.Computer
        platform.contains("Móvil", ignoreCase = true) || platform.contains("Mobile", ignoreCase = true) -> Icons.Filled.PhoneAndroid
        platform.contains("Switch", ignoreCase = true) -> Icons.Filled.Gamepad
        else -> Icons.Filled.SportsEsports
    }
}

@Composable
fun ForgeAvatar(
    avatarPreset: String,
    frameStyle: String,
    displayName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    statusKey: String? = null,
    level: Int? = null,
    onClick: (() -> Unit)? = null
) {
    val ringWidth = if (size >= 64.dp) 3.dp else 2.dp
    Box(
        modifier = modifier
            .size(size)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(ObsidianBg)
                .border(ringWidth, frameBrush(frameStyle), CircleShape)
                .padding(ringWidth + 1.5.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when {
                avatarPreset.startsWith("uri:") -> {
                    val uri = avatarPreset.removePrefix("uri:")
                    AsyncImage(
                        model = uri,
                        contentDescription = "Avatar de $displayName",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                avatarPreset == "preset_knight" -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_avatar_knight),
                        contentDescription = "Avatar de $displayName",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    IllustratedAvatarContent(avatarKey = avatarPreset)
                }
            }
        }

        if (statusKey != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(if (size >= 64.dp) 18.dp else 13.dp)
                    .clip(CircleShape)
                    .background(ObsidianBg)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(statusColor(statusKey))
            )
        }

        if (level != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-2).dp, y = 2.dp),
                shape = RoundedCornerShape(6.dp),
                color = CarbonHighest,
                border = BorderStroke(1.dp, LocalPlayForjaExtras.current.neonPrimary)
            ) {
                Text(
                    text = "NV$level",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
fun ForgeBanner(
    bannerPreset: String,
    modifier: Modifier = Modifier,
    height: Dp = 150.dp
) {
    val extras = LocalPlayForjaExtras.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(ObsidianBg)
    ) {
        when {
            bannerPreset.startsWith("uri:") -> {
                AsyncImage(
                    model = bannerPreset.removePrefix("uri:"),
                    contentDescription = "Portada personalizada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            bannerPreset == "banner_cyber" -> {
                Image(
                    painter = painterResource(id = R.drawable.img_banner_cyber),
                    contentDescription = "Portada setup gamer",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            bannerPreset == "banner_arena" -> {
                Image(
                    painter = painterResource(id = R.drawable.img_post_arena),
                    contentDescription = "Portada arena esports",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            bannerPreset == "banner_matrix" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(ObsidianBg, Color(0xFF0A192F), Color(0xFF1C0D2E))
                        )
                    )
                    val step = 28.dp.toPx()
                    var x = 0f
                    while (x < size.width) {
                        drawLine(
                            color = extras.neonSecondary.copy(alpha = 0.16f),
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1f
                        )
                        x += step
                    }
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = extras.neonPrimary.copy(alpha = 0.14f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                        y += step
                    }
                }
            }
            else -> {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_forge),
                    contentDescription = "Portada PlayForja",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ObsidianBg.copy(alpha = 0.45f),
                            ObsidianBg.copy(alpha = 0.92f)
                        )
                    )
                )
        )
    }
}

@Composable
fun ProfileBackgroundLayer(
    profileBgStyle: String,
    modifier: Modifier = Modifier
) {
    if (profileBgStyle.startsWith("uri:")) {
        Box(modifier = modifier.fillMaxSize().background(ObsidianBg)) {
            AsyncImage(
                model = profileBgStyle.removePrefix("uri:"),
                contentDescription = "Fondo personalizado de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ObsidianBg.copy(alpha = 0.78f))
            )
        }
        return
    }

    val style = ProfileBgStyle.fromKey(profileBgStyle)
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        when (style) {
            ProfileBgStyle.OBSIDIAN -> {
                drawRect(ObsidianBg)
            }
            ProfileBgStyle.CYBER_GRID -> {
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color(0xFF05131F), ObsidianBg, Color(0xFF071A26))
                    )
                )
                val step = 32.dp.toPx()
                var x = 0f
                while (x < w) {
                    drawLine(CyberCyan.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                    x += step
                }
                var y = 0f
                while (y < h) {
                    drawLine(CyberCyan.copy(alpha = 0.08f), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                    y += step
                }
            }
            ProfileBgStyle.MAGMA -> {
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color(0xFF230B06), Color(0xFF100809), ObsidianBg)
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(ForgeEmber.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(w * 0.85f, h * 0.18f),
                        radius = w * 0.7f
                    )
                )
            }
            ProfileBgStyle.VOID_NEBULA -> {
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color(0xFF190A2E), Color(0xFF0A0D1D), ObsidianBg)
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(PlasmaViolet.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(w * 0.2f, h * 0.25f),
                        radius = w * 0.75f
                    )
                )
            }
            ProfileBgStyle.TOXIC_CIRCUIT -> {
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color(0xFF061E0E), ObsidianBg, Color(0xFF051510))
                    )
                )
                val step = 40.dp.toPx()
                var y = step
                while (y < h) {
                    drawLine(VenomLime.copy(alpha = 0.07f), Offset(0f, y), Offset(w, y), strokeWidth = 1.5f)
                    y += step * 2
                }
            }
            ProfileBgStyle.GOLD_ROYAL -> {
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color(0xFF211704), Color(0xFF0D0B07), ObsidianBg)
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(ChampionGold.copy(alpha = 0.16f), Color.Transparent),
                        center = Offset(w * 0.5f, h * 0.12f),
                        radius = w * 0.8f
                    )
                )
            }
        }
    }
}

@Composable
fun CyberSurfaceCard(
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    val borderBrush = if (highlighted) {
        Brush.linearGradient(listOf(extras.neonPrimary, extras.neonSecondary))
    } else {
        Brush.linearGradient(listOf(CarbonBorder, CarbonBorder.copy(alpha = 0.55f)))
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        shape = RoundedCornerShape(16.dp),
        color = CarbonElevated,
        border = BorderStroke(if (highlighted) 1.4.dp else 1.dp, borderBrush),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
fun XpProgressBar(
    xp: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    val extras = LocalPlayForjaExtras.current
    val progress by animateFloatAsState(
        targetValue = LevelSystem.progressFraction(xp),
        animationSpec = tween(500),
        label = "xp_progress"
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    tint = extras.neonPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "NIVEL $level",
                    style = MaterialTheme.typography.labelLarge,
                    color = extras.neonPrimary
                )
            }
            Text(
                text = "${LevelSystem.xpIntoCurrentLevel(xp)}/${LevelSystem.xpNeededPerLevel()} XP",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
        Spacer(Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ObsidianBg)
                .border(0.5.dp, CarbonBorder, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0.05f, 1f))
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Brush.horizontalGradient(listOf(extras.neonPrimary, extras.neonSecondary)))
            )
        }
    }
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Filled.Verified,
        contentDescription = "Perfil verificado",
        tint = CyberCyan,
        modifier = modifier.size(16.dp)
    )
}

@Composable
fun ClanTagPill(tag: String, modifier: Modifier = Modifier) {
    val extras = LocalPlayForjaExtras.current
    if (tag.isBlank()) return
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = extras.neonPrimary.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, extras.neonPrimary.copy(alpha = 0.5f))
    ) {
        Text(
            text = "[$tag]",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = extras.neonGlow,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
fun PlatformPill(platform: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = CarbonHighest,
        border = BorderStroke(1.dp, CarbonBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = platformIcon(platform),
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = platform,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = (System.currentTimeMillis() - timestamp).coerceAtLeast(0L)
    val mins = diff / 60_000L
    val hours = diff / 3_600_000L
    val days = diff / 86_400_000L
    return when {
        mins < 1 -> "Ahora"
        mins < 60 -> "Hace ${mins}m"
        hours < 24 -> "Hace ${hours}h"
        days < 7 -> "Hace ${days}d"
        else -> "Hace ${days / 7}sem"
    }
}
