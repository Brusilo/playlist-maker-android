package com.example.playlist_maker_android_brusilodiana.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.playlist_maker_android_brusilodiana.domain.models.Playlist
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

class DatabaseMock private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: DatabaseMock? = null

        fun getInstance(context: Context): DatabaseMock {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseMock(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_database", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val tracksKey = "tracks_data"
    private val idCounterKey = "id_counter"
    private val playlistsKey = "playlists_data"
    private val historyKey = "history_data"

    private val historyList = mutableListOf<String>().apply {
        addAll(loadHistoryFromStorage())
    }
    private val _historyUpdates = MutableSharedFlow<Unit>()
    private val playlists = mutableListOf<Playlist>().apply {
        addAll(loadPlaylistsFromStorage())
    }

    private val _tracks = MutableStateFlow(loadTracksFromStorage())
    val tracks: List<Track> get() = _tracks.value

    private val tracksMutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.IO)

    private var idCounter: Long = sharedPreferences.getLong(idCounterKey, 1)

    private fun generateId(): Long {
        val newId = idCounter
        idCounter++
        sharedPreferences.edit().putLong(idCounterKey, idCounter).apply()
        return newId
    }

    private fun loadTracksFromStorage(): MutableList<Track> {
        val tracksJson = sharedPreferences.getString(tracksKey, null)
        return if (tracksJson != null) {
            val type = object : TypeToken<MutableList<Track>>() {}.type
            gson.fromJson(tracksJson, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }
    }

    private fun loadPlaylistsFromStorage(): List<Playlist> {
        val playlistsJson = sharedPreferences.getString(playlistsKey, null)
        return if (playlistsJson != null) {
            val type = object : TypeToken<List<Playlist>>() {}.type
            gson.fromJson(playlistsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun loadHistoryFromStorage(): List<String> {
        val historyJson = sharedPreferences.getString(historyKey, null)
        return if (historyJson != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(historyJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun saveTracksToStorage(tracks: List<Track>) {
        val tracksJson = gson.toJson(tracks)
        sharedPreferences.edit().putString(tracksKey, tracksJson).apply()
    }

    private fun savePlaylistsToStorage() {
        val playlistsJson = gson.toJson(playlists)
        sharedPreferences.edit().putString(playlistsKey, playlistsJson).apply()
    }

    private fun saveHistoryToStorage() {
        val historyJson = gson.toJson(historyList)
        sharedPreferences.edit().putString(historyKey, historyJson).apply()
    }

    fun getHistory(): List<String> {
        return historyList.toList()
    }

    fun addToHistory(word: String) {
        historyList.add(word)
        saveHistoryToStorage()
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
        val playlist = playlists.find { it.id == id }
        if (playlist != null) {
            val playlistTracks = _tracks.value.filter { it.playlistId == id }
            emit(playlist.copy(tracks = playlistTracks))
        } else {
            emit(null)
        }
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
        savePlaylistsToStorage()
    }

    fun deletePlaylistById(playlistId: Long) {
        playlists.removeIf { it.id == playlistId }
        savePlaylistsToStorage()
    }

    suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        tracksMutex.withLock {
            val currentTracks = _tracks.value.toMutableList()

            val trackWithPlaylist = track.copy(
                id = if (track.id == 0L) generateId() else track.id,
                playlistId = playlistId
            )

            val existingIndex = currentTracks.indexOfFirst { existingTrack ->
                existingTrack.trackName.equals(track.trackName, ignoreCase = true) &&
                        existingTrack.artistName.equals(track.artistName, ignoreCase = true) &&
                        existingTrack.playlistId == playlistId
            }

            if (existingIndex != -1) {
                currentTracks[existingIndex] = trackWithPlaylist
            } else {
                currentTracks.add(trackWithPlaylist)
            }

            _tracks.value = currentTracks
            saveTracksToStorage(currentTracks)
        }
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        scope.launch(Dispatchers.IO) {
            tracksMutex.withLock {
                val currentTracks = _tracks.value.toMutableList()
                currentTracks.removeIf { it.id == trackId }
                _tracks.value = currentTracks
                saveTracksToStorage(currentTracks)
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
                        sharedPreferences.edit().putLong(idCounterKey, idCounter).apply()
                    }
                }

                _tracks.value = currentTracks
                saveTracksToStorage(currentTracks)
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
                saveTracksToStorage(currentTracks)
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
        val searchQuery = expression.trim().lowercase()
        if (searchQuery.isEmpty()) return emptyList()

        return _tracks.value
            .filter { track ->
                track.trackName.contains(searchQuery, ignoreCase = true) ||
                        track.artistName.contains(searchQuery, ignoreCase = true)
            }
            .sortedWith(compareBy<Track> { track ->
                val trackNameLower = track.trackName.lowercase()
                val artistNameLower = track.artistName.lowercase()

                when {
                    trackNameLower == searchQuery -> 1
                    artistNameLower == searchQuery -> 2
                    trackNameLower.startsWith(searchQuery) -> 3
                    artistNameLower.startsWith(searchQuery) -> 4
                    trackNameLower.split(" ").any { it.startsWith(searchQuery) } -> 5
                    artistNameLower.split(" ").any { it.startsWith(searchQuery) } -> 6
                    else -> 7
                }
            }.thenBy { it.trackName })
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
                saveTracksToStorage(currentTracks)
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