package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.GameCatalog
import com.example.data.local.GameLogoBadge
import com.example.data.local.GamerStatus
import com.example.data.local.PostWithDetails
import com.example.data.local.UserEntity
import com.example.ui.components.ClanTagPill
import com.example.ui.components.CyberSurfaceCard
import com.example.ui.components.ForgeAvatar
import com.example.ui.components.ForgeBanner
import com.example.ui.components.PlatformPill
import com.example.ui.components.PostCard
import com.example.ui.components.ProfileBackgroundLayer
import com.example.ui.components.VerifiedBadge
import com.example.ui.components.XpProgressBar
import com.example.ui.components.frameBrush
import com.example.ui.components.statusColor
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    profileUser: UserEntity,
    currentUser: UserEntity,
    allPosts: List<PostWithDetails>,
    isFollowing: Boolean,
    isSecondaryView: Boolean = false,
    onBack: (() -> Unit)? = null,
    onEditProfile: () -> Unit,
    onToggleFollow: (Long) -> Unit,
    onOpenDm: (UserEntity) -> Unit,
    onAuthorClick: (Long) -> Unit,
    onLikeClick: (Long) -> Unit,
    onCommentClick: (PostWithDetails) -> Unit,
    onRepostClick: (Long) -> Unit,
    onBookmarkClick: (Long) -> Unit,
    onVotePoll: (Long, Int) -> Unit,
    onJoinLfg: (Long) -> Unit,
    onDeletePost: (Long) -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    val isMe = profileUser.id == currentUser.id
    var selectedTab by remember { mutableIntStateOf(0) }

    if (isSecondaryView && onBack != null) {
        BackHandler(onBack = onBack)
    }

    val userPosts = remember(allPosts, profileUser.id) {
        allPosts.filter { it.author.id == profileUser.id }
    }
    val mediaPosts = remember(userPosts) {
        userPosts.filter { it.post.mediaType != "NONE" }
    }
    val bookmarkedPosts = remember(allPosts) {
        allPosts.filter { it.isBookmarked }
    }
    val selectedGames = remember(profileUser.favoriteGamesCsv, profileUser.favoriteGame) {
        GameCatalog.parseSelectedGames(profileUser.favoriteGamesCsv, profileUser.favoriteGame)
    }

    val tabs = if (isMe) {
        listOf("Posts (${userPosts.size})", "Media (${mediaPosts.size})", "Guardados (${bookmarkedPosts.size})", "Setup & Logros")
    } else {
        listOf("Posts (${userPosts.size})", "Media (${mediaPosts.size})", "Setup & Logros")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Custom Profile Background Layer
        ProfileBackgroundLayer(profileBgStyle = profileUser.profileBgStyle)

        LazyColumn(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cover Banner, Profile Frame & Identity Header
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ForgeBanner(bannerPreset = profileUser.bannerPreset, height = 165.dp)

                        if (isSecondaryView && onBack != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ObsidianBg.copy(alpha = 0.75f),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                            ) {
                                IconButton(onClick = onBack) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = TextPrimary
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .offset(y = 115.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            ForgeAvatar(
                                avatarPreset = profileUser.avatarPreset,
                                frameStyle = profileUser.frameStyle,
                                displayName = profileUser.displayName,
                                size = 88.dp,
                                statusKey = profileUser.status,
                                level = profileUser.level
                            )

                            if (isMe) {
                                Button(
                                    onClick = onEditProfile,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = extras.neonPrimary,
                                        contentColor = ObsidianBg
                                    ),
                                    modifier = Modifier.testTag("edit_profile_button")
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Personalizar Perfil", style = MaterialTheme.typography.labelLarge)
                                }
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onOpenDm(profileUser) },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, extras.neonSecondary)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = extras.neonSecondary, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Mensaje", color = extras.neonSecondary)
                                    }
                                    Button(
                                        onClick = { onToggleFollow(profileUser.id) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isFollowing) CarbonHighest else extras.neonPrimary,
                                            contentColor = if (isFollowing) TextPrimary else ObsidianBg
                                        )
                                    ) {
                                        Text(if (isFollowing) "Siguiendo" else "+ Seguir")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(48.dp))

                    // Framed Identity Card
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CarbonElevated.copy(alpha = 0.88f),
                        border = BorderStroke(1.6.dp, frameBrush(profileUser.frameStyle)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profileUser.displayName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextPrimary
                                )
                                if (profileUser.isVerified) {
                                    Spacer(Modifier.width(6.dp))
                                    VerifiedBadge()
                                }
                                Spacer(Modifier.width(8.dp))
                                ClanTagPill(tag = profileUser.clanTag)
                            }

                            Spacer(Modifier.height(3.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "@${profileUser.handle}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = extras.neonSecondary
                                )
                                Text("•", color = TextMuted)
                                Text(
                                    text = profileUser.customTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = extras.neonGlow
                                )
                                Text("•", color = TextMuted)
                                Text(
                                    text = GamerStatus.fromKey(profileUser.status).label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = statusColor(profileUser.status)
                                )
                            }

                            Spacer(Modifier.height(10.dp))
                            // Enhanced Bio Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ObsidianBg.copy(alpha = 0.72f),
                                border = BorderStroke(1.dp, CarbonBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    if (profileUser.bioMotto.isNotBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.FormatQuote,
                                                contentDescription = null,
                                                tint = extras.neonPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = profileUser.bioMotto,
                                                style = MaterialTheme.typography.titleSmall.copy(fontStyle = FontStyle.Italic),
                                                color = extras.neonGlow
                                            )
                                        }
                                        Spacer(Modifier.height(6.dp))
                                    }
                                    Text(
                                        text = profileUser.bio,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                    if (profileUser.playSchedule.isNotBlank()) {
                                        Spacer(Modifier.height(8.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.Schedule,
                                                contentDescription = null,
                                                tint = extras.neonSecondary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(Modifier.width(5.dp))
                                            Text(
                                                text = "Horario: ${profileUser.playSchedule}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = extras.neonSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PlatformPill(platform = profileUser.platform)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CarbonHighest,
                                    border = BorderStroke(1.dp, CarbonBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.SportsEsports,
                                            contentDescription = null,
                                            tint = extras.neonPrimary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = selectedGames.firstOrNull()?.title ?: profileUser.favoriteGame,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            XpProgressBar(xp = profileUser.xp, level = profileUser.level)

                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ProfileStatItem("${userPosts.size}", "Posts")
                                ProfileStatItem("${profileUser.followersCount}", "Seguidores")
                                ProfileStatItem("${profileUser.followingCount}", "Siguiendo")
                                ProfileStatItem("${profileUser.xp}", "XP Total")
                            }
                        }
                    }

                    // REAL FAVORITE GAMES SHOWCASE WITH REAL LOGOS
                    if (selectedGames.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "JUEGOS FAVORITOS (${selectedGames.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = extras.neonSecondary
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                selectedGames.forEachIndexed { idx, game ->
                                    val isPrimary = idx == 0
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = CarbonElevated.copy(alpha = 0.92f),
                                        border = BorderStroke(
                                            if (isPrimary) 1.5.dp else 1.dp,
                                            if (isPrimary) game.primaryColor else CarbonBorder
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            GameLogoBadge(game = game, size = 38.dp)
                                            Spacer(Modifier.width(9.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = game.title,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = TextPrimary
                                                    )
                                                    if (isPrimary) {
                                                        Spacer(Modifier.width(4.dp))
                                                        Icon(
                                                            imageVector = Icons.Filled.Star,
                                                            contentDescription = "Juego principal",
                                                            tint = ChampionGold,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = if (isPrimary) "Principal • ${game.genre.label}" else game.genre.label,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isPrimary) ChampionGold else TextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Profile Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { idx, label ->
                        val selected = selectedTab == idx
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) extras.neonPrimary else CarbonElevated,
                            border = BorderStroke(1.dp, if (selected) extras.neonPrimary else CarbonBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedTab = idx }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) ObsidianBg else TextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            val setupTabIndex = if (isMe) 3 else 2
            if (selectedTab == setupTabIndex) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CyberSurfaceCard {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Computer, contentDescription = null, tint = extras.neonSecondary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Setup & Hardware", style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = profileUser.hardwareSpecs.ifBlank { "Sin especificar aún." },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }

                        CyberSurfaceCard {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Link, contentDescription = null, tint = extras.neonPrimary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Canales y Redes Gamer", style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = profileUser.socialLinks.ifBlank { "Sin enlaces configurados." },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }

                        CyberSurfaceCard(highlighted = true) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = ChampionGold)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Vitrina de Insignias", style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(Modifier.height(10.dp))
                                val badges = profileUser.badgesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    badges.forEach { badge ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = ChampionGold.copy(alpha = 0.14f),
                                            border = BorderStroke(1.dp, ChampionGold.copy(alpha = 0.6f))
                                        ) {
                                            Text(
                                                text = "★ $badge",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = ChampionGold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val listToShow = when {
                    selectedTab == 0 -> userPosts
                    selectedTab == 1 -> mediaPosts
                    isMe && selectedTab == 2 -> bookmarkedPosts
                    else -> emptyList()
                }
                if (listToShow.isEmpty()) {
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            CyberSurfaceCard {
                                Text(
                                    text = "Todavía no hay publicaciones en esta sección.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted,
                                    modifier = Modifier.padding(24.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(listToShow, key = { it.post.id }) { item ->
                        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                            PostCard(
                                item = item,
                                currentUserId = currentUser.id,
                                onAuthorClick = onAuthorClick,
                                onFollowClick = { onToggleFollow(item.author.id) },
                                onLikeClick = { onLikeClick(item.post.id) },
                                onCommentClick = { onCommentClick(item) },
                                onRepostClick = { onRepostClick(item.post.id) },
                                onBookmarkClick = { onBookmarkClick(item.post.id) },
                                onVotePoll = { idx -> onVotePoll(item.post.id, idx) },
                                onJoinLfg = { onJoinLfg(item.post.id) },
                                onHashtagClick = {},
                                onDeleteClick = { onDeletePost(item.post.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}
