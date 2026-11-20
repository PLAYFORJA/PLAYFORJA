package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.PostWithDetails
import com.example.data.local.UserEntity
import com.example.ui.components.ClanTagPill
import com.example.ui.components.CyberSurfaceCard
import com.example.ui.components.ForgeAvatar
import com.example.ui.components.PlatformPill
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExploreScreen(
    currentUser: UserEntity,
    users: List<UserEntity>,
    posts: List<PostWithDetails>,
    followedUserIds: Set<Long>,
    onHashtagSelected: (String) -> Unit,
    onUserClick: (Long) -> Unit,
    onFollowClick: (Long) -> Unit,
    onOpenClans: () -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    val trendingTags = listOf(
        "#Valorant" to "1.4k posts",
        "#PlayForja" to "3.2k posts",
        "#SetupGamer" to "980 posts",
        "#EldenRing" to "740 posts",
        "#LFG" to "610 squads",
        "#ConceptArt" to "420 obras"
    )
    val topPosts = posts.sortedByDescending { it.post.likesCount }.take(3)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Spotlight
            item {
                CyberSurfaceCard(highlighted = true, onClick = onOpenClans) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(172.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_forge),
                            contentDescription = "Copa PlayForja",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            ObsidianBg.copy(alpha = 0.2f),
                                            ObsidianBg.copy(alpha = 0.92f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = extras.neonPrimary
                            ) {
                                Text(
                                    text = "EVENTO GLOBAL EN VIVO",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ObsidianBg,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "COPA RELÁMPAGO PLAYFORJA S5",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "Únete a un clan o arma tu escuadrón LFG para ganar la insignia Dorada.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Trending Hashtags
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = extras.neonSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "TENDENCIAS DE LA COMUNIDAD",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                    }
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        trendingTags.forEach { (tag, count) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CarbonElevated,
                                border = BorderStroke(1.dp, CarbonBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onHashtagSelected(tag) }
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = extras.neonSecondary
                                    )
                                    Text(
                                        text = count,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Top Destacados
            if (topPosts.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = extras.neonPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "CLIPS Y DEBATES MÁS VOTADOS",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                        }
                        topPosts.forEach { item ->
                            CyberSurfaceCard(onClick = { onHashtagSelected(item.post.gameTag) }) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ForgeAvatar(
                                        avatarPreset = item.author.avatarPreset,
                                        frameStyle = item.author.frameStyle,
                                        displayName = item.author.displayName,
                                        size = 42.dp
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.post.title.ifBlank { item.post.content },
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${item.author.displayName} • ${item.post.gameTag} • ${item.post.likesCount} GG",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Leaderboard header
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = ChampionGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "RANKING GLOBAL DE FORJADORES",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary
                    )
                }
            }

            itemsIndexed(users, key = { _, u -> u.id }) { index, gamer ->
                val isFollowing = followedUserIds.contains(gamer.id)
                val isMe = gamer.id == currentUser.id
                CyberSurfaceCard(
                    highlighted = index == 0,
                    onClick = { onUserClick(gamer.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (index == 0) ChampionGold.copy(alpha = 0.2f) else CarbonHighest,
                            border = BorderStroke(1.dp, if (index == 0) ChampionGold else CarbonBorder),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "#${index + 1}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (index == 0) ChampionGold else TextSecondary
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        ForgeAvatar(
                            avatarPreset = gamer.avatarPreset,
                            frameStyle = gamer.frameStyle,
                            displayName = gamer.displayName,
                            size = 46.dp,
                            statusKey = gamer.status,
                            level = gamer.level
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = gamer.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                if (gamer.isVerified) {
                                    Spacer(Modifier.width(4.dp))
                                    VerifiedBadge()
                                }
                                Spacer(Modifier.width(6.dp))
                                ClanTagPill(tag = gamer.clanTag)
                            }
                            Text(
                                text = "@${gamer.handle} • ${gamer.xp} XP",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(Modifier.height(4.dp))
                            PlatformPill(platform = "${gamer.platform} • ${gamer.favoriteGame}")
                        }
                        if (!isMe) {
                            TextButton(onClick = { onFollowClick(gamer.id) }) {
                                Text(
                                    text = if (isFollowing) "Siguiendo" else "Seguir",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isFollowing) TextSecondary else extras.neonSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
