package com.example.playlist_maker_android_brusilodiana.data.local

import com.example.playlist_maker_android_brusilodiana.domain.models.Playlist
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DatabaseMock private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: DatabaseMock? = null

        fun getInstance(): DatabaseMock {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseMock().also { INSTANCE = it }
            }
        }
    }

    private val historyList = mutableListOf<String>()
    private val _historyUpdates = MutableSharedFlow<Unit>()
    private val playlists = mutableListOf<Playlist>()

    private val _tracks = MutableStateFlow(mutableListOf<Track>())
    val tracks: List<Track> get() = _tracks.value

    private val tracksMutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.IO)

    private var idCounter: Long = 1

    private fun generateId(): Long {
        return idCounter++
    }

    fun getHistory(): List<String> {
        return historyList.toList()
    }

    fun addToHistory(word: String) {
        historyList.add(word)
        notifyHistoryChanged()
    }

    private fun notifyHistoryChanged() {
        scope.launch(Dispatchers.IO) {
            _historyUpdates.emit(Unit)
        }
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        delay(500)
        val filteredPlaylists = mutableListOf<Playlist>()
        playlists.forEach { playlist ->
            val playlistTracks = _tracks.value.filter { track ->
                track.playlistId == playlist.id
            }
            filteredPlaylists.add(playlist.copy(tracks = playlistTracks))
        }
        emit(filteredPlaylists.toList())
        delay(100)
    }

    fun getPlaylist(id: Long): Flow<Playlist?> = flow {
        emit(playlists.find { it.id == id })
    }

    fun addNewPlaylist(name: String, description: String) {
        playlists.add(
            Playlist(
                id = playlists.size.toLong() + 1,
                name = name,
                description = description,
                tracks = emptyList()
            )
        )
    }

    fun deletePlaylistById(playlistId: Long) {
        playlists.removeIf { it.id == playlistId }
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        scope.launch(Dispatchers.IO) {
            tracksMutex.withLock {
                val currentTracks = _tracks.value.toMutableList()
                currentTracks.removeIf { it.id == trackId }
                _tracks.value = currentTracks
            }
        }
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow {
        tracksMutex.withLock {
            val foundTrack = _tracks.value.find { existingTrack ->
                existingTrack.trackName.equals(track.trackName, ignoreCase = true) &&
                        existingTrack.artistName.equals(track.artistName, ignoreCase = true)
            }
            emit(foundTrack)
        }
    }

    fun insertTrack(track: Track) {
        scope.launch(Dispatchers.IO) {
            tracksMutex.withLock {
                val currentTracks = _tracks.value.toMutableList()

                val existingIndex = currentTracks.indexOfFirst { existingTrack ->
                    (existingTrack.id != 0L && existingTrack.id == track.id) ||
                            (existingTrack.trackName.equals(track.trackName, ignoreCase = true) &&
                                    existingTrack.artistName.equals(track.artistName, ignoreCase = true))
                }

                if (existingIndex != -1) {
                    val existingTrack = currentTracks[existingIndex]

                    val updatedTrack = track.copy(id = existingTrack.id)
                    currentTracks[existingIndex] = updatedTrack
                } else {

                    val newId = if (track.id == 0L) generateId() else track.id
                    val newTrack = track.copy(id = newId)
                    currentTracks.add(newTrack)

                    if (newId >= idCounter) {
                        idCounter = newId + 1
                    }
                }

                _tracks.value = currentTracks
            }
        }
    }

    fun getFavoriteTracks(): Flow<List<Track>> = flow {
        _tracks.collect { trackList ->
            tracksMutex.withLock {
                val favorites = trackList.filter { it.favorite }
                emit(favorites)
            }
        }
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        scope.launch(Dispatchers.IO) {
            tracksMutex.withLock {
                val currentTracks = _tracks.value.toMutableList()
                currentTracks.removeIf { it.playlistId == playlistId }
                _tracks.value = currentTracks
            }
        }
    }

    fun getTrackById(trackId: Long): Flow<Track?> = flow {
        tracksMutex.withLock {
            val track = _tracks.value.find { it.id == trackId }
            emit(track)
        }
    }

    fun searchTracks(expression: String): List<Track> {
        return _tracks.value.filter { it.trackName.contains(expression, true) }
    }

    fun deleteTrackFromPlaylist(track: Track) {
        scope.launch(Dispatchers.IO) {
            tracksMutex.withLock {
                val currentTracks = _tracks.value.toMutableList()
                currentTracks.removeIf {
                    it.trackName == track.trackName &&
                            it.artistName == track.artistName &&
                            it.playlistId == track.playlistId
                }
                _tracks.value = currentTracks
            }
        }
    }

    fun getTracksByPlaylistId(playlistId: Long): Flow<List<Track>> = flow {
        _tracks.collect { trackList ->
            tracksMutex.withLock {
                val playlistTracks = trackList.filter { it.playlistId == playlistId }
                emit(playlistTracks)
            }
        }
    }
}