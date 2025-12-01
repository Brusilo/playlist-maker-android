package com.example.playlist_maker_android_brusilodiana.data.repository

import android.content.Context
import com.example.playlist_maker_android_brusilodiana.data.database.AppDatabase
import com.example.playlist_maker_android_brusilodiana.data.database.entity.PlaylistTrackJoin
import com.example.playlist_maker_android_brusilodiana.data.database.toTrack
import com.example.playlist_maker_android_brusilodiana.data.database.toEntity
import com.example.playlist_maker_android_brusilodiana.data.database.toPlaylist
import com.example.playlist_maker_android_brusilodiana.domain.PlaylistsRepository
import com.example.playlist_maker_android_brusilodiana.domain.models.Playlist
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.firstOrNull

class PlaylistsRepositoryImpl(
    private val context: Context,
    private val database: AppDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PlaylistsRepository {

    private val playlistsDao = database.playlistsDao()
    private val tracksDao = database.tracksDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return combine(
            playlistsDao.getPlaylistById(playlistId),
            playlistsDao.getTracksForPlaylist(playlistId)
        ) { playlistEntity, trackEntities ->
            if (playlistEntity == null) {
                null
            } else {
                val tracks = trackEntities.map { it.toTrack() }
                playlistEntity.toPlaylist(tracks)
            }
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistsDao.getAllPlaylists().flatMapLatest { playlistEntities ->
            if (playlistEntities.isEmpty()) {
                kotlinx.coroutines.flow.flowOf(emptyList<Playlist>())
            } else {
                val playlistFlows = playlistEntities.map { playlistEntity ->
                    playlistsDao.getTracksForPlaylist(playlistEntity.id).map { trackEntities ->
                        val tracks = trackEntities.map { it.toTrack() }
                        playlistEntity.toPlaylist(tracks)
                    }
                }
                combine(playlistFlows) { it.toList() }
            }
        }
    }

    override suspend fun addNewPlaylist(name: String, description: String) {
        withContext(Dispatchers.IO) {
            val playlistEntity = com.example.playlist_maker_android_brusilodiana.data.database.entity.PlaylistEntity(
                name = name,
                description = description
            )
            playlistsDao.insertPlaylist(playlistEntity)
        }
    }

    override suspend fun deletePlaylistById(id: Long) {
        withContext(Dispatchers.IO) {
            val playlist = playlistsDao.getPlaylistById(id).firstOrNull()
            playlist?.let {
                playlistsDao.removeAllTracksFromPlaylist(id)
                playlistsDao.deletePlaylist(it)
            }
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        withContext(Dispatchers.IO) {
            val trackEntity = tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName).firstOrNull()

            if (trackEntity != null) {
                val join = PlaylistTrackJoin(
                    playlistId = playlistId,
                    trackId = trackEntity.id
                )
                playlistsDao.addTrackToPlaylist(join)
            } else {
                val newTrackEntity = track.toEntity()
                val trackId = tracksDao.insertTrack(newTrackEntity)

                val join = PlaylistTrackJoin(
                    playlistId = playlistId,
                    trackId = trackId
                )
                playlistsDao.addTrackToPlaylist(join)
            }
        }
    }
}