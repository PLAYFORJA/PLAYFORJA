package com.example.data.local

/**
 * Applies the visibility choice stored on a post before it enters a user's feed.
 * Unknown values fail closed instead of accidentally becoming public.
 */
internal object PostVisibilityPolicy {
    fun canView(
        visibility: String,
        authorId: Long,
        authorClanTag: String,
        viewerId: Long,
        viewerClanTag: String?,
        followedUserIds: Set<Long>
    ): Boolean {
        if (authorId == viewerId) return true

        return when (visibility.uppercase()) {
            "GLOBAL" -> true
            "SEGUIDORES" -> followedUserIds.contains(authorId)
            "CLAN" -> !viewerClanTag.isNullOrBlank() &&
                viewerClanTag.equals(authorClanTag, ignoreCase = true)
            else -> false
        }
    }
}
