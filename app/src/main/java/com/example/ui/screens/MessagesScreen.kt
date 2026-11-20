package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatThreadEntity
import com.example.data.local.UserEntity
import com.example.ui.components.CyberSurfaceCard
import com.example.ui.components.ForgeAvatar
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MessagesScreen(
    currentUser: UserEntity,
    availableGamers: List<UserEntity>,
    threads: List<ChatThreadEntity>,
    messagesByThread: Map<Long, List<ChatMessageEntity>>,
    selectedThreadId: Long?,
    onSelectThread: (Long?) -> Unit,
    onStartChatWithUser: (UserEntity) -> Unit,
    onMarkThreadRead: (Long) -> Unit,
    onSendMessage: (threadId: Long, content: String, attachmentTag: String) -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    val activeThread = threads.find { it.id == selectedThreadId }

    if (activeThread != null) {
        BackHandler { onSelectThread(null) }
        LaunchedEffect(activeThread.id) {
            onMarkThreadRead(activeThread.id)
        }

        val messages = messagesByThread[activeThread.id].orEmpty()
        var draft by remember { mutableStateOf("") }
        val quickReplies = listOf("¡GG!", "¿Sale partida?", "¡Voy entrando!", "Armemos Squad 🔥")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxSize()
            ) {
                // Thread Header
                Surface(
                    color = CarbonElevated,
                    border = BorderStroke(1.dp, CarbonBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onSelectThread(null) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver a mensajes",
                                tint = TextPrimary
                            )
                        }
                        ForgeAvatar(
                            avatarPreset = activeThread.participantAvatarPreset,
                            frameStyle = activeThread.participantFrame,
                            displayName = activeThread.participantName,
                            size = 40.dp,
                            statusKey = activeThread.participantStatus
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeThread.participantName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                                if (activeThread.isClanChat) {
                                    Spacer(Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Groups,
                                        contentDescription = "Sala de clan",
                                        tint = extras.neonSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = "@${activeThread.participantHandle}",
                                style = MaterialTheme.typography.labelSmall,
                                color = extras.neonSecondary
                            )
                        }
                    }
                }

                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isMine = msg.senderId == currentUser.id
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                        ) {
                            if (!isMine) {
                                Text(
                                    text = msg.senderName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extras.neonSecondary,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isMine) 14.dp else 4.dp,
                                    bottomEnd = if (isMine) 4.dp else 14.dp
                                ),
                                color = if (isMine) extras.neonPrimary.copy(alpha = 0.22f) else CarbonElevated,
                                border = BorderStroke(
                                    1.dp,
                                    if (isMine) extras.neonPrimary.copy(alpha = 0.7f) else CarbonBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) {
                                    if (msg.attachmentTag.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = extras.neonSecondary.copy(alpha = 0.2f),
                                            modifier = Modifier.padding(bottom = 5.dp)
                                        ) {
                                            Text(
                                                text = msg.attachmentTag,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = extras.neonSecondary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = msg.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                    )
                                }
                            }
                            Text(
                                text = formatRelativeTime(msg.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                            )
                        }
                    }
                }

                // Quick Gamer Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickReplies.forEach { reply ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CarbonHighest,
                            border = BorderStroke(1.dp, CarbonBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSendMessage(activeThread.id, reply, "") }
                        ) {
                            Text(
                                text = reply,
                                style = MaterialTheme.typography.labelMedium,
                                color = extras.neonSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Composer Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        placeholder = { Text("Escribe un mensaje al squad...", color = TextMuted) },
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
                        modifier = Modifier.weight(1f).testTag("chat_message_input")
                    )
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = CircleShape,
                        color = extras.neonPrimary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (draft.isNotBlank()) {
                                    onSendMessage(activeThread.id, draft, "")
                                    draft = ""
                                }
                            },
                            modifier = Modifier.testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = ObsidianBg
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Threads List
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
                    val otherGamers = remember(availableGamers, currentUser.id) {
                        availableGamers.filter { it.id != currentUser.id }
                    }
                    if (otherGamers.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "NUEVO CHAT CON FORJADORES",
                                style = MaterialTheme.typography.labelSmall,
                                color = extras.neonSecondary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                otherGamers.forEach { gamer ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onStartChatWithUser(gamer) }
                                            .padding(4.dp)
                                    ) {
                                        ForgeAvatar(
                                            avatarPreset = gamer.avatarPreset,
                                            frameStyle = gamer.frameStyle,
                                            displayName = gamer.displayName,
                                            size = 48.dp,
                                            statusKey = gamer.status
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = gamer.displayName.substringBefore(" "),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextPrimary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "CHATS Y SALAS DE ESCUADRÓN",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                items(threads, key = { it.id }) { thread ->
                    CyberSurfaceCard(
                        highlighted = thread.unreadCount > 0,
                        onClick = { onSelectThread(thread.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ForgeAvatar(
                                avatarPreset = thread.participantAvatarPreset,
                                frameStyle = thread.participantFrame,
                                displayName = thread.participantName,
                                size = 50.dp,
                                statusKey = thread.participantStatus
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = thread.participantName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = formatRelativeTime(thread.lastTimestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                                Spacer(Modifier.height(3.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = thread.lastMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (thread.unreadCount > 0) TextPrimary else TextSecondary,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (thread.unreadCount > 0) {
                                        Spacer(Modifier.width(8.dp))
                                        Badge(containerColor = extras.neonPrimary, contentColor = ObsidianBg) {
                                            Text(thread.unreadCount.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
