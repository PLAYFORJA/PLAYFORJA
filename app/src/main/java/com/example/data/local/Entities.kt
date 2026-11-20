package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GamerStatus(val key: String, val label: String) {
    ONLINE("ONLINE", "En línea"),
    INGAME("INGAME", "En partida"),
    STREAMING("STREAMING", "Transmitiendo"),
    DND("DND", "No molestar"),
    INVISIBLE("INVISIBLE", "Invisible");

    companion object {
        fun fromKey(key: String?): GamerStatus =
            entries.find { it.key == key } ?: ONLINE
    }
}

enum class FrameStyle(val key: String, val label: String) {
    EMBER("EMBER", "Llama Forja"),
    CYAN("CYAN", "Pulso Ciber"),
    PLASMA("PLASMA", "Aura Plasma"),
    LIME("LIME", "Circuito Venom"),
    GOLD("GOLD", "Corona Campeón"),
    CRIMSON("CRIMSON", "Furia Carmesí"),
    HOLO("HOLO", "Prisma Holo"),
    VOID("VOID", "Acero Void");

    companion object {
        fun fromKey(key: String?): FrameStyle =
            entries.find { it.key == key } ?: EMBER
    }
}

enum class ProfileBgStyle(val key: String, val label: String) {
    OBSIDIAN("OBSIDIAN", "Obsidiana Pura"),
    CYBER_GRID("CYBER_GRID", "Matriz Ciber"),
    MAGMA("MAGMA", "Forja Magma"),
    VOID_NEBULA("VOID_NEBULA", "Nebulosa Void"),
    TOXIC_CIRCUIT("TOXIC_CIRCUIT", "Circuito Venom"),
    GOLD_ROYAL("GOLD_ROYAL", "Arena Dorada");

    companion object {
        fun fromKey(key: String?): ProfileBgStyle =
            entries.find { it.key == key } ?: OBSIDIAN
    }
}

enum class PostCategory(val key: String, val label: String) {
    GENERAL("GENERAL", "Libre"),
    CLIP("CLIP", "Clips & Capturas"),
    DEBATE("DEBATE", "Debates"),
    LFG("LFG", "Buscar Squad"),
    SETUP("SETUP", "Setups RGB"),
    ARTE("ARTE", "Arte & Mods"),
    MEME("MEME", "Humor Gamer");

    companion object {
        fun fromKey(key: String?): PostCategory =
            entries.find { it.key == key } ?: GENERAL
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val handle: String,
    val displayName: String,
    val email: String,
    val passwordHash: String,
    val bio: String = "Forjando leyendas en PlayForja.",
    val customTitle: String = "Forjador Novato",
    val platform: String = "PC",
    val favoriteGame: String = "Valorant",
    val avatarPreset: String = "preset_knight",
    val bannerPreset: String = "banner_forge",
    val frameStyle: String = FrameStyle.EMBER.key,
    val accentTheme: String = "FORJA",
    val status: String = GamerStatus.ONLINE.key,
    val hardwareSpecs: String = "PC Gamer • Teclado Mecánico • 144Hz",
    val socialLinks: String = "Discord: disponible • Twitch: en vivo pronto",
    val clanTag: String = "FORJA",
    val level: Int = 1,
    val xp: Int = 120,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val badgesCsv: String = "Fundador Local,Primer Salto",
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val profileBgStyle: String = ProfileBgStyle.OBSIDIAN.key,
    val favoriteGamesCsv: String = "valorant",
    val bioMotto: String = "Sin límites en la arena.",
    val playSchedule: String = "Noches y fines de semana"
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: Long,
    val category: String = PostCategory.GENERAL.key,
    val gameTag: String = "Multiplataforma",
    val title: String = "",
    val content: String,
    val mediaType: String = "NONE", // NONE, DRAWABLE_ARENA, DRAWABLE_CYBER, DRAWABLE_HERO, URI
    val mediaUri: String = "",
    val hashtags: String = "",
    val visibility: String = "GLOBAL", // GLOBAL, CLAN, SEGUIDORES
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val repostsCount: Int = 0,
    val pollQuestion: String = "",
    val pollOptionsCsv: String = "", // separated by ||
    val pollVotesCsv: String = "", // comma separated ints
    val lfgSlotsTotal: Int = 0,
    val lfgSlotsFilled: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "post_reactions", primaryKeys = ["userId", "postId"])
data class PostReactionEntity(
    val userId: Long,
    val postId: Long,
    val liked: Boolean = false,
    val reposted: Boolean = false,
    val bookmarked: Boolean = false,
    val pollVoteIndex: Int = -1,
    val lfgJoined: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorId: Long,
    val content: String,
    val likesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "follows", primaryKeys = ["followerId", "targetUserId"])
data class FollowEntity(
    val followerId: Long,
    val targetUserId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "clans")
data class ClanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val tag: String,
    val description: String,
    val gameFocus: String,
    val bannerPreset: String = "banner_forge",
    val membersCount: Int = 1,
    val isJoined: Boolean = false,
    val leaderName: String,
    val region: String = "Global / LATAM",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val participantUserId: Long = 0,
    val participantName: String,
    val participantHandle: String,
    val participantAvatarPreset: String,
    val participantFrame: String,
    val participantStatus: String,
    val isClanChat: Boolean = false,
    val lastMessage: String,
    val lastTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threadId: Long,
    val senderId: Long,
    val senderName: String,
    val content: String,
    val attachmentTag: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val type: String, // GG, COMMENT, FOLLOW, CLAN, BADGE
    val actorName: String,
    val message: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class PostWithDetails(
    val post: PostEntity,
    val author: UserEntity,
    val reaction: PostReactionEntity?,
    val isFollowingAuthor: Boolean
) {
    val isLiked: Boolean get() = reaction?.liked == true
    val isReposted: Boolean get() = reaction?.reposted == true
    val isBookmarked: Boolean get() = reaction?.bookmarked == true
    val pollVoteIndex: Int get() = reaction?.pollVoteIndex ?: -1
    val lfgJoined: Boolean get() = reaction?.lfgJoined == true
}

data class CommentWithAuthor(
    val comment: CommentEntity,
    val author: UserEntity
)
