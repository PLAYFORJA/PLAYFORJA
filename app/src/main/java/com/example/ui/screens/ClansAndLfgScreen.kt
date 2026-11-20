package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.ClanEntity
import com.example.data.local.PostCategory
import com.example.data.local.PostWithDetails
import com.example.data.local.UserEntity
import com.example.ui.components.ClanTagPill
import com.example.ui.components.CyberSurfaceCard
import com.example.ui.components.ForgeBanner
import com.example.ui.components.PostCard
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VenomLime

@Composable
fun ClansAndLfgScreen(
    currentUser: UserEntity,
    clans: List<ClanEntity>,
    lfgPosts: List<PostWithDetails>,
    onToggleJoinClan: (Long) -> Unit,
    onCreateClan: (name: String, tag: String, description: String, gameFocus: String, region: String) -> Unit,
    onOpenLfgComposer: () -> Unit,
    onAuthorClick: (Long) -> Unit,
    onFollowClick: (Long) -> Unit,
    onLikeClick: (Long) -> Unit,
    onCommentClick: (PostWithDetails) -> Unit,
    onRepostClick: (Long) -> Unit,
    onBookmarkClick: (Long) -> Unit,
    onVotePoll: (Long, Int) -> Unit,
    onJoinLfg: (Long) -> Unit,
    onDeletePost: (Long) -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateClanDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
        ) {
            // Sub-tab selector
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CarbonElevated,
                border = BorderStroke(1.dp, CarbonBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    listOf(0 to "Clanes y Comunidades", 1 to "Arena LFG (${lfgPosts.size})").forEach { (idx, label) ->
                        val selected = selectedTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) extras.neonPrimary else Color.Transparent)
                                .clickable { selectedTab = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) ObsidianBg else TextSecondary
                            )
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Button(
                            onClick = { showCreateClanDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("create_clan_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = extras.neonSecondary,
                                contentColor = ObsidianBg
                            )
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("FORJAR NUEVO CLAN", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    items(clans, key = { it.id }) { clan ->
                        CyberSurfaceCard(highlighted = clan.isJoined) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                ForgeBanner(bannerPreset = clan.bannerPreset, height = 84.dp)
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ClanTagPill(tag = clan.tag)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = clan.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = clan.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Groups, contentDescription = null, tint = extras.neonSecondary, modifier = Modifier.size(15.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "${clan.membersCount} miembros • Líder: ${clan.leaderName}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = TextPrimary
                                            )
                                        }
                                        Spacer(Modifier.height(3.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Public, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "${clan.gameFocus} • ${clan.region}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    if (clan.isJoined) {
                                        OutlinedButton(
                                            onClick = { onToggleJoinClan(clan.id) },
                                            border = BorderStroke(1.dp, VenomLime),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Miembro", color = VenomLime)
                                        }
                                    } else {
                                        Button(
                                            onClick = { onToggleJoinClan(clan.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = extras.neonPrimary,
                                                contentColor = ObsidianBg
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Filled.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Unirme")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Button(
                            onClick = onOpenLfgComposer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = extras.neonPrimary,
                                contentColor = ObsidianBg
                            )
                        ) {
                            Icon(Icons.Filled.Groups, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("RECLUTAR ESCUADRÓN LFG", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    if (lfgPosts.isEmpty()) {
                        item {
                            CyberSurfaceCard {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No hay solicitudes LFG activas", style = MaterialTheme.typography.titleMedium)
                                    Text("Publica la primera invitación para armar equipo.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                            }
                        }
                    } else {
                        items(lfgPosts, key = { it.post.id }) { item ->
                            PostCard(
                                item = item,
                                currentUserId = currentUser.id,
                                onAuthorClick = onAuthorClick,
                                onFollowClick = onFollowClick,
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

    if (showCreateClanDialog) {
        var name by remember { mutableStateOf("") }
        var tag by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var game by remember { mutableStateOf("") }
        var region by remember { mutableStateOf("LATAM / Global") }

        AlertDialog(
            onDismissRequest = { showCreateClanDialog = false },
            containerColor = CarbonSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Forjar Nuevo Clan", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del clan") }, singleLine = true)
                    OutlinedTextField(value = tag, onValueChange = { tag = it.take(5) }, label = { Text("Tag (máx 5 letras)") }, singleLine = true)
                    OutlinedTextField(value = game, onValueChange = { game = it }, label = { Text("Juegos principales") }, singleLine = true)
                    OutlinedTextField(value = region, onValueChange = { region = it }, label = { Text("Región") }, singleLine = true)
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción y reglas") }, minLines = 2)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && tag.isNotBlank()) {
                            onCreateClan(name, tag, desc, game, region)
                            showCreateClanDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = extras.neonPrimary, contentColor = ObsidianBg)
                ) {
                    Text("Forjar Clan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateClanDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }
}
