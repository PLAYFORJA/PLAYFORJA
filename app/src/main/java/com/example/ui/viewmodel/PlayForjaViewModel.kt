package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AuthResult
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatThreadEntity
import com.example.data.local.ClanEntity
import com.example.data.local.CommentWithAuthor
import com.example.data.local.NotificationEntity
import com.example.data.local.PlayForjaRepository
import com.example.data.local.PostWithDetails
import com.example.data.local.SessionManager
import com.example.data.local.UserEntity
import com.example.ui.theme.AccentTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlayForjaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val sessionManager = SessionManager(application)
    private val repository = PlayForjaRepository(db, sessionManager)

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents = _snackbarEvents.asSharedFlow()

    val activeUserId: StateFlow<Long?> = repository.activeUserIdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentUser: StateFlow<UserEntity?> = activeUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.observeUser(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val accentTheme: StateFlow<AccentTheme> = combine(
        currentUser,
        repository.accentThemeFlow
    ) { user, storedKey ->
        AccentTheme.fromKey(user?.accentTheme ?: storedKey)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccentTheme.FORJA)

    val compactCards: StateFlow<Boolean> = repository.compactCardsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posts: StateFlow<List<PostWithDetails>> = activeUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.observePostsForUser(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commentsByPost: StateFlow<Map<Long, List<CommentWithAuthor>>> = repository.observeComments()
        .map { list -> list.groupBy { it.comment.postId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val clans: StateFlow<List<ClanEntity>> = repository.allClansFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val threads: StateFlow<List<ChatThreadEntity>> = repository.allThreadsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messagesByThread: StateFlow<Map<Long, List<ChatMessageEntity>>> = repository.allMessagesFlow
        .map { list -> list.groupBy { it.threadId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val notifications: StateFlow<List<NotificationEntity>> = activeUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.observeNotifications(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followedUserIds: StateFlow<Set<Long>> = activeUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.observeFollows(id)
        }
        .map { list -> list.map { it.targetUserId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            _isInitialized.value = true
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun registerLocal(
        handle: String,
        displayName: String,
        email: String,
        password: String,
        platform: String,
        favoriteGame: String,
        avatarPreset: String
    ) {
        viewModelScope.launch {
            _authError.value = null
            when (val result = repository.registerLocalUser(
                handleRaw = handle,
                displayName = displayName,
                emailRaw = email,
                password = password,
                platform = platform,
                favoriteGame = favoriteGame,
                avatarPreset = avatarPreset
            )) {
                is AuthResult.Success -> _snackbarEvents.emit("¡Bienvenido a PlayForja, @${result.user.handle}!")
                is AuthResult.Error -> _authError.value = result.message
            }
        }
    }

    fun loginLocal(identifier: String, password: String) {
        viewModelScope.launch {
            _authError.value = null
            when (val result = repository.loginLocalUser(identifier, password)) {
                is AuthResult.Success -> _snackbarEvents.emit("Sesión iniciada como ${result.user.displayName}")
                is AuthResult.Error -> _authError.value = result.message
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun updateStatus(statusKey: String) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.updateStatus(uid, statusKey)
        }
    }

    fun setAccentTheme(themeKey: String) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.setAccentTheme(uid, themeKey)
            _snackbarEvents.emit("Tema neón actualizado")
        }
    }

    fun setCompactCards(enabled: Boolean) {
        viewModelScope.launch {
            repository.setCompactCards(enabled)
        }
    }

    fun updateProfile(
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
        clanTag: String,
        onSaved: () -> Unit = {}
    ) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.updateProfile(
                userId = uid,
                displayName = displayName,
                bio = bio,
                bioMotto = bioMotto,
                playSchedule = playSchedule,
                customTitle = customTitle,
                platform = platform,
                favoriteGame = favoriteGame,
                favoriteGamesCsv = favoriteGamesCsv,
                avatarPreset = avatarPreset,
                bannerPreset = bannerPreset,
                profileBgStyle = profileBgStyle,
                frameStyle = frameStyle,
                accentTheme = accentTheme,
                hardwareSpecs = hardwareSpecs,
                socialLinks = socialLinks,
                clanTag = clanTag
            )
            _snackbarEvents.emit("Perfil gamer personalizado (+40 XP)")
            onSaved()
        }
    }

    fun createPost(
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
        lfgSlotsTotal: Int,
        onCreated: () -> Unit = {}
    ) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.createPost(
                authorId = uid,
                category = category,
                gameTag = gameTag,
                title = title,
                content = content,
                mediaType = mediaType,
                mediaUri = mediaUri,
                hashtags = hashtags,
                visibility = visibility,
                pollQuestion = pollQuestion,
                pollOptions = pollOptions,
                lfgSlotsTotal = lfgSlotsTotal
            )
            _snackbarEvents.emit("¡Publicado en PlayForja! (+50 XP)")
            onCreated()
        }
    }

    fun editPost(
        postId: Long,
        category: String,
        gameTag: String,
        title: String,
        content: String,
        mediaType: String,
        mediaUri: String,
        hashtags: String,
        visibility: String,
        onEdited: () -> Unit = {}
    ) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.editPost(
                postId = postId,
                requesterId = uid,
                category = category,
                gameTag = gameTag,
                title = title,
                content = content,
                mediaType = mediaType,
                mediaUri = mediaUri,
                hashtags = hashtags,
                visibility = visibility
            )
            _snackbarEvents.emit("Publicación actualizada")
            onEdited()
        }
    }

    fun deletePost(postId: Long) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.deletePost(postId, uid)
            _snackbarEvents.emit("Publicación eliminada")
        }
    }

    fun toggleLike(postId: Long) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch { repository.toggleLike(uid, postId) }
    }

    fun toggleRepost(postId: Long) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.toggleRepost(uid, postId)
            _snackbarEvents.emit("Re-Forjado en tu actividad")
        }
    }

    fun toggleBookmark(postId: Long) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.toggleBookmark(uid, postId)
            _snackbarEvents.emit("Bóveda actualizada")
        }
    }

    fun votePoll(postId: Long, optionIndex: Int) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch { repository.votePoll(uid, postId, optionIndex) }
    }

    fun toggleJoinLfg(postId: Long) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.toggleJoinLfg(uid, postId)
            _snackbarEvents.emit("Estado de escuadrón actualizado")
        }
    }

    fun addComment(postId: Long, content: String) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch { repository.addComment(uid, postId, content) }
    }

    fun likeComment(commentId: Long) {
        viewModelScope.launch { repository.likeComment(commentId) }
    }

    fun toggleFollow(targetUserId: Long) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch { repository.toggleFollow(uid, targetUserId) }
    }

    fun toggleJoinClan(clanId: Long) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.toggleJoinClan(uid, clanId)
            _snackbarEvents.emit("Membresía de clan actualizada")
        }
    }

    fun createClan(name: String, tag: String, description: String, gameFocus: String, region: String) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.createClan(uid, name, tag, description, gameFocus, region)
            _snackbarEvents.emit("¡Clan [$tag] forjado! (+75 XP)")
        }
    }

    fun openDmWithUser(targetUser: UserEntity, onOpened: (Long) -> Unit) {
        viewModelScope.launch {
            val threadId = repository.openOrCreateDmThread(targetUser)
            onOpened(threadId)
        }
    }

    fun markThreadRead(threadId: Long) {
        viewModelScope.launch { repository.markThreadRead(threadId) }
    }

    fun sendMessage(threadId: Long, content: String, attachmentTag: String = "") {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.sendChatMessage(threadId, uid, content, attachmentTag)
        }
    }

    fun markAllNotificationsRead() {
        val uid = activeUserId.value ?: return
        viewModelScope.launch { repository.markAllNotificationsRead(uid) }
    }
}
