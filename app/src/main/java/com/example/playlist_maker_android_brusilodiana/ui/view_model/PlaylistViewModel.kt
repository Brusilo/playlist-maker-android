package com.example.playlist_maker_android_brusilodiana.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_brusilodiana.creator.Creator
import com.example.playlist_maker_android_brusilodiana.domain.models.Playlist
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {
    private val playlistsRepository = Creator.getPlaylistsRepository()
    private val tracksRepository = Creator.getTracksRepository()

    val playlists: Flow<List<Playlist>> = playlistsRepository.getAllPlaylists()
    val favoriteTracks: Flow<List<Track>> = tracksRepository.getFavoriteTracks()

    fun createNewPlaylist(name: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(name, description)
        }
    }

    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        tracksRepository.insertTrackToPlaylist(track, playlistId)
    }

    fun toggleFavorite(track: Track, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
        }
    }

    suspend fun isTrackExist(track: Track): Track? {
        return tracksRepository.getTrackByNameAndArtist(track).first()
    }

    fun getTrackById(trackId: Long): Flow<Track?> {
        return tracksRepository.getTrackById(trackId)
    }
}