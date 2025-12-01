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
import java.text.SimpleDateFormat
import java.util.Locale

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
            (response as TracksSearchResponse).results.mapNotNull { trackDto ->
                mapDtoToTrack(trackDto)
            }.also { tracks ->
                searchResults.clear()
                searchResults.addAll(tracks)

                tracks.forEach { track ->
                    saveTrackIfNotExists(track)
                }
            }
        } else {
            // Если есть ошибка, можно её логировать
            if (response.errorMessage != null) {
                println("Search error: ${response.errorMessage}")
            }
            emptyList()
        }
    }

    private fun mapDtoToTrack(dto: com.example.playlist_maker_android_brusilodiana.data.dto.TrackDto): Track? {
        return try {
            val id = dto.id ?: 0L
            val trackName = dto.trackName ?: "Unknown"
            val artistName = dto.artistName ?: "Unknown"

            val trackTimeMillis = dto.trackTimeMillis ?: 0L
            val trackTime = if (trackTimeMillis > 0) {
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
            } else {
                "00:00"
            }

            Track(
                id = id,
                trackName = trackName,
                artistName = artistName,
                trackTime = trackTime,
                artworkUrl = dto.artworkUrl100 ?: "",
                previewUrl = dto.previewUrl ?: ""
            )
        } catch (e: Exception) {
            null
        }
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