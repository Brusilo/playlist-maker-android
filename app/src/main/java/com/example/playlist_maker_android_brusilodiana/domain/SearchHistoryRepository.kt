package com.example.playlist_maker_android_brusilodiana.domain

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getHistory(): Flow<List<String>>
    fun addToHistory(word: String)
    suspend fun clearHistory()
}