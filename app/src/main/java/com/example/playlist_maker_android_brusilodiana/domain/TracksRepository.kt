package com.example.playlist_maker_android_brusilodiana.domain

import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>

    fun getTrackByNameAndArtist(track: Track): Flow<Track?>

    fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long)

    suspend fun deleteTrackFromPlaylist(track: Track)

    suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean)

    fun getTrackById(trackId: Long): Flow<Track?>

    suspend fun saveTrack(track: Track, playlistId: Long? = null)
}