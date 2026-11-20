package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.GameCatalog
import com.example.data.local.GameLogoBadge
import com.example.data.local.UserEntity
import com.example.ui.components.AvatarCatalog
import com.example.ui.components.CyberSurfaceCard
import com.example.ui.components.ForgeAvatar
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonElevated
import com.example.ui.theme.CarbonHighest
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.LocalPlayForjaExtras
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AuthScreen(
    existingAccounts: List<UserEntity>,
    authError: String?,
    onClearError: () -> Unit,
    onLogin: (identifier: String, password: String) -> Unit,
    onRegister: (
        handle: String,
        displayName: String,
        email: String,
        password: String,
        platform: String,
        favoriteGame: String,
        avatarPreset: String
    ) -> Unit
) {
    val extras = LocalPlayForjaExtras.current
    var isRegisterMode by rememberSaveable { mutableStateOf(false) }

    // Login states
    var loginIdentifier by rememberSaveable { mutableStateOf("") }
    var loginPassword by rememberSaveable { mutableStateOf("") }
    var showLoginPassword by remember { mutableStateOf(false) }

    // Register states
    var regHandle by rememberSaveable { mutableStateOf("") }
    var regName by rememberSaveable { mutableStateOf("") }
    var regEmail by rememberSaveable { mutableStateOf("") }
    var regPassword by rememberSaveable { mutableStateOf("") }
    var showRegPassword by remember { mutableStateOf(false) }
    var regPlatform by rememberSaveable { mutableStateOf("PC") }
    var regGame by rememberSaveable { mutableStateOf("Valorant") }
    var regAvatar by rememberSaveable { mutableStateOf("preset_knight") }

    val platforms = listOf("PC", "PS5", "Xbox", "Switch", "Móvil")
    val avatarPresets = AvatarCatalog.presets
    val quickGames = GameCatalog.allGames.take(10)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Epic Hero Banner Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_forge),
                    contentDescription = "Banner PlayForja",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ObsidianBg.copy(alpha = 0.25f),
                                    ObsidianBg.copy(alpha = 0.65f),
                                    ObsidianBg
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = extras.neonPrimary.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, extras.neonPrimary)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = extras.neonPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "RED SOCIAL GAMER LOCAL Y LIBRE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = extras.neonGlow
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "PLAYFORJA",
                        style = MaterialTheme.typography.displaySmall,
                        color = TextPrimary
                    )
                    Text(
                        text = "Comparte sin límites, arma tu clan y forja tu identidad gamer.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Segmented Mode Toggle
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CarbonElevated,
                    border = BorderStroke(1.dp, CarbonBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        listOf(false to "Iniciar Sesión", true to "Crear Cuenta Local").forEach { (reg, label) ->
                            val selected = isRegisterMode == reg
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selected) Brush.horizontalGradient(
                                            listOf(extras.neonPrimary, extras.neonPrimary.copy(alpha = 0.85f))
                                        ) else Brush.horizontalGradient(
                                            listOf(Color.Transparent, Color.Transparent)
                                        )
                                    )
                                    .clickable {
                                        isRegisterMode = reg
                                        onClearError()
                                    }
                                    .padding(vertical = 12.dp)
                                    .testTag(if (reg) "tab_register" else "tab_login"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) ObsidianBg else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                AnimatedVisibility(visible = authError != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CrimsonAlert.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, CrimsonAlert),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = authError.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrimsonAlert,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                CyberSurfaceCard(highlighted = true) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        if (!isRegisterMode) {
                            Text(
                                text = "Acceso de Forjador",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "Inicia sesión con tu cuenta local guardada en el dispositivo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(Modifier.height(14.dp))

                            AuthTextField(
                                value = loginIdentifier,
                                onValueChange = { loginIdentifier = it; onClearError() },
                                label = "GamerTag (@usuario) o Correo",
                                icon = Icons.Filled.AlternateEmail,
                                testTag = "login_identifier_input"
                            )
                            Spacer(Modifier.height(10.dp))
                            AuthTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it; onClearError() },
                                label = "Contraseña",
                                icon = Icons.Filled.Lock,
                                isPassword = true,
                                passwordVisible = showLoginPassword,
                                onTogglePassword = { showLoginPassword = !showLoginPassword },
                                testTag = "login_password_input"
                            )
                            Spacer(Modifier.height(18.dp))
                            Button(
                                onClick = { onLogin(loginIdentifier, loginPassword) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("login_submit_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = extras.neonPrimary,
                                    contentColor = ObsidianBg
                                )
                            ) {
                                Text(
                                    text = "ENTRAR A PLAYFORJA",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        } else {
                            Text(
                                text = "Forja tu Nueva Cuenta Local",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "100% dentro de la aplicación, sin depender de servidores externos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(Modifier.height(14.dp))

                            Text(
                                text = "ELIGE TU AVATAR INICIAL (${avatarPresets.size} DISEÑOS)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                avatarPresets.forEach { preset ->
                                    val selected = regAvatar == preset.key
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { regAvatar = preset.key }
                                            .padding(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .border(
                                                    width = if (selected) 2.dp else 0.dp,
                                                    color = if (selected) extras.neonSecondary else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .padding(3.dp)
                                        ) {
                                            ForgeAvatar(
                                                avatarPreset = preset.key,
                                                frameStyle = when (preset.key) {
                                                    "preset_cyber", "preset_diamond" -> "CYAN"
                                                    "preset_plasma", "preset_samurai", "preset_psy" -> "PLASMA"
                                                    "preset_venom", "preset_guardian" -> "LIME"
                                                    "preset_gold", "preset_nova" -> "GOLD"
                                                    else -> "EMBER"
                                                },
                                                displayName = regName.ifBlank { preset.label },
                                                size = 48.dp
                                            )
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = preset.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (selected) extras.neonSecondary else TextMuted,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            AuthTextField(
                                value = regHandle,
                                onValueChange = { regHandle = it; onClearError() },
                                label = "GamerTag único (ej. shadow_mx)",
                                icon = Icons.Filled.AlternateEmail,
                                testTag = "register_handle_input"
                            )
                            Spacer(Modifier.height(10.dp))
                            AuthTextField(
                                value = regName,
                                onValueChange = { regName = it; onClearError() },
                                label = "Nombre visible",
                                icon = Icons.Filled.Person,
                                testTag = "register_name_input"
                            )
                            Spacer(Modifier.height(10.dp))
                            AuthTextField(
                                value = regEmail,
                                onValueChange = { regEmail = it; onClearError() },
                                label = "Correo o ID local",
                                icon = Icons.Filled.Email,
                                keyboardType = KeyboardType.Email,
                                testTag = "register_email_input"
                            )
                            Spacer(Modifier.height(10.dp))
                            AuthTextField(
                                value = regPassword,
                                onValueChange = { regPassword = it; onClearError() },
                                label = "Contraseña (mín. 4 caracteres)",
                                icon = Icons.Filled.Lock,
                                isPassword = true,
                                passwordVisible = showRegPassword,
                                onTogglePassword = { showRegPassword = !showRegPassword },
                                testTag = "register_password_input"
                            )
                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "PLATAFORMA PRINCIPAL",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                platforms.forEach { p ->
                                    val selected = regPlatform == p
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (selected) extras.neonPrimary.copy(alpha = 0.2f) else CarbonHighest,
                                        border = BorderStroke(1.dp, if (selected) extras.neonPrimary else CarbonBorder),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { regPlatform = p }
                                    ) {
                                        Text(
                                            text = p,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (selected) extras.neonGlow else TextSecondary,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "JUEGO PRINCIPAL DEL CATÁLOGO",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                quickGames.forEach { game ->
                                    val selected = regGame.equals(game.title, ignoreCase = true)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (selected) game.primaryColor.copy(alpha = 0.2f) else CarbonHighest,
                                        border = BorderStroke(1.dp, if (selected) game.primaryColor else CarbonBorder),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { regGame = game.title }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            GameLogoBadge(game = game, size = 24.dp)
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = game.title,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (selected) TextPrimary else TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            AuthTextField(
                                value = regGame,
                                onValueChange = { regGame = it },
                                label = "Juego favorito",
                                icon = Icons.Filled.SportsEsports,
                                testTag = "register_game_input"
                            )

                            Spacer(Modifier.height(18.dp))
                            Button(
                                onClick = {
                                    onRegister(
                                        regHandle,
                                        regName,
                                        regEmail,
                                        regPassword,
                                        regPlatform,
                                        regGame,
                                        regAvatar
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("register_submit_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = extras.neonPrimary,
                                    contentColor = ObsidianBg
                                )
                            ) {
                                Text(
                                    text = "FORJAR MI CUENTA LOCAL",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                // Quick Local Account Switcher
                if (existingAccounts.isNotEmpty()) {
                    Spacer(Modifier.height(22.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CUENTAS LOCALES EN ESTE DISPOSITIVO",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "Acceso rápido",
                            style = MaterialTheme.typography.labelSmall,
                            color = extras.neonSecondary
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        existingAccounts.forEach { acc ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = CarbonElevated,
                                border = BorderStroke(1.dp, CarbonBorder),
                                modifier = Modifier
                                    .width(154.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        isRegisterMode = false
                                        loginIdentifier = acc.handle
                                        loginPassword = ""
                                    }
                                    .testTag("quick_account_${acc.handle}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ForgeAvatar(
                                        avatarPreset = acc.avatarPreset,
                                        frameStyle = acc.frameStyle,
                                        displayName = acc.displayName,
                                        size = 46.dp,
                                        level = acc.level
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = acc.displayName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "@${acc.handle}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extras.neonSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val extras = LocalPlayForjaExtras.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null, tint = extras.neonSecondary)
        },
        trailingIcon = if (isPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = "Mostrar u ocultar contraseña",
                        tint = TextSecondary
                    )
                }
            }
        } else null,
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = extras.neonPrimary,
            unfocusedBorderColor = CarbonBorder,
            focusedContainerColor = ObsidianBg,
            unfocusedContainerColor = ObsidianBg,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = extras.neonPrimary,
            unfocusedLabelColor = TextMuted
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}
