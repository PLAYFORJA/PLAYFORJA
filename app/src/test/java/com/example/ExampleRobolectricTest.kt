package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.AuthResult
import com.example.data.local.GameCatalog
import com.example.data.local.LevelSystem
import com.example.data.local.PlayForjaRepository
import com.example.data.local.ProfileBgStyle
import com.example.data.local.SecurityUtils
import com.example.data.local.SessionManager
import com.example.ui.components.AvatarCatalog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `app name matches PlayForja`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PlayForja", appName)
    }

    @Test
    fun `password hashing and level system work deterministically`() {
        val stored = SecurityUtils.createStoredPassword("gamer2026")
        assertTrue(SecurityUtils.verifyPassword("gamer2026", stored))
        assertFalse(SecurityUtils.verifyPassword("wrongpass", stored))
        assertEquals(1, LevelSystem.levelForXp(0))
        assertEquals(2, LevelSystem.levelForXp(250))
    }

    @Test
    fun `game catalog and custom avatar helpers resolve real titles and emblems`() {
        val parsed = GameCatalog.parseSelectedGames("Valorant,elden_ring,Minecraft,unknown_game", "Valorant")
        assertEquals(3, parsed.size)
        assertEquals("valorant", parsed[0].id)
        assertEquals("elden_ring", parsed[1].id)
        assertEquals("minecraft", parsed[2].id)
        assertEquals(24, GameCatalog.allGames.size)

        val customKey = AvatarCatalog.buildCustomKey("crown", "cyber")
        assertEquals("custom:crown:cyber", customKey)
        assertEquals(18, AvatarCatalog.presets.size)
    }

    @Test
    fun `local registration login post and profile customization persist in Room`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val session = SessionManager(context)
        val repo = PlayForjaRepository(db, session)
        repo.ensureSeeded()

        val handle = "forjador_${System.currentTimeMillis() % 10000}"
        val regResult = repo.registerLocalUser(
            handleRaw = handle,
            displayName = "Forjador Test",
            emailRaw = "$handle@local.gg",
            password = "pass1234",
            platform = "PC",
            favoriteGame = "Valorant",
            avatarPreset = "preset_knight"
        )
        assertTrue(regResult is AuthResult.Success)
        val user = (regResult as AuthResult.Success).user

        val wrongLogin = repo.loginLocalUser(handle, "badpass")
        assertTrue(wrongLogin is AuthResult.Error)

        val goodLogin = repo.loginLocalUser(handle, "pass1234")
        assertTrue(goodLogin is AuthResult.Success)

        repo.updateProfile(
            userId = user.id,
            displayName = "Forjador Épico",
            bio = "Main duelista y fanático de RPGs.",
            bioMotto = "GG hasta el último pixel",
            playSchedule = "Noches 20:00 - 00:00",
            customTitle = "Forjador Supremo",
            platform = "PC",
            favoriteGame = "Elden Ring",
            favoriteGamesCsv = "Elden Ring,Valorant,Hollow Knight",
            avatarPreset = "custom:dragon:plasma",
            bannerPreset = "banner_cyber",
            profileBgStyle = ProfileBgStyle.VOID_NEBULA.key,
            frameStyle = "HOLO",
            accentTheme = "PLASMA",
            hardwareSpecs = "RTX 4070 • 32GB",
            socialLinks = "twitch.tv/forjador",
            clanTag = "FRJ"
        )

        val updated = repo.observeUser(user.id).first()!!
        assertEquals("Forjador Épico", updated.displayName)
        assertEquals("GG hasta el último pixel", updated.bioMotto)
        assertEquals("Noches 20:00 - 00:00", updated.playSchedule)
        assertEquals("Elden Ring,Valorant,Hollow Knight", updated.favoriteGamesCsv)
        assertEquals("custom:dragon:plasma", updated.avatarPreset)
        assertEquals(ProfileBgStyle.VOID_NEBULA.key, updated.profileBgStyle)
        assertEquals("HOLO", updated.frameStyle)

        repo.createPost(
            authorId = user.id,
            category = "GENERAL",
            gameTag = "Valorant",
            title = "Mi primer post libre",
            content = "Probando PlayForja local",
            mediaType = "NONE",
            mediaUri = "",
            hashtags = "#PlayForja",
            visibility = "GLOBAL",
            pollQuestion = "",
            pollOptions = emptyList(),
            lfgSlotsTotal = 0
        )

        val posts = repo.observePostsForUser(user.id).first()
        assertTrue(posts.any { it.post.title == "Mi primer post libre" && it.author.id == user.id })
    }

    @Test
    fun `auth screen renders and switches between login and register tabs`() {
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("PLAYFORJA").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("PLAYFORJA").assertIsDisplayed()
        composeRule.onNodeWithTag("tab_register").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("register_handle_input").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("register_submit_button").performScrollTo().assertIsDisplayed()
    }
}
