package com.example.playlist_maker_android_brusilodiana.data.repository

import com.example.playlist_maker_android_brusilodiana.data.preferences.SearchHistoryPreferences
import com.example.playlist_maker_android_brusilodiana.domain.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepositoryImpl(
    private val searchHistoryPreferences: SearchHistoryPreferences
) : SearchHistoryRepository {

    override fun getHistory(): Flow<List<String>> {
        return searchHistoryPreferences.getEntries()
    }

    override fun addToHistory(word: String) {
        searchHistoryPreferences.addEntry(word)
    }

    override suspend fun clearHistory() {
        searchHistoryPreferences.clearHistory()
    }
}