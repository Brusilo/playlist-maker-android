package com.example.playlist_maker_android_brusilodiana.domain

import com.example.playlist_maker_android_brusilodiana.domain.models.Track

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
}