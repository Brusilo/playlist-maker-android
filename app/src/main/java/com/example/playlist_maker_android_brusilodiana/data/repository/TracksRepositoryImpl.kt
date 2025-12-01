package com.example.playlist_maker_android_brusilodiana.data.repository

import android.content.Context
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.data.dto.TrackDto
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_brusilodiana.data.local.DatabaseMock
import com.example.playlist_maker_android_brusilodiana.domain.NetworkClient
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context
) : TracksRepository {

    private val database = DatabaseMock.Companion.getInstance(context)
    private val searchResults = mutableListOf<Track>()

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return when (response.resultCode) {
            200 -> {
                val searchResponse = response as TracksSearchResponse

                val tracks = searchResponse.results.mapNotNull { trackDto ->
                    mapDtoToTrack(trackDto)
                }

                val searchQuery = expression.lowercase().trim()
                val sortedTracks = tracks.sortedWith(compareByDescending<Track> { track ->
                    calculateRelevanceScore(track, searchQuery)
                }.thenBy { it.trackName.lowercase() })

                sortedTracks.also { sortedList ->
                    searchResults.clear()
                    searchResults.addAll(sortedList)

                    sortedList.forEach { track ->
                        saveTrackIfNotExists(track)
                    }
                }
            }
            -1 -> throw IOException(response.errorMessage ?: context.getString(R.string.network_error))
            else -> throw Exception(response.errorMessage ?: context.getString(R.string.server_error))
        }
    }

    private fun calculateRelevanceScore(track: Track, searchQuery: String): Int {
        var score = 0
        val trackNameLower = track.trackName.lowercase()
        val artistNameLower = track.artistName.lowercase()

        if (trackNameLower == searchQuery) {
            score += 10
        }

        if (trackNameLower.startsWith(searchQuery)) {
            score += 8
        }

        if (artistNameLower.startsWith(searchQuery)) {
            score += 6
        }

        if (trackNameLower.contains(searchQuery)) {
            score += 4
        }

        if (artistNameLower.contains(searchQuery)) {
            score += 2
        }

        if (trackNameLower.length < 20) {
            score += 1
        }

        return score
    }

    private fun mapDtoToTrack(dto: TrackDto): Track? {
        return try {
            val id = dto.id ?: 0L
            val trackName = dto.trackName ?: context.getString(R.string.unknown)
            val artistName = dto.artistName ?: context.getString(R.string.unknown)

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