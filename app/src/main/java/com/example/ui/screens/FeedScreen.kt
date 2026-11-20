package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.PostCategory
import com.example.data.local.PostWithDetails
import com.example.data.local.UserEntity
import com.example.ui.components.CyberSurfaceCard
import com.example.ui.components.ForgeAvatar
import com.example.ui.components.PostCard
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FeedScreen(
    currentUser: UserEntity,
    posts: List<PostWithDetails>,
    compactMode: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenComposer: () -> Unit,
    onAuthorClick: (Long) -> Unit,
    onFollowClick: (Long) -> Unit,
    onLikeClick: (Long) -> Unit,
    onCommentClick: (PostWithDetails) -> Unit,
    onRepostClick: (Long) -> Unit,
    onBookmarkClick: (Long) -> Unit,
    onVotePoll: (Long, Int) -> Unit,
    onJoinLfg: (Long) -> Unit,
    onDeletePost: (Long) -> Unit,
    onEditPost: (PostWithDetails) -> Unit = {}
) {
    val extras = LocalPlayForjaExtras.current
    var selectedFilter by remember { mutableStateOf("ALL") }
    var sortPopular by remember { mutableStateOf(false) }

    val filterItems = listOf(
        "ALL" to "Todo el Mundo",
        "FOLLOWING" to "Siguiendo"
    ) + PostCategory.entries.map { it.key to it.label }

    val filteredPosts = remember(posts, selectedFilter, searchQuery, sortPopular) {
        val q = searchQuery.trim().lowercase()
        val base = posts.filter { item ->
            val matchesFilter = when (selectedFilter) {
                "ALL" -> true
                "FOLLOWING" -> item.isFollowingAuthor || item.author.id == currentUser.id
                else -> item.post.category == selectedFilter
            }
            val matchesQuery = if (q.isEmpty()) true else {
                item.post.title.lowercase().contains(q) ||
                    item.post.content.lowercase().contains(q) ||
                    item.post.hashtags.lowercase().contains(q) ||
                    item.post.gameTag.lowercase().contains(q) ||
                    item.author.displayName.lowercase().contains(q) ||
                    item.author.handle.lowercase().contains(q)
            }
            matchesFilter && matchesQuery
        }
        if (sortPopular) {
            base.sortedByDescending { it.post.likesCount + it.post.commentsCount * 2 }
        } else {
            base
        }
    }

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
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search & Sort Strip
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = "Buscar juegos, #hashtags o gamers...",
                                color = TextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = extras.neonSecondary
                            )
                        },
                        trailingIcon = if (searchQuery.isNotBlank()) {
                            {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Limpiar búsqueda",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = extras.neonPrimary,
                            unfocusedBorderColor = CarbonBorder,
                            focusedContainerColor = CarbonElevated,
                            unfocusedContainerColor = CarbonElevated,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feed_search_input")
                    )

                    // Category Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filterItems.forEach { (key, label) ->
                            val selected = selectedFilter == key
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) extras.neonPrimary else CarbonElevated,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) extras.neonPrimary else CarbonBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedFilter = key }
                                    .testTag("filter_chip_$key")
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) ObsidianBg else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Quick Post Launcher Card
                    CyberSurfaceCard(
                        highlighted = true,
                        onClick = onOpenComposer,
                        modifier = Modifier.testTag("quick_compose_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ForgeAvatar(
                                avatarPreset = currentUser.avatarPreset,
                                frameStyle = currentUser.frameStyle,
                                displayName = currentUser.displayName,
                                size = 42.dp,
                                statusKey = currentUser.status
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "¿Qué estás jugando hoy, ${currentUser.displayName}?",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Sube clips, fotos, encuestas o busca escuadra...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = extras.neonPrimary.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, extras.neonPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AddPhotoAlternate,
                                    contentDescription = "Crear post",
                                    tint = extras.neonPrimary,
                                    modifier = Modifier.padding(8.dp).size(20.dp)
                                )
                            }
                        }
                    }

                    // Sort Toggle Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${filteredPosts.size} PUBLICACIONES",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (!sortPopular) CarbonHighest else CarbonElevated,
                                border = BorderStroke(1.dp, if (!sortPopular) extras.neonSecondary else CarbonBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { sortPopular = false }
                            ) {
                                Text(
                                    text = "Recientes",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (!sortPopular) extras.neonSecondary else TextMuted,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (sortPopular) CarbonHighest else CarbonElevated,
                                border = BorderStroke(1.dp, if (sortPopular) extras.neonPrimary else CarbonBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { sortPopular = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = if (sortPopular) extras.neonPrimary else TextMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Top GG",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (sortPopular) extras.neonPrimary else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (filteredPosts.isEmpty()) {
                item {
                    CyberSurfaceCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SportsEsports,
                                contentDescription = null,
                                tint = extras.neonSecondary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "Aún no hay publicaciones aquí",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "¡Sé el primero en forjar contenido para esta categoría!",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredPosts, key = { it.post.id }) { item ->
                    PostCard(
                        item = item,
                        currentUserId = currentUser.id,
                        compactMode = compactMode,
                        onAuthorClick = onAuthorClick,
                        onFollowClick = onFollowClick,
                        onLikeClick = { onLikeClick(item.post.id) },
                        onCommentClick = { onCommentClick(item) },
                        onRepostClick = { onRepostClick(item.post.id) },
                        onBookmarkClick = { onBookmarkClick(item.post.id) },
                        onVotePoll = { idx -> onVotePoll(item.post.id, idx) },
                        onJoinLfg = { onJoinLfg(item.post.id) },
                        onHashtagClick = { tag -> onSearchChange(tag) },
                        onDeleteClick = { onDeletePost(item.post.id) },
                        onEditClick = { onEditPost(item) }
                    )
                }
            }
        }
    }
}
