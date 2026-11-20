package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.PostCategory
import com.example.data.local.PostWithDetails
import com.example.ui.components.AppDestination
import com.example.ui.components.PlayForjaDrawerContent
import com.example.ui.screens.AccountSwitcherDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ClansAndLfgScreen
import com.example.ui.screens.CommentsSheet
import com.example.ui.screens.CreatePostSheet
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PlayForjaTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PlayForjaViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: PlayForjaViewModel = viewModel()
            val accentTheme by viewModel.accentTheme.collectAsStateWithLifecycle()

            PlayForjaTheme(accentTheme = accentTheme) {
                PlayForjaApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayForjaApp(viewModel: PlayForjaViewModel) {
    val isInitialized by viewModel.isInitialized.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val commentsByPost by viewModel.commentsByPost.collectAsStateWithLifecycle()
    val clans by viewModel.clans.collectAsStateWithLifecycle()
    val threads by viewModel.threads.collectAsStateWithLifecycle()
    val messagesByThread by viewModel.messagesByThread.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val followedUserIds by viewModel.followedUserIds.collectAsStateWithLifecycle()
    val compactCards by viewModel.compactCards.collectAsStateWithLifecycle()
    val accentTheme by viewModel.accentTheme.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    val extras = LocalPlayForjaExtras.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (!isInitialized) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = extras.neonPrimary)
        }
        return
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(
            existingAccounts = allUsers,
            authError = authError,
            onClearError = { viewModel.clearAuthError() },
            onLogin = { identifier, password -> viewModel.loginLocal(identifier, password) },
            onRegister = { handle, name, email, pass, platform, game, avatar ->
                viewModel.registerLocal(handle, name, email, pass, platform, game, avatar)
            },
            onQuickSwitch = { userId -> viewModel.switchAccount(userId) }
        )
        return
    }

    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.FEED) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedChatThreadId by rememberSaveable { mutableStateOf<Long?>(null) }
    var viewingProfileUserId by rememberSaveable { mutableStateOf<Long?>(null) }
    var isEditingProfile by rememberSaveable { mutableStateOf(false) }

    var showCreatePostSheet by remember { mutableStateOf(false) }
    var editingPost by remember { mutableStateOf<com.example.data.local.PostEntity?>(null) }
    var composerInitialCategory by remember { mutableStateOf(PostCategory.GENERAL.key) }
    var activeCommentPostId by remember { mutableStateOf<Long?>(null) }
    var showAccountSwitcher by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val unreadMessages = remember(threads) { threads.sumOf { it.unreadCount } }
    val unreadNotifications = remember(notifications) { notifications.count { !it.isRead } }
    val bookmarkedPosts = remember(posts) { posts.filter { it.isBookmarked } }
    val lfgPosts = remember(posts) {
        posts.filter { it.post.category == PostCategory.LFG.key || it.post.lfgSlotsTotal > 0 }
    }

    // Handle secondary back navigation when drawer is open or sub-destination is active
    if (currentDestination != AppDestination.FEED && viewingProfileUserId == null && !isEditingProfile) {
        BackHandler { currentDestination = AppDestination.FEED }
    }

    if (isEditingProfile) {
        EditProfileScreen(
            user = user,
            onBack = { isEditingProfile = false },
            onSave = { name, bio, bioMotto, schedule, title, plat, game, gamesCsv, avatar, banner, profileBg, frame, theme, hw, links, clan ->
                viewModel.updateProfile(
                    displayName = name,
                    bio = bio,
                    bioMotto = bioMotto,
                    playSchedule = schedule,
                    customTitle = title,
                    platform = plat,
                    favoriteGame = game,
                    favoriteGamesCsv = gamesCsv,
                    avatarPreset = avatar,
                    bannerPreset = banner,
                    profileBgStyle = profileBg,
                    frameStyle = frame,
                    accentTheme = theme,
                    hardwareSpecs = hw,
                    socialLinks = links,
                    clanTag = clan,
                    onSaved = { isEditingProfile = false }
                )
            }
        )
        return
    }

    val viewingOtherUser = viewingProfileUserId?.let { id -> allUsers.find { it.id == id } }
    if (viewingOtherUser != null) {
        ProfileScreen(
            profileUser = viewingOtherUser,
            currentUser = user,
            allPosts = posts,
            isFollowing = followedUserIds.contains(viewingOtherUser.id),
            isSecondaryView = true,
            onBack = { viewingProfileUserId = null },
            onEditProfile = {
                viewingProfileUserId = null
                isEditingProfile = true
            },
            onToggleFollow = { viewModel.toggleFollow(it) },
            onOpenDm = { target ->
                viewModel.openDmWithUser(target) { threadId ->
                    viewingProfileUserId = null
                    selectedChatThreadId = threadId
                    currentDestination = AppDestination.MESSAGES
                }
            },
            onAuthorClick = { uid ->
                if (uid == user.id) {
                    viewingProfileUserId = null
                    currentDestination = AppDestination.PROFILE
                } else {
                    viewingProfileUserId = uid
                }
            },
            onLikeClick = { viewModel.toggleLike(it) },
            onCommentClick = { activeCommentPostId = it.post.id },
            onRepostClick = { viewModel.toggleRepost(it) },
            onBookmarkClick = { viewModel.toggleBookmark(it) },
            onVotePoll = { pid, idx -> viewModel.votePoll(pid, idx) },
            onJoinLfg = { viewModel.toggleJoinLfg(it) },
            onDeletePost = { viewModel.deletePost(it) }
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PlayForjaDrawerContent(
                user = user,
                currentDestination = currentDestination,
                unreadMessages = unreadMessages,
                unreadNotifications = unreadNotifications,
                savedPostsCount = bookmarkedPosts.size,
                onNavigate = { dest ->
                    currentDestination = dest
                    if (dest != AppDestination.MESSAGES) selectedChatThreadId = null
                    scope.launch { drawerState.close() }
                },
                onStatusChange = { statusKey -> viewModel.updateStatus(statusKey) },
                onOpenAccountSwitcher = {
                    scope.launch { drawerState.close() }
                    showAccountSwitcher = true
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    viewModel.logout()
                }
            )
        }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpandedWidth = maxWidth >= 840.dp

            Row(modifier = Modifier.fillMaxSize().background(ObsidianBg)) {
                if (isExpandedWidth) {
                    PlayForjaNavigationRail(
                        currentDestination = currentDestination,
                        unreadMessages = unreadMessages,
                        onSelect = { dest ->
                            currentDestination = dest
                            if (dest != AppDestination.MESSAGES) selectedChatThreadId = null
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = ObsidianBg,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = CarbonSurface,
                                titleContentColor = TextPrimary
                            ),
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("open_drawer_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Abrir menú desplegable",
                                        tint = TextPrimary
                                    )
                                }
                            },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = extras.neonPrimary.copy(alpha = 0.16f),
                                        border = BorderStroke(1.dp, extras.neonPrimary)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Bolt,
                                            contentDescription = null,
                                            tint = extras.neonPrimary,
                                            modifier = Modifier.padding(4.dp).size(16.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = if (currentDestination == AppDestination.FEED) "PLAYFORJA" else currentDestination.label,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { currentDestination = AppDestination.NOTIFICATIONS },
                                    modifier = Modifier.testTag("topbar_notifications_button")
                                ) {
                                    BadgedBox(
                                        badge = {
                                            if (unreadNotifications > 0) {
                                                Badge(containerColor = extras.neonPrimary, contentColor = ObsidianBg) {
                                                    Text(unreadNotifications.toString())
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Notifications,
                                            contentDescription = "Notificaciones",
                                            tint = if (currentDestination == AppDestination.NOTIFICATIONS) extras.neonPrimary else TextSecondary
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        if (!isExpandedWidth) {
                            PlayForjaBottomBar(
                                currentDestination = currentDestination,
                                unreadMessages = unreadMessages,
                                onSelect = { dest ->
                                    currentDestination = dest
                                    if (dest != AppDestination.MESSAGES) selectedChatThreadId = null
                                }
                            )
                        }
                    },
                    floatingActionButton = {
                        if (currentDestination == AppDestination.FEED || currentDestination == AppDestination.EXPLORE) {
                            FloatingActionButton(
                                onClick = {
                                    composerInitialCategory = PostCategory.GENERAL.key
                                    showCreatePostSheet = true
                                },
                                containerColor = extras.neonPrimary,
                                contentColor = ObsidianBg,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("fab_create_post")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Crear publicación")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentDestination) {
                            AppDestination.FEED -> FeedScreen(
                                currentUser = user,
                                posts = posts,
                                compactMode = compactCards,
                                searchQuery = searchQuery,
                                onSearchChange = { searchQuery = it },
                                onOpenComposer = {
                                    composerInitialCategory = PostCategory.GENERAL.key
                                    showCreatePostSheet = true
                                },
                                onAuthorClick = { uid ->
                                    if (uid == user.id) currentDestination = AppDestination.PROFILE
                                    else viewingProfileUserId = uid
                                },
                                onFollowClick = { viewModel.toggleFollow(it) },
                                onLikeClick = { viewModel.toggleLike(it) },
                                onCommentClick = { activeCommentPostId = it.post.id },
                                onRepostClick = { viewModel.toggleRepost(it) },
                                onBookmarkClick = { viewModel.toggleBookmark(it) },
                                onVotePoll = { pid, idx -> viewModel.votePoll(pid, idx) },
                                onJoinLfg = { viewModel.toggleJoinLfg(it) },
                                onDeletePost = { viewModel.deletePost(it) },
                                onEditPost = {
                                    editingPost = it.post
                                    showCreatePostSheet = true
                                }
                            )

                            AppDestination.EXPLORE -> ExploreScreen(
                                currentUser = user,
                                users = allUsers,
                                posts = posts,
                                followedUserIds = followedUserIds,
                                onHashtagSelected = { tag ->
                                    searchQuery = tag
                                    currentDestination = AppDestination.FEED
                                },
                                onUserClick = { uid ->
                                    if (uid == user.id) currentDestination = AppDestination.PROFILE
                                    else viewingProfileUserId = uid
                                },
                                onFollowClick = { viewModel.toggleFollow(it) },
                                onOpenClans = { currentDestination = AppDestination.CLANS }
                            )

                            AppDestination.CLANS -> ClansAndLfgScreen(
                                currentUser = user,
                                clans = clans,
                                lfgPosts = lfgPosts,
                                onToggleJoinClan = { viewModel.toggleJoinClan(it) },
                                onCreateClan = { n, t, d, g, r -> viewModel.createClan(n, t, d, g, r) },
                                onOpenLfgComposer = {
                                    composerInitialCategory = PostCategory.LFG.key
                                    showCreatePostSheet = true
                                },
                                onAuthorClick = { uid ->
                                    if (uid == user.id) currentDestination = AppDestination.PROFILE
                                    else viewingProfileUserId = uid
                                },
                                onFollowClick = { viewModel.toggleFollow(it) },
                                onLikeClick = { viewModel.toggleLike(it) },
                                onCommentClick = { activeCommentPostId = it.post.id },
                                onRepostClick = { viewModel.toggleRepost(it) },
                                onBookmarkClick = { viewModel.toggleBookmark(it) },
                                onVotePoll = { pid, idx -> viewModel.votePoll(pid, idx) },
                                onJoinLfg = { viewModel.toggleJoinLfg(it) },
                                onDeletePost = { viewModel.deletePost(it) }
                            )

                            AppDestination.MESSAGES -> MessagesScreen(
                                currentUser = user,
                                availableGamers = allUsers,
                                threads = threads,
                                messagesByThread = messagesByThread,
                                selectedThreadId = selectedChatThreadId,
                                onSelectThread = { selectedChatThreadId = it },
                                onStartChatWithUser = { target ->
                                    viewModel.openDmWithUser(target) { tid ->
                                        selectedChatThreadId = tid
                                    }
                                },
                                onMarkThreadRead = { viewModel.markThreadRead(it) },
                                onSendMessage = { tid, content, tag -> viewModel.sendMessage(tid, content, tag) }
                            )

                            AppDestination.PROFILE -> ProfileScreen(
                                profileUser = user,
                                currentUser = user,
                                allPosts = posts,
                                isFollowing = false,
                                onEditProfile = { isEditingProfile = true },
                                onToggleFollow = { viewModel.toggleFollow(it) },
                                onOpenDm = {},
                                onAuthorClick = { uid ->
                                    if (uid != user.id) viewingProfileUserId = uid
                                },
                                onLikeClick = { viewModel.toggleLike(it) },
                                onCommentClick = { activeCommentPostId = it.post.id },
                                onRepostClick = { viewModel.toggleRepost(it) },
                                onBookmarkClick = { viewModel.toggleBookmark(it) },
                                onVotePoll = { pid, idx -> viewModel.votePoll(pid, idx) },
                                onJoinLfg = { viewModel.toggleJoinLfg(it) },
                                onDeletePost = { viewModel.deletePost(it) }
                            )

                            AppDestination.VAULT -> VaultScreen(
                                currentUser = user,
                                bookmarkedPosts = bookmarkedPosts,
                                onAuthorClick = { uid ->
                                    if (uid == user.id) currentDestination = AppDestination.PROFILE
                                    else viewingProfileUserId = uid
                                },
                                onFollowClick = { viewModel.toggleFollow(it) },
                                onLikeClick = { viewModel.toggleLike(it) },
                                onCommentClick = { activeCommentPostId = it.post.id },
                                onRepostClick = { viewModel.toggleRepost(it) },
                                onBookmarkClick = { viewModel.toggleBookmark(it) },
                                onVotePoll = { pid, idx -> viewModel.votePoll(pid, idx) },
                                onJoinLfg = { viewModel.toggleJoinLfg(it) },
                                onDeletePost = { viewModel.deletePost(it) }
                            )

                            AppDestination.NOTIFICATIONS -> NotificationsScreen(
                                notifications = notifications,
                                onMarkAllRead = { viewModel.markAllNotificationsRead() }
                            )

                            AppDestination.SETTINGS -> SettingsScreen(
                                currentUser = user,
                                currentTheme = accentTheme,
                                compactCards = compactCards,
                                onSelectTheme = { viewModel.setAccentTheme(it) },
                                onToggleCompactCards = { viewModel.setCompactCards(it) },
                                onOpenAccountSwitcher = { showAccountSwitcher = true },
                                onLogout = { viewModel.logout() }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreatePostSheet) {
        val postBeingEdited = editingPost
        CreatePostSheet(
            initialCategory = composerInitialCategory,
            editingPost = postBeingEdited,
            onDismiss = {
                showCreatePostSheet = false
                editingPost = null
            },
            onPublish = { cat, game, title, content, mediaType, mediaUri, tags, vis, pollQ, pollOpts, lfgSlots ->
                if (postBeingEdited != null) {
                    viewModel.editPost(
                        postId = postBeingEdited.id,
                        category = cat,
                        gameTag = game,
                        title = title,
                        content = content,
                        mediaType = mediaType,
                        mediaUri = mediaUri,
                        hashtags = tags,
                        visibility = vis,
                        onEdited = {
                            showCreatePostSheet = false
                            editingPost = null
                        }
                    )
                } else {
                    viewModel.createPost(
                        category = cat,
                        gameTag = game,
                        title = title,
                        content = content,
                        mediaType = mediaType,
                        mediaUri = mediaUri,
                        hashtags = tags,
                        visibility = vis,
                        pollQuestion = pollQ,
                        pollOptions = pollOpts,
                        lfgSlotsTotal = lfgSlots,
                        onCreated = { showCreatePostSheet = false }
                    )
                }
            }
        )
    }

    val activePost: PostWithDetails? = activeCommentPostId?.let { pid -> posts.find { it.post.id == pid } }
    if (activePost != null) {
        CommentsSheet(
            postDetails = activePost,
            comments = commentsByPost[activePost.post.id].orEmpty(),
            onDismiss = { activeCommentPostId = null },
            onAddComment = { text -> viewModel.addComment(activePost.post.id, text) },
            onLikeComment = { cid -> viewModel.likeComment(cid) },
            onAuthorClick = { uid ->
                activeCommentPostId = null
                if (uid == user.id) currentDestination = AppDestination.PROFILE
                else viewingProfileUserId = uid
            }
        )
    }

    if (showAccountSwitcher) {
        AccountSwitcherDialog(
            accounts = allUsers,
            activeUserId = user.id,
            onSelectAccount = { viewModel.switchAccount(it) },
            onCreateNewAccount = { viewModel.logout() },
            onDismiss = { showAccountSwitcher = false }
        )
    }
}

@Composable
private fun PlayForjaBottomBar(
    currentDestination: AppDestination,
    unreadMessages: Int,
    onSelect: (AppDestination) -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    val items = listOf(
        Triple(AppDestination.FEED, Icons.Filled.Home, Icons.Outlined.Home),
        Triple(AppDestination.EXPLORE, Icons.Filled.Explore, Icons.Outlined.Explore),
        Triple(AppDestination.CLANS, Icons.Filled.Shield, Icons.Outlined.Shield),
        Triple(AppDestination.MESSAGES, Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
        Triple(AppDestination.PROFILE, Icons.Filled.Person, Icons.Outlined.Person)
    )

    NavigationBar(
        containerColor = CarbonSurface,
        contentColor = TextPrimary
    ) {
        items.forEach { (dest, activeIcon, inactiveIcon) ->
            val selected = currentDestination == dest
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(dest) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (dest == AppDestination.MESSAGES && unreadMessages > 0) {
                                Badge(containerColor = extras.neonPrimary, contentColor = ObsidianBg) {
                                    Text(unreadMessages.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selected) activeIcon else inactiveIcon,
                            contentDescription = dest.label
                        )
                    }
                },
                label = {
                    Text(
                        text = when (dest) {
                            AppDestination.FEED -> "Feed"
                            AppDestination.EXPLORE -> "Explorar"
                            AppDestination.CLANS -> "Clanes"
                            AppDestination.MESSAGES -> "Chats"
                            AppDestination.PROFILE -> "Perfil"
                            else -> dest.label
                        },
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = extras.neonPrimary,
                    selectedTextColor = extras.neonPrimary,
                    indicatorColor = extras.neonPrimary.copy(alpha = 0.16f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("bottom_nav_${dest.route}")
            )
        }
    }
}

@Composable
private fun PlayForjaNavigationRail(
    currentDestination: AppDestination,
    unreadMessages: Int,
    onSelect: (AppDestination) -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    val items = listOf(
        Triple(AppDestination.FEED, Icons.Filled.Home, Icons.Outlined.Home),
        Triple(AppDestination.EXPLORE, Icons.Filled.Explore, Icons.Outlined.Explore),
        Triple(AppDestination.CLANS, Icons.Filled.Shield, Icons.Outlined.Shield),
        Triple(AppDestination.MESSAGES, Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
        Triple(AppDestination.PROFILE, Icons.Filled.Person, Icons.Outlined.Person)
    )

    NavigationRail(
        containerColor = CarbonSurface,
        modifier = Modifier.fillMaxHeight()
    ) {
        Spacer(Modifier.size(16.dp))
        items.forEach { (dest, activeIcon, inactiveIcon) ->
            val selected = currentDestination == dest
            NavigationRailItem(
                selected = selected,
                onClick = { onSelect(dest) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (dest == AppDestination.MESSAGES && unreadMessages > 0) {
                                Badge(containerColor = extras.neonPrimary, contentColor = ObsidianBg) {
                                    Text(unreadMessages.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selected) activeIcon else inactiveIcon,
                            contentDescription = dest.label
                        )
                    }
                },
                label = { Text(dest.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = extras.neonPrimary,
                    selectedTextColor = extras.neonPrimary,
                    indicatorColor = extras.neonPrimary.copy(alpha = 0.16f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
