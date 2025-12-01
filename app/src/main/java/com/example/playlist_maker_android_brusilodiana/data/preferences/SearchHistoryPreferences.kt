package com.example.playlist_maker_android_brusilodiana.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

class SearchHistoryPreferences(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = CoroutineScope(
        CoroutineName("search-history-preferences") + SupervisorJob()
    )
) {
    companion object {
        private const val MAX_ENTRIES = 10
        private const val SEPARATOR = ","
        private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")

        fun create(context: Context): SearchHistoryPreferences {
            return SearchHistoryPreferences(context.dataStore)
        }
    }

    fun addEntry(word: String) {
        if (word.isEmpty()) {
            return
        }

        coroutineScope.launch {
            dataStore.edit { preferences ->
                val historyString = preferences[SEARCH_HISTORY_KEY].orEmpty()
                val history = if (historyString.isNotEmpty()) {
                    historyString.split(SEPARATOR).toMutableList()
                } else {
                    mutableListOf()
                }

                history.remove(word)
                history.add(0, word)
                val subList = if (history.size > MAX_ENTRIES) {
                    history.subList(0, MAX_ENTRIES)
                } else {
                    history
                }

                val updatedString = subList.joinToString(SEPARATOR)

                preferences[SEARCH_HISTORY_KEY] = updatedString
            }
        }
    }

    fun getEntries(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            val historyString = preferences[SEARCH_HISTORY_KEY].orEmpty()
            if (historyString.isEmpty()) {
                emptyList()
            } else {
                historyString.split(SEPARATOR)
            }
        }
    }

    suspend fun clearHistory() {
        dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_KEY)
        }
    }
}