package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.PostCategory
import com.example.data.local.PostWithDetails
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VenomLime

@Composable
fun PostCard(
    item: PostWithDetails,
    currentUserId: Long,
    compactMode: Boolean = false,
    onAuthorClick: (Long) -> Unit,
    onFollowClick: (Long) -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onRepostClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onVotePoll: (Int) -> Unit,
    onJoinLfg: () -> Unit,
    onHashtagClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val extras = LocalPlayForjaExtras.current
    val post = item.post
    val author = item.author
    val isMine = author.id == currentUserId

    CyberSurfaceCard(
        modifier = modifier.testTag("post_card_${post.id}"),
        highlighted = post.category == PostCategory.LFG.key || post.pollQuestion.isNotBlank()
    ) {
        Column(modifier = Modifier.padding(if (compactMode) 12.dp else 16.dp)) {
            // Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ForgeAvatar(
                    avatarPreset = author.avatarPreset,
                    frameStyle = author.frameStyle,
                    displayName = author.displayName,
                    size = 44.dp,
                    statusKey = author.status,
                    onClick = { onAuthorClick(author.id) }
                )
                Spacer(Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAuthorClick(author.id) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = author.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (author.isVerified) {
                            Spacer(Modifier.width(4.dp))
                            VerifiedBadge()
                        }
                        Spacer(Modifier.width(6.dp))
                        ClanTagPill(tag = author.clanTag)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "@${author.handle} • ${formatRelativeTime(post.createdAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = when (post.visibility) {
                                "CLAN" -> Icons.Filled.Shield
                                "SEGUIDORES" -> Icons.Filled.Lock
                                else -> Icons.Filled.Public
                            },
                            contentDescription = post.visibility,
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                if (isMine) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onEditClick != null) {
                            IconButton(
                                onClick = onEditClick,
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Editar publicación",
                                    tint = extras.neonSecondary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Eliminar publicación",
                                tint = TextMuted
                            )
                        }
                    }
                } else {
                    TextButton(
                        onClick = { onFollowClick(author.id) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(
                            text = if (item.isFollowingAuthor) "Siguiendo" else "+ Seguir",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (item.isFollowingAuthor) TextSecondary else extras.neonSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Category & Game Tag Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = extras.neonPrimary.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, extras.neonPrimary.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = PostCategory.fromKey(post.category).label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = extras.neonGlow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CarbonHighest,
                    border = BorderStroke(1.dp, CarbonBorder)
                ) {
                    Text(
                        text = post.gameTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = extras.neonSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (post.title.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }

            if (post.content.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }

            // Hashtags
            if (post.hashtags.isNotBlank()) {
                val tags = post.hashtags.split(" ", ",").map { it.trim() }.filter { it.startsWith("#") }
                if (tags.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.take(4).forEach { tag ->
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelMedium,
                                color = extras.neonSecondary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onHashtagClick(tag) }
                                    .padding(vertical = 2.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }
            }

            // Media Attachment
            if (post.mediaType != "NONE") {
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compactMode) 170.dp else 210.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, CarbonBorder, RoundedCornerShape(12.dp))
                ) {
                    when (post.mediaType) {
                        "URI" -> AsyncImage(
                            model = post.mediaUri,
                            contentDescription = "Imagen compartida en la publicación",
                            modifier = Modifier.fillMaxWidth().height(210.dp),
                            contentScale = ContentScale.Crop
                        )
                        "DRAWABLE_ARENA" -> Image(
                            painter = painterResource(id = R.drawable.img_post_arena),
                            contentDescription = "Escena de combate esports",
                            modifier = Modifier.fillMaxWidth().height(210.dp),
                            contentScale = ContentScale.Crop
                        )
                        "DRAWABLE_CYBER" -> Image(
                            painter = painterResource(id = R.drawable.img_banner_cyber),
                            contentDescription = "Setup gamer RGB",
                            modifier = Modifier.fillMaxWidth().height(210.dp),
                            contentScale = ContentScale.Crop
                        )
                        else -> Image(
                            painter = painterResource(id = R.drawable.img_hero_forge),
                            contentDescription = "Arte PlayForja",
                            modifier = Modifier.fillMaxWidth().height(210.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Interactive Poll Widget
            if (post.pollQuestion.isNotBlank() && post.pollOptionsCsv.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                val options = post.pollOptionsCsv.split("||").filter { it.isNotBlank() }
                val votes = post.pollVotesCsv.split(",").map { it.toIntOrNull() ?: 0 }
                val totalVotes = votes.sum().coerceAtLeast(1)
                val userVote = item.pollVoteIndex

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ObsidianBg,
                    border = BorderStroke(1.dp, CarbonBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = post.pollQuestion,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        options.forEachIndexed { idx, opt ->
                            val count = votes.getOrElse(idx) { 0 }
                            val ratio = if (votes.sum() == 0) 0f else count.toFloat() / totalVotes
                            val animatedRatio by animateFloatAsState(
                                targetValue = if (userVote >= 0) ratio else 0f,
                                label = "poll_bar"
                            )
                            val isSelected = userVote == idx

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CarbonHighest)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) extras.neonPrimary else CarbonBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onVotePoll(idx) }
                            ) {
                                if (userVote >= 0) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .fillMaxWidth(animatedRatio.coerceIn(0f, 1f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(animatedRatio.coerceIn(0.02f, 1f))
                                                .height(44.dp)
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(
                                                            extras.neonPrimary.copy(alpha = if (isSelected) 0.35f else 0.16f),
                                                            extras.neonSecondary.copy(alpha = 0.12f)
                                                        )
                                                    )
                                                )
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                tint = extras.neonPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = opt,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                    if (userVote >= 0) {
                                        Text(
                                            text = "${(ratio * 100).toInt()}%",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isSelected) extras.neonGlow else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${votes.sum()} votos • Toca una opción para votar",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            // LFG Squad Recruitment Widget
            if (post.lfgSlotsTotal > 0) {
                Spacer(Modifier.height(12.dp))
                val isFull = post.lfgSlotsFilled >= post.lfgSlotsTotal && !item.lfgJoined
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ObsidianBg,
                    border = BorderStroke(1.dp, extras.neonSecondary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Groups,
                                    contentDescription = null,
                                    tint = extras.neonSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "ESCUADRÓN LFG • ${post.lfgSlotsFilled}/${post.lfgSlotsTotal}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = extras.neonSecondary
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (isFull) "Escuadrón completo" else "Plazas abiertas para jugar ya",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                item.lfgJoined -> VenomLime.copy(alpha = 0.2f)
                                isFull -> CarbonHighest
                                else -> extras.neonSecondary
                            },
                            border = if (item.lfgJoined) BorderStroke(1.dp, VenomLime) else null,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = !isFull, onClick = onJoinLfg)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (item.lfgJoined) Icons.Filled.CheckCircle else Icons.Filled.GroupAdd,
                                    contentDescription = null,
                                    tint = if (item.lfgJoined) VenomLime else ObsidianBg,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = when {
                                        item.lfgJoined -> "En Squad"
                                        isFull -> "Lleno"
                                        else -> "Unirme"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (item.lfgJoined) VenomLime else ObsidianBg
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Action Bar
            val likeColor by animateColorAsState(
                targetValue = if (item.isLiked) extras.neonPrimary else TextSecondary,
                label = "like_color"
            )
            val likeScale by animateFloatAsState(
                targetValue = if (item.isLiked) 1.15f else 1.0f,
                animationSpec = spring(),
                label = "like_scale"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onLikeClick)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (item.isLiked) Icons.Filled.LocalFireDepartment else Icons.Outlined.LocalFireDepartment,
                        contentDescription = "Dar GG",
                        tint = likeColor,
                        modifier = Modifier.size(20.dp).scale(likeScale)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "${post.likesCount} GG",
                        style = MaterialTheme.typography.labelMedium,
                        color = likeColor
                    )
                }

                Row(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onCommentClick)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comentarios",
                        tint = TextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "${post.commentsCount}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                Row(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onRepostClick)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Repeat,
                        contentDescription = "Re-Forjar",
                        tint = if (item.isReposted) VenomLime else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "${post.repostsCount}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (item.isReposted) VenomLime else TextSecondary
                    )
                }

                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = if (item.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Guardar en bóveda",
                        tint = if (item.isBookmarked) extras.neonSecondary else TextSecondary
                    )
                }
            }
        }
    }
}
