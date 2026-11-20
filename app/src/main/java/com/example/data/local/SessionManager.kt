package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "playforja_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_ACTIVE_USER_ID = longPreferencesKey("active_user_id")
        private val KEY_ACCENT_THEME = stringPreferencesKey("accent_theme")
        private val KEY_COMPACT_CARDS = booleanPreferencesKey("compact_cards")
        private val KEY_AUTO_SAVE_SESSION = booleanPreferencesKey("auto_save_session")
    }

    val activeUserIdFlow: Flow<Long?> = context.dataStore.data.map { prefs ->
        val id = prefs[KEY_ACTIVE_USER_ID] ?: -1L
        if (id > 0L) id else null
    }

    val accentThemeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCENT_THEME] ?: "FORJA"
    }

    val compactCardsFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_COMPACT_CARDS] ?: false
    }

    suspend fun setActiveUser(userId: Long, accentTheme: String? = null) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACTIVE_USER_ID] = userId
            if (accentTheme != null) {
                prefs[KEY_ACCENT_THEME] = accentTheme
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACTIVE_USER_ID)
        }
    }

    suspend fun setAccentTheme(themeKey: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCENT_THEME] = themeKey
        }
    }

    suspend fun setCompactCards(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_COMPACT_CARDS] = enabled
        }
    }
}
