package com.example.data.local

import com.example.data.local.LevelSystem.levelForXp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class PlayForjaRepository(
    private val db: AppDatabase,
    private val sessionManager: SessionManager
) {
    private val userDao = db.userDao()
    private val postDao = db.postDao()
    private val reactionDao = db.postReactionDao()
    private val commentDao = db.commentDao()
    private val followDao = db.followDao()
    private val clanDao = db.clanDao()
    private val chatDao = db.chatDao()
    private val notificationDao = db.notificationDao()

    val activeUserIdFlow: Flow<Long?> = sessionManager.activeUserIdFlow
    val accentThemeFlow: Flow<String> = sessionManager.accentThemeFlow
    val compactCardsFlow: Flow<Boolean> = sessionManager.compactCardsFlow
    val allUsersFlow: Flow<List<UserEntity>> = userDao.observeAllUsers()
    val allClansFlow: Flow<List<ClanEntity>> = clanDao.observeAllClans()
    val allThreadsFlow: Flow<List<ChatThreadEntity>> = chatDao.observeAllThreads()
    val allMessagesFlow: Flow<List<ChatMessageEntity>> = chatDao.observeAllMessages()

    suspend fun ensureSeeded() {
        SeedData.populateIfEmpty(db)
    }

    fun observeUser(userId: Long): Flow<UserEntity?> = userDao.observeUserById(userId)

    fun observePostsForUser(currentUserId: Long): Flow<List<PostWithDetails>> =
        combine(
            postDao.observeAllPosts(),
            userDao.observeAllUsers(),
            reactionDao.observeReactionsForUser(currentUserId),
            followDao.observeFollowsByUser(currentUserId),
            userDao.observeUserById(currentUserId)
        ) { posts, users, reactions, follows, currentUser ->
            val userMap = users.associateBy { it.id }
            val reactionMap = reactions.associateBy { it.postId }
            val followedIds = follows.map { it.targetUserId }.toSet()

            posts.mapNotNull { post ->
                val author = userMap[post.authorId] ?: return@mapNotNull null
                if (!PostVisibilityPolicy.canView(
                        visibility = post.visibility,
                        authorId = author.id,
                        authorClanTag = author.clanTag,
                        viewerId = currentUserId,
                        viewerClanTag = currentUser?.clanTag,
                        followedUserIds = followedIds
                    )
                ) return@mapNotNull null

                PostWithDetails(
                    post = post,
                    author = author,
                    reaction = reactionMap[post.id],
                    isFollowingAuthor = followedIds.contains(author.id)
                )
            }
        }

    fun observeComments(): Flow<List<CommentWithAuthor>> =
        combine(
            commentDao.observeAllComments(),
            userDao.observeAllUsers()
        ) { comments, users ->
            val userMap = users.associateBy { it.id }
            comments.mapNotNull { comment ->
                val author = userMap[comment.authorId] ?: return@mapNotNull null
                CommentWithAuthor(comment, author)
            }
        }

    fun observeNotifications(userId: Long): Flow<List<NotificationEntity>> =
        notificationDao.observeNotificationsForUser(userId)

    fun observeFollows(userId: Long): Flow<List<FollowEntity>> =
        followDao.observeFollowsByUser(userId)

    suspend fun registerLocalUser(
        handleRaw: String,
        displayName: String,
        emailRaw: String,
        password: String,
        platform: String,
        favoriteGame: String,
        avatarPreset: String
    ): AuthResult {
        val cleanHandle = handleRaw.trim().removePrefix("@").lowercase().replace(" ", "_")
        val cleanName = displayName.trim()
        val cleanEmail = emailRaw.trim().lowercase()

        if (cleanHandle.length < 3) return AuthResult.Error("El GamerTag debe tener al menos 3 caracteres.")
        if (cleanName.length < 2) return AuthResult.Error("Ingresa tu nombre de jugador.")
        if (cleanEmail.length < 4) return AuthResult.Error("Ingresa un correo o identificador válido.")
        if (password.length < 4) return AuthResult.Error("La contraseña debe tener mínimo 4 caracteres.")

        if (userDao.getUserByHandle(cleanHandle) != null) {
            return AuthResult.Error("El GamerTag @$cleanHandle ya está en uso en este dispositivo.")
        }
        if (userDao.findByHandleOrEmail(cleanEmail) != null) {
            return AuthResult.Error("Ese correo ya está registrado localmente.")
        }

        val matchedGame = GameCatalog.findByTitleOrId(favoriteGame)
        val newUser = UserEntity(
            handle = cleanHandle,
            displayName = cleanName,
            email = cleanEmail,
            passwordHash = SecurityUtils.createStoredPassword(password),
            bio = "¡Nuevo en PlayForja! Listo para jugar ${matchedGame?.title ?: favoriteGame.ifBlank { "de todo" }} en $platform.",
            customTitle = "Forjador Libre",
            platform = platform,
            favoriteGame = matchedGame?.title ?: favoriteGame.ifBlank { "Valorant" },
            avatarPreset = avatarPreset,
            bannerPreset = "banner_forge",
            frameStyle = FrameStyle.EMBER.key,
            accentTheme = "FORJA",
            status = GamerStatus.ONLINE.key,
            hardwareSpecs = "$platform • Setup Personalizado",
            socialLinks = "PlayForja: @$cleanHandle",
            clanTag = "FORJA",
            level = 1,
            xp = 150,
            badgesCsv = "Cuenta Local,Pionero PlayForja",
            profileBgStyle = ProfileBgStyle.OBSIDIAN.key,
            favoriteGamesCsv = matchedGame?.id ?: "valorant",
            bioMotto = "Forjando mi propia leyenda.",
            playSchedule = "Flexible"
        )

        val userId = userDao.insertUser(newUser)
        val created = userDao.getUserById(userId) ?: return AuthResult.Error("No se pudo crear la cuenta.")

        notificationDao.insertNotification(
            NotificationEntity(
                userId = userId,
                type = "BADGE",
                actorName = "PlayForja HQ",
                message = "¡Cuenta @$cleanHandle forjada con éxito! +150 XP iniciales."
            )
        )

        sessionManager.setActiveUser(userId, created.accentTheme)
        return AuthResult.Success(created)
    }

    suspend fun loginLocalUser(identifierRaw: String, password: String): AuthResult {
        val identifier = identifierRaw.trim().removePrefix("@").lowercase()
        if (identifier.isBlank() || password.isBlank()) {
            return AuthResult.Error("Completa tu GamerTag/correo y contraseña.")
        }
        val user = userDao.findByHandleOrEmail(identifier)
            ?: return AuthResult.Error("No existe ninguna cuenta local con ese GamerTag o correo.")

        if (!SecurityUtils.verifyPassword(password, user.passwordHash)) {
            return AuthResult.Error("Contraseña incorrecta. Verifica tus datos.")
        }

        // Upgrade legacy SHA-256 hashes after a successful login without forcing a password reset.
        val authenticatedUser = if (SecurityUtils.needsRehash(user.passwordHash)) {
            user.copy(passwordHash = SecurityUtils.createStoredPassword(password)).also {
                userDao.updateUser(it)
            }
        } else {
            user
        }

        sessionManager.setActiveUser(authenticatedUser.id, authenticatedUser.accentTheme)
        return AuthResult.Success(authenticatedUser)
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    suspend fun setAccentTheme(userId: Long, themeKey: String) {
        sessionManager.setAccentTheme(themeKey)
        val user = userDao.getUserById(userId) ?: return
        userDao.updateUser(user.copy(accentTheme = themeKey))
    }

    suspend fun setCompactCards(enabled: Boolean) {
        sessionManager.setCompactCards(enabled)
    }

    suspend fun updateStatus(userId: Long, statusKey: String) {
        val user = userDao.getUserById(userId) ?: return
        userDao.updateUser(user.copy(status = statusKey))
    }

    suspend fun updateProfile(
        userId: Long,
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
    ) {
        val user = userDao.getUserById(userId) ?: return
        val badges = user.badgesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
        badges.add("Forjador Estético")
        val newXp = user.xp + 40
        userDao.updateUser(
            user.copy(
                displayName = displayName.trim().ifBlank { user.displayName },
                bio = bio.trim(),
                bioMotto = bioMotto.trim(),
                playSchedule = playSchedule.trim(),
                customTitle = customTitle.trim().ifBlank { user.customTitle },
                platform = platform,
                favoriteGame = favoriteGame.trim().ifBlank { user.favoriteGame },
                favoriteGamesCsv = favoriteGamesCsv.trim(),
                avatarPreset = avatarPreset,
                bannerPreset = bannerPreset,
                profileBgStyle = profileBgStyle,
                frameStyle = frameStyle,
                accentTheme = accentTheme,
                hardwareSpecs = hardwareSpecs.trim(),
                socialLinks = socialLinks.trim(),
                clanTag = clanTag.trim().uppercase().take(6).ifBlank { user.clanTag },
                xp = newXp,
                level = levelForXp(newXp),
                badgesCsv = badges.joinToString(",")
            )
        )
        sessionManager.setAccentTheme(accentTheme)
    }

    suspend fun createPost(
        authorId: Long,
        category: String,
        gameTag: String,
        title: String,
        content: String,
        mediaType: String,
        mediaUri: String,
        hashtags: String,
        visibility: String,
        pollQuestion: String,
        pollOptions: List<String>,
        lfgSlotsTotal: Int
    ) {
        val cleanOptions = pollOptions.map { it.trim() }.filter { it.isNotEmpty() }
        val votesCsv = cleanOptions.joinToString(",") { "0" }
        val post = PostEntity(
            authorId = authorId,
            category = category,
            gameTag = gameTag.trim().ifBlank { "Comunidad Libre" },
            title = title.trim(),
            content = content.trim(),
            mediaType = mediaType,
            mediaUri = mediaUri,
            hashtags = hashtags.trim(),
            visibility = visibility,
            pollQuestion = pollQuestion.trim(),
            pollOptionsCsv = cleanOptions.joinToString("||"),
            pollVotesCsv = votesCsv,
            lfgSlotsTotal = lfgSlotsTotal,
            lfgSlotsFilled = if (lfgSlotsTotal > 0) 1 else 0
        )
        postDao.insertPost(post)
        awardXp(authorId, 50, "Creador Activo")
    }

    suspend fun editPost(
        postId: Long,
        requesterId: Long,
        category: String,
        gameTag: String,
        title: String,
        content: String,
        mediaType: String,
        mediaUri: String,
        hashtags: String,
        visibility: String
    ) {
        val post = postDao.getPostById(postId) ?: return
        if (post.authorId != requesterId) return
        postDao.updatePost(
            post.copy(
                category = category,
                gameTag = gameTag.trim().ifBlank { post.gameTag },
                title = title.trim(),
                content = content.trim(),
                mediaType = mediaType,
                mediaUri = mediaUri,
                hashtags = hashtags.trim(),
                visibility = visibility
            )
        )
    }

    suspend fun deletePost(postId: Long, requesterId: Long) {
        val post = postDao.getPostById(postId) ?: return
        if (post.authorId == requesterId) {
            postDao.deletePostById(postId)
        }
    }

    suspend fun toggleLike(userId: Long, postId: Long) {
        val post = postDao.getPostById(postId) ?: return
        val existing = reactionDao.getReaction(userId, postId) ?: PostReactionEntity(userId, postId)
        val newLiked = !existing.liked
        reactionDao.upsertReaction(existing.copy(liked = newLiked))
        val delta = if (newLiked) 1 else -1
        postDao.updatePost(post.copy(likesCount = (post.likesCount + delta).coerceAtLeast(0)))
        if (newLiked) awardXp(userId, 5, null)
    }

    suspend fun toggleRepost(userId: Long, postId: Long) {
        val post = postDao.getPostById(postId) ?: return
        val existing = reactionDao.getReaction(userId, postId) ?: PostReactionEntity(userId, postId)
        val newReposted = !existing.reposted
        reactionDao.upsertReaction(existing.copy(reposted = newReposted))
        val delta = if (newReposted) 1 else -1
        postDao.updatePost(post.copy(repostsCount = (post.repostsCount + delta).coerceAtLeast(0)))
    }

    suspend fun toggleBookmark(userId: Long, postId: Long) {
        val existing = reactionDao.getReaction(userId, postId) ?: PostReactionEntity(userId, postId)
        reactionDao.upsertReaction(existing.copy(bookmarked = !existing.bookmarked))
    }

    suspend fun votePoll(userId: Long, postId: Long, optionIndex: Int) {
        val post = postDao.getPostById(postId) ?: return
        val existing = reactionDao.getReaction(userId, postId) ?: PostReactionEntity(userId, postId)
        if (existing.pollVoteIndex == optionIndex) return

        val votes = post.pollVotesCsv.split(",").mapNotNull { it.toIntOrNull() }.toMutableList()
        if (optionIndex !in votes.indices) return

        if (existing.pollVoteIndex in votes.indices) {
            votes[existing.pollVoteIndex] = (votes[existing.pollVoteIndex] - 1).coerceAtLeast(0)
        }
        votes[optionIndex] = votes[optionIndex] + 1

        reactionDao.upsertReaction(existing.copy(pollVoteIndex = optionIndex))
        postDao.updatePost(post.copy(pollVotesCsv = votes.joinToString(",")))
        awardXp(userId, 10, null)
    }

    suspend fun toggleJoinLfg(userId: Long, postId: Long) {
        val post = postDao.getPostById(postId) ?: return
        if (post.lfgSlotsTotal <= 0) return
        val existing = reactionDao.getReaction(userId, postId) ?: PostReactionEntity(userId, postId)
        val joining = !existing.lfgJoined
        if (joining && post.lfgSlotsFilled >= post.lfgSlotsTotal) return

        val newFilled = if (joining) {
            (post.lfgSlotsFilled + 1).coerceAtMost(post.lfgSlotsTotal)
        } else {
            (post.lfgSlotsFilled - 1).coerceAtLeast(1)
        }
        reactionDao.upsertReaction(existing.copy(lfgJoined = joining))
        postDao.updatePost(post.copy(lfgSlotsFilled = newFilled))
        if (joining) {
            awardXp(userId, 25, "Jugador de Equipo")
        }
    }

    suspend fun addComment(userId: Long, postId: Long, content: String) {
        val clean = content.trim()
        if (clean.isEmpty()) return
        val post = postDao.getPostById(postId) ?: return
        commentDao.insertComment(
            CommentEntity(postId = postId, authorId = userId, content = clean)
        )
        postDao.updatePost(post.copy(commentsCount = post.commentsCount + 1))
        awardXp(userId, 15, null)
    }

    suspend fun likeComment(commentId: Long) {
        commentDao.incrementCommentLike(commentId)
    }

    suspend fun toggleFollow(currentUserId: Long, targetUserId: Long) {
        if (currentUserId == targetUserId) return
        val currentUser = userDao.getUserById(currentUserId) ?: return
        val targetUser = userDao.getUserById(targetUserId) ?: return
        val existing = followDao.getFollow(currentUserId, targetUserId)

        if (existing == null) {
            followDao.insertFollow(FollowEntity(currentUserId, targetUserId))
            userDao.updateUser(currentUser.copy(followingCount = currentUser.followingCount + 1))
            userDao.updateUser(targetUser.copy(followersCount = targetUser.followersCount + 1))
            awardXp(currentUserId, 10, null)
        } else {
            followDao.deleteFollow(currentUserId, targetUserId)
            userDao.updateUser(currentUser.copy(followingCount = (currentUser.followingCount - 1).coerceAtLeast(0)))
            userDao.updateUser(targetUser.copy(followersCount = (targetUser.followersCount - 1).coerceAtLeast(0)))
        }
    }

    suspend fun toggleJoinClan(userId: Long, clanId: Long) {
        val clan = clanDao.getClanById(clanId) ?: return
        val joining = !clan.isJoined
        val delta = if (joining) 1 else -1
        clanDao.updateClan(
            clan.copy(
                isJoined = joining,
                membersCount = (clan.membersCount + delta).coerceAtLeast(1)
            )
        )
        if (joining) {
            val user = userDao.getUserById(userId)
            if (user != null) {
                userDao.updateUser(user.copy(clanTag = clan.tag))
            }
            awardXp(userId, 30, "Miembro de Clan")
        }
    }

    suspend fun createClan(
        userId: Long,
        name: String,
        tag: String,
        description: String,
        gameFocus: String,
        region: String
    ) {
        val user = userDao.getUserById(userId) ?: return
        val cleanTag = tag.trim().uppercase().take(5).ifBlank { "CLAN" }
        clanDao.insertClan(
            ClanEntity(
                name = name.trim(),
                tag = cleanTag,
                description = description.trim(),
                gameFocus = gameFocus.trim().ifBlank { "Multiplataforma" },
                bannerPreset = "banner_forge",
                membersCount = 1,
                isJoined = true,
                leaderName = user.displayName,
                region = region.trim().ifBlank { "Global" }
            )
        )
        userDao.updateUser(user.copy(clanTag = cleanTag))
        awardXp(userId, 75, "Fundador de Clan")
    }

    suspend fun openOrCreateDmThread(targetUser: UserEntity): Long {
        val existing = chatDao.getThreadByParticipantUserId(targetUser.id)
        if (existing != null) return existing.id
        return chatDao.insertThread(
            ChatThreadEntity(
                participantUserId = targetUser.id,
                participantName = targetUser.displayName,
                participantHandle = targetUser.handle,
                participantAvatarPreset = targetUser.avatarPreset,
                participantFrame = targetUser.frameStyle,
                participantStatus = targetUser.status,
                isClanChat = false,
                lastMessage = "Inicia una conversación con @${targetUser.handle}",
                unreadCount = 0
            )
        )
    }

    suspend fun markThreadRead(threadId: Long) {
        val thread = chatDao.getThreadById(threadId) ?: return
        if (thread.unreadCount > 0) {
            chatDao.updateThread(thread.copy(unreadCount = 0))
        }
    }

    suspend fun sendChatMessage(threadId: Long, senderId: Long, content: String, attachmentTag: String = "") {
        val clean = content.trim()
        if (clean.isEmpty()) return
        val sender = userDao.getUserById(senderId) ?: return
        val thread = chatDao.getThreadById(threadId) ?: return
        val now = System.currentTimeMillis()

        chatDao.insertMessage(
            ChatMessageEntity(
                threadId = threadId,
                senderId = senderId,
                senderName = sender.displayName,
                content = clean,
                attachmentTag = attachmentTag,
                timestamp = now
            )
        )
        chatDao.updateThread(
            thread.copy(
                lastMessage = "${sender.displayName}: $clean",
                lastTimestamp = now,
                unreadCount = 0
            )
        )
        awardXp(senderId, 8, null)
    }

    suspend fun markAllNotificationsRead(userId: Long) {
        notificationDao.markAllRead(userId)
    }

    private suspend fun awardXp(userId: Long, amount: Int, unlockBadge: String?) {
        val user = userDao.getUserById(userId) ?: return
        val newXp = user.xp + amount
        val newLevel = levelForXp(newXp)
        val badges = user.badgesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
        if (unlockBadge != null) badges.add(unlockBadge)
        userDao.updateUser(
            user.copy(
                xp = newXp,
                level = newLevel,
                badgesCsv = badges.joinToString(",")
            )
        )
    }
}
