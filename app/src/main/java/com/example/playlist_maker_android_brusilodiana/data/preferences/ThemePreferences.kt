package com.example.playlist_maker_android_brusilodiana.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

class ThemePreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

        fun create(context: Context): ThemePreferences {
            return ThemePreferences(context.themeDataStore)
        }
    }

    suspend fun saveDarkTheme(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    fun getDarkTheme(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }
    }
}