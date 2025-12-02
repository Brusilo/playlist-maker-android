package com.example.playlist_maker_android_brusilodiana.ui.view_model

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.creator.Creator
import com.example.playlist_maker_android_brusilodiana.domain.models.Playlist
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistViewModel(private val context: Context) : ViewModel() {
    private val playlistsRepository = Creator.getPlaylistsRepository(context)
    private val tracksRepository = Creator.getTracksRepository(context)

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

    fun removeTrackFromFavorites(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.updateTrackFavoriteStatus(track, false)
        }
    }

    fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlistsRepository.getPlaylist(playlistId)
    }

    suspend fun isTrackExist(track: Track): Track? {
        return tracksRepository.getTrackByNameAndArtist(track).first()
    }

    fun getTrackById(trackId: Long): Flow<Track?> {
        return tracksRepository.getTrackById(trackId)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistsRepository.deletePlaylistById(playlistId)
    }

    fun sharePlaylist(playlist: Playlist) {
        val message = context.getString(R.string.share_playlist_message, playlist.name)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}