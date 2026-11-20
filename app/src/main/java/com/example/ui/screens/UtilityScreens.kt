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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.data.local.NotificationEntity
import com.example.data.local.PostWithDetails
import com.example.data.local.UserEntity
import com.example.ui.components.CyberSurfaceCard
import com.example.ui.components.ForgeAvatar
import com.example.ui.components.PostCard
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onMarkAllRead: () -> Unit
) {
    val extras = LocalPlayForjaExtras.current
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
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CENTRO DE ACTIVIDAD",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted
                    )
                    TextButton(onClick = onMarkAllRead) {
                        Icon(Icons.Filled.DoneAll, contentDescription = null, tint = extras.neonSecondary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Marcar leídas", color = extras.neonSecondary)
                    }
                }
            }

            if (notifications.isEmpty()) {
                item {
                    CyberSurfaceCard {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = null, tint = extras.neonSecondary, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Todo al día", style = MaterialTheme.typography.titleMedium)
                            Text("Aquí aparecerán tus logros, menciones y avisos de clanes.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                }
            } else {
                items(notifications, key = { it.id }) { notif ->
                    CyberSurfaceCard(highlighted = !notif.isRead) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (notif.type == "BADGE") ChampionGold.copy(alpha = 0.18f) else extras.neonPrimary.copy(alpha = 0.16f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = when (notif.type) {
                                            "BADGE" -> Icons.Filled.MilitaryTech
                                            "CLAN" -> Icons.Filled.Shield
                                            else -> Icons.Filled.Notifications
                                        },
                                        contentDescription = null,
                                        tint = if (notif.type == "BADGE") ChampionGold else extras.neonPrimary
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(notif.actorName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                    Text(formatRelativeTime(notif.createdAt), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                Spacer(Modifier.height(3.dp))
                                Text(notif.message, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VaultScreen(
    currentUser: UserEntity,
    bookmarkedPosts: List<PostWithDetails>,
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
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "BÓVEDA DE PUBLICACIONES GUARDADAS",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted
                )
            }
            if (bookmarkedPosts.isEmpty()) {
                item {
                    CyberSurfaceCard {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Bookmark, contentDescription = null, tint = extras.neonSecondary, modifier = Modifier.size(42.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Tu Bóveda está vacía", style = MaterialTheme.typography.titleMedium)
                            Text("Toca el icono de guardar en cualquier post para tenerlo siempre a mano.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                }
            } else {
                items(bookmarkedPosts, key = { it.post.id }) { item ->
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

@Composable
fun SettingsScreen(
    currentUser: UserEntity,
    currentTheme: AccentTheme,
    compactCards: Boolean,
    onSelectTheme: (String) -> Unit,
    onToggleCompactCards: (Boolean) -> Unit,
    onOpenAccountSwitcher: () -> Unit,
    onLogout: () -> Unit
) {
    val extras = LocalPlayForjaExtras.current
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CyberSurfaceCard(highlighted = true) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Palette, contentDescription = null, tint = extras.neonPrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("Tema Neón de PlayForja", style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Personaliza el color de energía en toda la interfaz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AccentTheme.entries.forEach { theme ->
                                val selected = currentTheme == theme
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) theme.primary.copy(alpha = 0.18f) else CarbonElevated,
                                    border = BorderStroke(1.5.dp, if (selected) theme.primary else CarbonBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectTheme(theme.key) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(theme.primary)
                                            )
                                            Spacer(Modifier.width(10.dp))
                                            Text(theme.label, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                        }
                                        if (selected) {
                                            Text("ACTIVO", style = MaterialTheme.typography.labelSmall, color = theme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                CyberSurfaceCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Modo Feed Compacto", style = MaterialTheme.typography.titleMedium)
                            Text("Reduce el tamaño de tarjetas para ver más publicaciones.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Switch(checked = compactCards, onCheckedChange = onToggleCompactCards)
                    }
                }
            }

            item {
                CyberSurfaceCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Sesión Local en Dispositivo", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Conectado como ${currentUser.displayName} (@${currentUser.handle})",
                            style = MaterialTheme.typography.bodySmall,
                            color = extras.neonSecondary
                        )
                        Button(
                            onClick = onOpenAccountSwitcher,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = extras.neonSecondary,
                                contentColor = ObsidianBg
                            )
                        ) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Cambiar o Crear Cuenta Local")
                        }
                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CrimsonAlert)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = CrimsonAlert)
                            Spacer(Modifier.width(6.dp))
                            Text("Cerrar Sesión Actual", color = CrimsonAlert)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountSwitcherDialog(
    accounts: List<UserEntity>,
    activeUserId: Long,
    onSelectAccount: (Long) -> Unit,
    onCreateNewAccount: () -> Unit,
    onDismiss: () -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CarbonSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text("Cuentas Locales en PlayForja", style = MaterialTheme.typography.titleLarge) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(accounts, key = { it.id }) { acc ->
                    val isActive = acc.id == activeUserId
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isActive) extras.neonPrimary.copy(alpha = 0.16f) else CarbonElevated,
                        border = BorderStroke(1.dp, if (isActive) extras.neonPrimary else CarbonBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectAccount(acc.id)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ForgeAvatar(
                                avatarPreset = acc.avatarPreset,
                                frameStyle = acc.frameStyle,
                                displayName = acc.displayName,
                                size = 40.dp,
                                level = acc.level
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(acc.displayName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("@${acc.handle}", style = MaterialTheme.typography.labelSmall, color = extras.neonSecondary)
                            }
                            if (isActive) {
                                Text("ACTIVA", style = MaterialTheme.typography.labelSmall, color = extras.neonPrimary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onCreateNewAccount()
                },
                colors = ButtonDefaults.buttonColors(containerColor = extras.neonPrimary, contentColor = ObsidianBg)
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Nueva Cuenta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TextSecondary)
            }
        }
    )
}
