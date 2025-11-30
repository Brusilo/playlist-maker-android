package com.example.playlist_maker_android_brusilodiana.domain

import com.example.playlist_maker_android_brusilodiana.domain.models.Playlist
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylist(playlistId: Long): Flow<Playlist?>
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addNewPlaylist(name: String, description: String)
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun deletePlaylistById(id: Long)
}