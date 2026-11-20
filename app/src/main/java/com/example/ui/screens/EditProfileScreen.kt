package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.FrameStyle
import com.example.data.local.GameCatalog
import com.example.data.local.GameGenre
import com.example.data.local.GameLogoBadge
import com.example.data.local.MediaStorageHelper
import com.example.data.local.ProfileBgStyle
import com.example.data.local.UserEntity
import com.example.ui.components.AvatarCatalog
import com.example.ui.components.ForgeAvatar
import com.example.ui.components.ForgeBanner
import com.example.ui.components.ProfileBackgroundLayer
import com.example.ui.components.frameBrush
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ForgeEmber
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PlasmaViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VenomLime

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: UserEntity,
    onBack: () -> Unit,
    onSave: (
        displayName: String,
        bio: String,
        bioMotto: String,
        playSchedule: String,
        customTitle: String,
        platform: String,
        favoriteGame: String,
        favoriteGamesCsv: String,
        avatarPreset: String,
        bannerPreset: String,
        profileBgStyle: String,
        frameStyle: String,
        accentTheme: String,
        hardwareSpecs: String,
        socialLinks: String,
        clanTag: String
    ) -> Unit
) {
    BackHandler(onBack = onBack)

    val extras = LocalPlayForjaExtras.current
    val context = LocalContext.current

    var displayName by remember { mutableStateOf(user.displayName) }
    var bio by remember { mutableStateOf(user.bio) }
    var bioMotto by remember { mutableStateOf(user.bioMotto) }
    var playSchedule by remember { mutableStateOf(user.playSchedule) }
    var customTitle by remember { mutableStateOf(user.customTitle) }
    var platform by remember { mutableStateOf(user.platform) }
    var avatarPreset by remember { mutableStateOf(user.avatarPreset) }
    var bannerPreset by remember { mutableStateOf(user.bannerPreset) }
    var profileBgStyle by remember { mutableStateOf(user.profileBgStyle) }
    var frameStyle by remember { mutableStateOf(user.frameStyle) }
    var accentTheme by remember { mutableStateOf(user.accentTheme) }
    var hardwareSpecs by remember { mutableStateOf(user.hardwareSpecs) }
    var socialLinks by remember { mutableStateOf(user.socialLinks) }
    var clanTag by remember { mutableStateOf(user.clanTag) }

    // Custom Avatar Builder state
    var avatarTab by remember { mutableIntStateOf(if (user.avatarPreset.startsWith("custom:")) 1 else 0) }
    val initialCustomParts = user.avatarPreset.removePrefix("custom:").split(":")
    var customEmblemKey by remember {
        mutableStateOf(if (user.avatarPreset.startsWith("custom:")) initialCustomParts.getOrElse(0) { "gamepad" } else "gamepad")
    }
    var customColorKey by remember {
        mutableStateOf(if (user.avatarPreset.startsWith("custom:")) initialCustomParts.getOrElse(1) { "ember" } else "ember")
    }

    // Real Favorite Games state
    val initialGames = remember(user.favoriteGamesCsv, user.favoriteGame) {
        GameCatalog.parseSelectedGames(user.favoriteGamesCsv, user.favoriteGame)
    }
    val selectedGames = remember {
        mutableStateListOf<com.example.data.local.GameInfo>().apply { addAll(initialGames) }
    }
    var gameSearchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf(GameGenre.ALL) }

    // Photo Pickers
    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarPreset = "uri:${MediaStorageHelper.persistPickedImage(context, uri)}"
        }
    }

    val bannerPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            bannerPreset = "uri:${MediaStorageHelper.persistPickedImage(context, uri)}"
        }
    }

    val profileBgPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            profileBgStyle = "uri:${MediaStorageHelper.persistPickedImage(context, uri)}"
        }
    }

    val bannerPresets = listOf(
        "banner_forge" to "Estadio Forja",
        "banner_cyber" to "Setup RGB",
        "banner_arena" to "Arena Combate",
        "banner_matrix" to "Grid Ciber"
    )
    val platforms = listOf("PC", "PS5", "Xbox", "Switch", "Móvil")
    val bioTemplates = listOf(
        "Competitivo en Ranked y amante de los buenos clutches. ¡Si buscas squad serio, escríbeme!",
        "Streamer y creador libre en PlayForja. Compartiendo guías, clips y setups cada semana.",
        "Explorador de RPGs, Soulsborne y joyas indie. Siempre dispuesto a ayudar en co-op."
    )
    val scheduleChips = listOf("Noches LATAM", "Fines de semana", "24/7 Ranked", "Tardes Chill")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentAlignment = Alignment.TopCenter
    ) {
        ProfileBackgroundLayer(profileBgStyle = profileBgStyle)

        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Top Bar
            Surface(
                color = CarbonElevated.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, CarbonBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                        }
                        Text(
                            text = "Personalizar Perfil",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                    }
                    Button(
                        onClick = {
                            val primaryGameTitle = selectedGames.firstOrNull()?.title ?: user.favoriteGame
                            val gamesCsv = selectedGames.joinToString(",") { it.id }
                            onSave(
                                displayName,
                                bio,
                                bioMotto,
                                playSchedule,
                                customTitle,
                                platform,
                                primaryGameTitle,
                                gamesCsv,
                                avatarPreset,
                                bannerPreset,
                                profileBgStyle,
                                frameStyle,
                                accentTheme,
                                hardwareSpecs,
                                socialLinks,
                                clanTag
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = extras.neonPrimary,
                            contentColor = ObsidianBg
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_profile_button")
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Guardar")
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // LIVE PREVIEW CARD WITH CHOSEN FRAME + BACKGROUND + GAMES
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CarbonElevated.copy(alpha = 0.92f),
                    border = BorderStroke(2.dp, frameBrush(frameStyle)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ForgeBanner(bannerPreset = bannerPreset, height = 118.dp)
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ForgeAvatar(
                                    avatarPreset = avatarPreset,
                                    frameStyle = frameStyle,
                                    displayName = displayName.ifBlank { user.displayName },
                                    size = 68.dp,
                                    statusKey = user.status,
                                    level = user.level
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = displayName.ifBlank { user.displayName },
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${customTitle.ifBlank { user.customTitle }} • [$clanTag]",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = extras.neonSecondary
                                    )
                                    if (bioMotto.isNotBlank()) {
                                        Text(
                                            text = "“$bioMotto”",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = extras.neonGlow,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                        if (selectedGames.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                selectedGames.take(5).forEach { game ->
                                    GameLogoBadge(game = game, size = 30.dp)
                                }
                                Text(
                                    text = "${selectedGames.size} juego(s) en vitrina",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // SECTION 1: AVATARS (18 PRESETS + CUSTOM BUILDER + OWN PHOTO)
                SectionCard(title = "1. AVATAR Y FOTO PROPIA") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                avatarPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, extras.neonSecondary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = extras.neonSecondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Mi Foto de Galería", color = extras.neonSecondary, maxLines = 1)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Toggle between Catalog (18) and Custom Avatar Builder
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ObsidianBg,
                        border = BorderStroke(1.dp, CarbonBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            listOf(0 to "Catálogo (${AvatarCatalog.presets.size})", 1 to "Avatar Personalizado").forEach { (tabIdx, label) ->
                                val selected = avatarTab == tabIdx
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) extras.neonPrimary else Color.Transparent)
                                        .clickable { avatarTab = tabIdx }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) ObsidianBg else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    if (avatarTab == 0) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AvatarCatalog.presets.forEach { preset ->
                                val selected = avatarPreset == preset.key
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(66.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) extras.neonPrimary.copy(alpha = 0.14f) else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (selected) extras.neonPrimary else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { avatarPreset = preset.key }
                                        .padding(4.dp)
                                ) {
                                    ForgeAvatar(
                                        avatarPreset = preset.key,
                                        frameStyle = frameStyle,
                                        displayName = preset.label,
                                        size = 48.dp
                                    )
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        text = preset.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) extras.neonPrimary else TextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    } else {
                        Text("ELIGE EMBLEMA", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AvatarCatalog.customEmblems.forEach { emb ->
                                val selected = customEmblemKey == emb.key
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) extras.neonPrimary.copy(alpha = 0.2f) else ObsidianBg,
                                    border = BorderStroke(1.dp, if (selected) extras.neonPrimary else CarbonBorder),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            customEmblemKey = emb.key
                                            avatarPreset = AvatarCatalog.buildCustomKey(customEmblemKey, customColorKey)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(emb.icon, contentDescription = null, tint = if (selected) extras.neonPrimary else TextSecondary, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(5.dp))
                                        Text(emb.label, style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        Text("ELIGE ENERGÍA DE COLOR", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AvatarCatalog.customColors.forEach { col ->
                                val selected = customColorKey == col.key
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) col.accent.copy(alpha = 0.22f) else ObsidianBg,
                                    border = BorderStroke(1.dp, if (selected) col.accent else CarbonBorder),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            customColorKey = col.key
                                            avatarPreset = AvatarCatalog.buildCustomKey(customEmblemKey, customColorKey)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(col.accent))
                                        Spacer(Modifier.width(5.dp))
                                        Text(col.label, style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                // SECTION 2: BANNER + PROFILE BACKGROUND + PROFILE FRAME + APP NEON THEME
                SectionCard(title = "2. PORTADA, FONDO DEL PERFIL Y MARCO") {
                    Text("PORTADA SUPERIOR (BANNER)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                bannerPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, extras.neonSecondary)
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = extras.neonSecondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Foto Galería", color = extras.neonSecondary)
                        }
                        bannerPresets.forEach { (key, label) ->
                            val selected = bannerPreset == key
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) extras.neonPrimary else ObsidianBg,
                                border = BorderStroke(1.dp, if (selected) extras.neonPrimary else CarbonBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { bannerPreset = key }
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) ObsidianBg else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text("FONDO COMPLETO DEL PERFIL", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                profileBgPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, extras.neonSecondary)
                        ) {
                            Icon(Icons.Filled.Wallpaper, contentDescription = null, tint = extras.neonSecondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Foto Fondo", color = extras.neonSecondary)
                        }
                        ProfileBgStyle.entries.forEach { bg ->
                            val selected = profileBgStyle == bg.key
                            val swatch = when (bg) {
                                ProfileBgStyle.OBSIDIAN -> ObsidianBg
                                ProfileBgStyle.CYBER_GRID -> CyberCyan
                                ProfileBgStyle.MAGMA -> ForgeEmber
                                ProfileBgStyle.VOID_NEBULA -> PlasmaViolet
                                ProfileBgStyle.TOXIC_CIRCUIT -> VenomLime
                                ProfileBgStyle.GOLD_ROYAL -> ChampionGold
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) extras.neonPrimary.copy(alpha = 0.2f) else ObsidianBg,
                                border = BorderStroke(1.5.dp, if (selected) extras.neonPrimary else CarbonBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { profileBgStyle = bg.key }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .clip(CircleShape)
                                            .background(swatch)
                                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = bg.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) TextPrimary else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text("MARCO DE PERFIL Y AVATAR", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FrameStyle.entries.forEach { frame ->
                            val selected = frameStyle == frame.key
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) CarbonHighest else ObsidianBg,
                                border = BorderStroke(if (selected) 2.dp else 1.2.dp, frameBrush(frame.key)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { frameStyle = frame.key }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(frameBrush(frame.key))
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = frame.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) TextPrimary else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text("TEMA NEÓN DE LA APP", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccentTheme.entries.forEach { theme ->
                            val selected = accentTheme == theme.key
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) theme.primary.copy(alpha = 0.22f) else ObsidianBg,
                                border = BorderStroke(1.5.dp, if (selected) theme.primary else CarbonBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { accentTheme = theme.key }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(theme.primary))
                                    Spacer(Modifier.width(6.dp))
                                    Text(theme.label, style = MaterialTheme.typography.labelMedium, color = if (selected) TextPrimary else TextSecondary)
                                }
                            }
                        }
                    }
                }

                // SECTION 3: ENHANCED BIOGRAPHY
                SectionCard(title = "3. IDENTIDAD Y BIOGRAFÍA MEJORADA") {
                    EditProfileField("Nombre Visible", displayName) { displayName = it }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1.6f)) {
                            EditProfileField("Título / Rango", customTitle) { customTitle = it }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            EditProfileField("Tag Clan", clanTag) { clanTag = it.take(5) }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    EditProfileField("Lema o Frase Destacada", bioMotto) { bioMotto = it.take(80) }
                    Spacer(Modifier.height(10.dp))
                    EditProfileField("Horario / Disponibilidad", playSchedule) { playSchedule = it }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scheduleChips.forEach { chip ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (playSchedule == chip) extras.neonSecondary.copy(alpha = 0.2f) else ObsidianBg,
                                border = BorderStroke(1.dp, if (playSchedule == chip) extras.neonSecondary else CarbonBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { playSchedule = chip }
                            ) {
                                Text(
                                    text = chip,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (playSchedule == chip) extras.neonSecondary else TextMuted,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    EditProfileField(
                        label = "Biografía Completa (${bio.length}/220)",
                        value = bio,
                        singleLine = false
                    ) { bio = it.take(220) }

                    Spacer(Modifier.height(8.dp))
                    Text("PLANTILLAS RÁPIDAS DE BIO", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Competitivo", "Streamer", "Coleccionista RPG").forEachIndexed { i, label ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CarbonHighest,
                                border = BorderStroke(1.dp, CarbonBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { bio = bioTemplates[i] }
                            ) {
                                Text(
                                    text = "+ $label",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extras.neonSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // SECTION 4: REAL FAVORITE GAMES CATALOG WITH REAL LOGOS
                SectionCard(title = "4. CATÁLOGO REAL DE JUEGOS FAVORITOS") {
                    Text(
                        text = "TUS JUEGOS SELECCIONADOS (${selectedGames.size}/8) — Toca para fijar Principal",
                        style = MaterialTheme.typography.labelSmall,
                        color = extras.neonSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    if (selectedGames.isEmpty()) {
                        Text(
                            text = "Aún no has seleccionado ningún juego del catálogo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedGames.forEachIndexed { idx, game ->
                                val isPrimary = idx == 0
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isPrimary) game.primaryColor.copy(alpha = 0.22f) else ObsidianBg,
                                    border = BorderStroke(
                                        1.5.dp,
                                        if (isPrimary) game.primaryColor else extras.neonSecondary
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            selectedGames.remove(game)
                                            selectedGames.add(0, game)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        GameLogoBadge(game = game, size = 32.dp)
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = game.title,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = TextPrimary
                                                )
                                                if (isPrimary) {
                                                    Spacer(Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.Filled.Star,
                                                        contentDescription = "Principal",
                                                        tint = ChampionGold,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = if (isPrimary) "Principal" else game.genre.label,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isPrimary) ChampionGold else TextMuted
                                            )
                                        }
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Quitar ${game.title}",
                                            tint = TextMuted,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clickable { selectedGames.remove(game) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = gameSearchQuery,
                        onValueChange = { gameSearchQuery = it },
                        placeholder = { Text("Buscar juego real (Valorant, GTA V, Elden Ring...)", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = extras.neonSecondary) },
                        trailingIcon = if (gameSearchQuery.isNotBlank()) {
                            {
                                IconButton(onClick = { gameSearchQuery = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Limpiar", tint = TextMuted)
                                }
                            }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = extras.neonPrimary,
                            unfocusedBorderColor = CarbonBorder,
                            focusedContainerColor = ObsidianBg,
                            unfocusedContainerColor = ObsidianBg,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("game_catalog_search")
                    )

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GameGenre.entries.forEach { genre ->
                            val selected = selectedGenre == genre
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) extras.neonPrimary else ObsidianBg,
                                border = BorderStroke(1.dp, if (selected) extras.neonPrimary else CarbonBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedGenre = genre }
                            ) {
                                Text(
                                    text = genre.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) ObsidianBg else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    val filteredGames = remember(gameSearchQuery, selectedGenre) {
                        val q = gameSearchQuery.trim().lowercase()
                        GameCatalog.allGames.filter { game ->
                            val matchesGenre = selectedGenre == GameGenre.ALL || game.genre == selectedGenre
                            val matchesQuery = q.isEmpty() || game.title.lowercase().contains(q) || game.genre.label.lowercase().contains(q)
                            matchesGenre && matchesQuery
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredGames.forEach { game ->
                            val isChosen = selectedGames.any { it.id == game.id }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isChosen) game.primaryColor.copy(alpha = 0.14f) else ObsidianBg,
                                border = BorderStroke(
                                    if (isChosen) 1.5.dp else 1.dp,
                                    if (isChosen) game.primaryColor else CarbonBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (isChosen) {
                                            selectedGames.removeAll { it.id == game.id }
                                        } else if (selectedGames.size < 8) {
                                            selectedGames.add(game)
                                        }
                                    }
                                    .testTag("catalog_game_${game.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GameLogoBadge(game = game, size = 42.dp)
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = game.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = game.genre.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isChosen) game.primaryColor else TextMuted
                                        )
                                    }
                                    if (isChosen) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = game.primaryColor.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, game.primaryColor)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.CheckCircle,
                                                    contentDescription = "Seleccionado",
                                                    tint = game.primaryColor,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    text = "Elegido",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = game.primaryColor
                                                )
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "+ Añadir",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = extras.neonSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // SECTION 5: PLATFORM, HARDWARE & SOCIALS
                SectionCard(title = "5. PLATAFORMA, HARDWARE Y REDES") {
                    Text("PLATAFORMA PRINCIPAL", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        platforms.forEach { p ->
                            val selected = platform == p
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) extras.neonPrimary else CarbonHighest,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { platform = p }
                            ) {
                                Text(
                                    text = p,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) ObsidianBg else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    EditProfileField("Setup / Especificaciones Hardware", hardwareSpecs, singleLine = false) { hardwareSpecs = it }
                    Spacer(Modifier.height(10.dp))
                    EditProfileField("Enlaces (Twitch, Discord, Steam, PSN...)", socialLinks, singleLine = false) { socialLinks = it }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CarbonElevated.copy(alpha = 0.90f),
        border = BorderStroke(1.dp, CarbonBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = LocalPlayForjaExtras.current.neonPrimary
            )
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun EditProfileField(
    label: String,
    value: String,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = extras.neonPrimary,
            unfocusedBorderColor = CarbonBorder,
            focusedContainerColor = ObsidianBg,
            unfocusedContainerColor = ObsidianBg,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = extras.neonPrimary,
            unfocusedLabelColor = TextMuted
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
