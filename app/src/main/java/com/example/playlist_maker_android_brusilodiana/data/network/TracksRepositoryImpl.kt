package com.example.playlist_maker_android_brusilodiana.data.network

import android.content.Context
import com.example.playlist_maker_android_brusilodiana.data.local.DatabaseMock
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_brusilodiana.domain.NetworkClient
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context
) : TracksRepository {

    private val database = DatabaseMock.getInstance(context)
    private val searchResults = mutableListOf<Track>()

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        delay(1000)

        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.mapIndexed { index, it ->
                val seconds = it.trackTimeMillis / 1000
                val minutes = seconds / 60
                val formatted = "%02d:%02d".format(minutes, seconds % 60)

                Track(
                    id = (index + 1).toLong(),
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = formatted,
                    artworkUrl = it.artworkUrl100 ?: ""
                )
            }.also { tracks ->
                searchResults.clear()
                searchResults.addAll(tracks)

                tracks.forEach { track ->
                    saveTrackIfNotExists(track)
                }
            }
        } else emptyList()
    }

    private suspend fun saveTrackIfNotExists(track: Track) {
        val existingTrack = database.getTrackByNameAndArtist(track).first()
        if (existingTrack == null) {
            database.insertTrack(track)
        } else {
            val updatedTrack = track.copy(
                id = existingTrack.id,
                favorite = existingTrack.favorite
            )
            database.insertTrack(updatedTrack)
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return database.getTrackByNameAndArtist(track)
    }

    override fun getTrackById(trackId: Long): Flow<Track?> = flow {
        val trackFromSearch = searchResults.find { it.id == trackId }
        if (trackFromSearch != null) {
            emit(trackFromSearch)
        } else {
            database.getTrackById(trackId).collect { trackFromDb ->
                emit(trackFromDb)
            }
        }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        database.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Long) {
        val existingTrack = database.getTrackByNameAndArtist(track).first()
        existingTrack?.let {
            database.deleteTrackFromPlaylist(it.id, playlistId)
        }
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val trackFromSearch = searchResults.find {
            it.trackName == track.trackName && it.artistName == track.artistName
        }

        if (trackFromSearch != null) {
            val updatedTrack = trackFromSearch.copy(favorite = isFavorite)
            database.insertTrack(updatedTrack)
        } else {
            val currentTrackFlow = database.getTrackByNameAndArtist(track)
            val currentTrack = currentTrackFlow.first()

            if (currentTrack != null) {
                val updatedTrack = currentTrack.copy(favorite = isFavorite)
                database.insertTrack(updatedTrack)
            } else {
                val newTrack = track.copy(favorite = isFavorite)
                database.insertTrack(newTrack)
            }
        }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }

    override suspend fun saveTrack(track: Track, playlistId: Long?) {
        if (playlistId != null) {
            database.addTrackToPlaylist(track, playlistId)
        } else {
            database.insertTrack(track)
        }
    }
}