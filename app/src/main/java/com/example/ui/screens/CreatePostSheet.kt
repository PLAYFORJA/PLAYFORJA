package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.MediaStorageHelper
import com.example.data.local.PostCategory
import com.example.data.local.PostEntity
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostSheet(
    initialCategory: String = PostCategory.GENERAL.key,
    editingPost: PostEntity? = null,
    onDismiss: () -> Unit,
    onPublish: (
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
    ) -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEditing = editingPost != null

    var selectedCategory by remember { mutableStateOf(editingPost?.category ?: initialCategory) }
    var title by remember { mutableStateOf(editingPost?.title ?: "") }
    var content by remember { mutableStateOf(editingPost?.content ?: "") }
    var gameTag by remember { mutableStateOf(editingPost?.gameTag ?: "") }
    var hashtags by remember { mutableStateOf(editingPost?.hashtags ?: "#PlayForja") }
    var visibility by remember { mutableStateOf(editingPost?.visibility ?: "GLOBAL") }

    var mediaType by remember { mutableStateOf(editingPost?.mediaType ?: "NONE") }
    var mediaUri by remember { mutableStateOf(editingPost?.mediaUri ?: "") }

    var enablePoll by remember { mutableStateOf(false) }
    var pollQuestion by remember { mutableStateOf("") }
    val pollOptions = remember { mutableStateListOf("Opción 1", "Opción 2") }

    var enableLfg by remember { mutableStateOf(initialCategory == PostCategory.LFG.key) }
    var lfgSlots by remember { mutableIntStateOf(4) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            mediaType = "URI"
            mediaUri = MediaStorageHelper.persistPickedImage(context, uri)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CarbonSurface,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Editar Publicación" else "Forjar Publicación Libre",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = TextSecondary)
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "CATEGORÍA",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PostCategory.entries.forEach { cat ->
                    val selected = selectedCategory == cat.key
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) extras.neonPrimary else CarbonElevated,
                        border = BorderStroke(1.dp, if (selected) extras.neonPrimary else CarbonBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedCategory = cat.key
                                if (cat == PostCategory.LFG) enableLfg = true
                            }
                    ) {
                        Text(
                            text = cat.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) ObsidianBg else TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título impactante (opcional)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = extras.neonPrimary,
                    unfocusedBorderColor = CarbonBorder,
                    focusedContainerColor = ObsidianBg,
                    unfocusedContainerColor = ObsidianBg,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth().testTag("compose_title_input")
            )

            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Comparte lo que quieras con el mundo gamer...") },
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = extras.neonPrimary,
                    unfocusedBorderColor = CarbonBorder,
                    focusedContainerColor = ObsidianBg,
                    unfocusedContainerColor = ObsidianBg,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth().testTag("compose_content_input")
            )

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = gameTag,
                    onValueChange = { gameTag = it },
                    label = { Text("Juego") },
                    placeholder = { Text("Ej. Valorant") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = extras.neonSecondary,
                        unfocusedBorderColor = CarbonBorder,
                        focusedContainerColor = ObsidianBg,
                        unfocusedContainerColor = ObsidianBg,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = hashtags,
                    onValueChange = { hashtags = it },
                    label = { Text("Hashtags") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = extras.neonSecondary,
                        unfocusedBorderColor = CarbonBorder,
                        focusedContainerColor = ObsidianBg,
                        unfocusedContainerColor = ObsidianBg,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "ADJUNTAR IMAGEN O CAPTURA",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, extras.neonSecondary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = null,
                        tint = extras.neonSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Galería", color = extras.neonSecondary)
                }

                listOf(
                    "DRAWABLE_ARENA" to "Arte Arena",
                    "DRAWABLE_CYBER" to "Setup RGB",
                    "DRAWABLE_HERO" to "Estadio Forja"
                ).forEach { (type, label) ->
                    val selected = mediaType == type
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) extras.neonSecondary.copy(alpha = 0.2f) else CarbonElevated,
                        border = BorderStroke(1.dp, if (selected) extras.neonSecondary else CarbonBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                mediaType = if (selected) "NONE" else type
                                mediaUri = ""
                            }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) extras.neonSecondary else TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            if (mediaType != "NONE") {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    when (mediaType) {
                        "URI" -> AsyncImage(
                            model = mediaUri,
                            contentDescription = "Vista previa",
                            modifier = Modifier.fillMaxWidth().height(140.dp),
                            contentScale = ContentScale.Crop
                        )
                        "DRAWABLE_ARENA" -> Image(
                            painter = painterResource(id = R.drawable.img_post_arena),
                            contentDescription = "Vista previa",
                            modifier = Modifier.fillMaxWidth().height(140.dp),
                            contentScale = ContentScale.Crop
                        )
                        "DRAWABLE_CYBER" -> Image(
                            painter = painterResource(id = R.drawable.img_banner_cyber),
                            contentDescription = "Vista previa",
                            modifier = Modifier.fillMaxWidth().height(140.dp),
                            contentScale = ContentScale.Crop
                        )
                        else -> Image(
                            painter = painterResource(id = R.drawable.img_hero_forge),
                            contentDescription = "Vista previa",
                            modifier = Modifier.fillMaxWidth().height(140.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ObsidianBg.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clickable { mediaType = "NONE"; mediaUri = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Quitar imagen",
                            tint = TextPrimary,
                            modifier = Modifier.padding(6.dp).size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Toggles for Poll & LFG
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Poll, contentDescription = null, tint = extras.neonPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Añadir Encuesta Interactiva", style = MaterialTheme.typography.titleSmall)
                }
                Switch(checked = enablePoll, onCheckedChange = { enablePoll = it })
            }

            AnimatedVisibility(visible = enablePoll) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pollQuestion,
                        onValueChange = { pollQuestion = it },
                        label = { Text("Pregunta de la encuesta") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    pollOptions.forEachIndexed { idx, opt ->
                        OutlinedTextField(
                            value = opt,
                            onValueChange = { pollOptions[idx] = it },
                            label = { Text("Opción ${idx + 1}") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (pollOptions.size < 4) {
                        OutlinedButton(onClick = { pollOptions.add("Opción ${pollOptions.size + 1}") }) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar opción")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Groups, contentDescription = null, tint = extras.neonSecondary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tarjeta Reclutamiento Squad LFG", style = MaterialTheme.typography.titleSmall)
                }
                Switch(checked = enableLfg, onCheckedChange = { enableLfg = it })
            }

            AnimatedVisibility(visible = enableLfg) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cupos totales:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    listOf(2, 3, 4, 5, 6).forEach { count ->
                        val selected = lfgSlots == count
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) extras.neonSecondary else CarbonHighest,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { lfgSlots = count }
                        ) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) ObsidianBg else TextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text("VISIBILIDAD", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("GLOBAL" to "Mundo Global", "CLAN" to "Solo Clan", "SEGUIDORES" to "Seguidores").forEach { (key, label) ->
                    val selected = visibility == key
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) extras.neonPrimary.copy(alpha = 0.2f) else CarbonElevated,
                        border = BorderStroke(1.dp, if (selected) extras.neonPrimary else CarbonBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { visibility = key }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) extras.neonGlow else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    if (content.isNotBlank() || title.isNotBlank()) {
                        onPublish(
                            selectedCategory,
                            gameTag,
                            title,
                            content,
                            mediaType,
                            mediaUri,
                            hashtags,
                            visibility,
                            if (enablePoll) pollQuestion else "",
                            if (enablePoll) pollOptions.toList() else emptyList(),
                            if (enableLfg) lfgSlots else 0
                        )
                    }
                },
                enabled = content.isNotBlank() || title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("publish_post_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = extras.neonPrimary,
                    contentColor = ObsidianBg
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "GUARDAR CAMBIOS" else "PUBLICAR EN PLAYFORJA",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
