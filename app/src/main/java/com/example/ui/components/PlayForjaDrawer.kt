package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.GamerStatus
import com.example.data.local.UserEntity
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AppDestination(val route: String, val label: String) {
    FEED("feed", "Forja Feed"),
    EXPLORE("explore", "Explorar"),
    CLANS("clans", "Clanes & LFG"),
    MESSAGES("messages", "Mensajes"),
    PROFILE("profile", "Mi Perfil"),
    VAULT("vault", "Bóveda Guardados"),
    NOTIFICATIONS("notifications", "Notificaciones"),
    SETTINGS("settings", "Apariencia y Ajustes")
}

@Composable
fun PlayForjaDrawerContent(
    user: UserEntity,
    currentDestination: AppDestination,
    unreadMessages: Int,
    unreadNotifications: Int,
    savedPostsCount: Int,
    onNavigate: (AppDestination) -> Unit,
    onStatusChange: (String) -> Unit,
    onOpenAccountSwitcher: () -> Unit,
    onLogout: () -> Unit
) {
    val extras = LocalPlayForjaExtras.current

    ModalDrawerSheet(
        drawerContainerColor = CarbonSurface,
        drawerContentColor = TextPrimary,
        modifier = Modifier.width(316.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            // Drawer Gamer Profile Header
            Box(modifier = Modifier.fillMaxWidth()) {
                ForgeBanner(
                    bannerPreset = user.bannerPreset,
                    height = 132.dp
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ForgeAvatar(
                            avatarPreset = user.avatarPreset,
                            frameStyle = user.frameStyle,
                            displayName = user.displayName,
                            size = 62.dp,
                            statusKey = user.status,
                            level = user.level,
                            onClick = { onNavigate(AppDestination.PROFILE) }
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ObsidianBg.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, extras.neonPrimary.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "PLAYFORJA",
                                style = MaterialTheme.typography.labelLarge,
                                color = extras.neonPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        if (user.isVerified) {
                            Spacer(Modifier.width(5.dp))
                            VerifiedBadge()
                        }
                        Spacer(Modifier.width(6.dp))
                        ClanTagPill(tag = user.clanTag)
                    }
                    Text(
                        text = "@${user.handle} • ${user.customTitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(10.dp))
                    XpProgressBar(xp = user.xp, level = user.level)

                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "${user.followersCount} Seguidores",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "${user.followingCount} Siguiendo",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Status Selector Strip
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "ESTADO DE CONEXIÓN",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GamerStatus.entries.forEach { st ->
                        val selected = user.status == st.key
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) statusColor(st.key).copy(alpha = 0.2f) else CarbonElevated,
                            border = BorderStroke(
                                1.dp,
                                if (selected) statusColor(st.key) else CarbonBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onStatusChange(st.key) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(statusColor(st.key))
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = st.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) TextPrimary else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = CarbonBorder, modifier = Modifier.padding(vertical = 6.dp))

            DrawerMenuItem(
                label = "Forja Feed Global",
                icon = Icons.Filled.Home,
                selected = currentDestination == AppDestination.FEED,
                testTag = "drawer_feed",
                onClick = { onNavigate(AppDestination.FEED) }
            )
            DrawerMenuItem(
                label = "Explorar & Tendencias",
                icon = Icons.Filled.Explore,
                selected = currentDestination == AppDestination.EXPLORE,
                testTag = "drawer_explore",
                onClick = { onNavigate(AppDestination.EXPLORE) }
            )
            DrawerMenuItem(
                label = "Clanes y Arena LFG",
                icon = Icons.Filled.Shield,
                selected = currentDestination == AppDestination.CLANS,
                testTag = "drawer_clans",
                onClick = { onNavigate(AppDestination.CLANS) }
            )
            DrawerMenuItem(
                label = "Mensajes y Squads",
                icon = Icons.AutoMirrored.Filled.Chat,
                selected = currentDestination == AppDestination.MESSAGES,
                badgeCount = unreadMessages,
                testTag = "drawer_messages",
                onClick = { onNavigate(AppDestination.MESSAGES) }
            )
            DrawerMenuItem(
                label = "Notificaciones",
                icon = Icons.Filled.Notifications,
                selected = currentDestination == AppDestination.NOTIFICATIONS,
                badgeCount = unreadNotifications,
                testTag = "drawer_notifications",
                onClick = { onNavigate(AppDestination.NOTIFICATIONS) }
            )
            DrawerMenuItem(
                label = "Bóveda de Guardados",
                icon = Icons.Filled.Bookmark,
                selected = currentDestination == AppDestination.VAULT,
                badgeCount = savedPostsCount,
                testTag = "drawer_vault",
                onClick = { onNavigate(AppDestination.VAULT) }
            )
            DrawerMenuItem(
                label = "Mi Perfil Gamer",
                icon = Icons.Filled.Person,
                selected = currentDestination == AppDestination.PROFILE,
                testTag = "drawer_profile",
                onClick = { onNavigate(AppDestination.PROFILE) }
            )
            DrawerMenuItem(
                label = "Personalizar Apariencia",
                icon = Icons.Filled.Palette,
                selected = currentDestination == AppDestination.SETTINGS,
                testTag = "drawer_settings",
                onClick = { onNavigate(AppDestination.SETTINGS) }
            )

            HorizontalDivider(color = CarbonBorder, modifier = Modifier.padding(vertical = 8.dp))

            DrawerMenuItem(
                label = "Cambiar Cuenta Local",
                icon = Icons.Filled.SwapHoriz,
                selected = false,
                testTag = "drawer_switch_account",
                onClick = onOpenAccountSwitcher
            )

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Cerrar Sesión",
                        style = MaterialTheme.typography.titleSmall,
                        color = CrimsonAlert
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = CrimsonAlert
                    )
                },
                selected = false,
                onClick = onLogout,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .testTag("drawer_logout"),
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    testTag: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    NavigationDrawerItem(
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        badge = if (badgeCount > 0) {
            {
                Badge(
                    containerColor = extras.neonPrimary,
                    contentColor = ObsidianBg
                ) {
                    Text(text = badgeCount.toString())
                }
            }
        } else null,
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = extras.neonPrimary.copy(alpha = 0.16f),
            selectedIconColor = extras.neonPrimary,
            selectedTextColor = TextPrimary,
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextSecondary
        )
    )
}
