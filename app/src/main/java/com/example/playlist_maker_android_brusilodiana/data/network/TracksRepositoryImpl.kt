package com.example.playlist_maker_android_brusilodiana.data.network

import com.example.playlist_maker_android_brusilodiana.data.local.DatabaseMock
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_brusilodiana.domain.NetworkClient
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : TracksRepository {

    private val database = DatabaseMock(scope = scope)
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
                    trackTime = formatted
                )
            }.also { tracks ->
                searchResults.clear()
                searchResults.addAll(tracks)
                tracks.forEach { track ->
                    database.insertTrack(track)
                }
            }
        } else emptyList()
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
        database.insertTrack(track.copy(playlistId = playlistId))
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        database.insertTrack(track.copy(playlistId = 0))
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        database.insertTrack(track.copy(favorite = isFavorite))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }
}