package com.example.playlist_maker_android_brusilodiana.data.repository

import android.content.Context
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.data.database.AppDatabase
import com.example.playlist_maker_android_brusilodiana.data.database.entity.PlaylistTrackJoin
import com.example.playlist_maker_android_brusilodiana.data.dto.TrackDto
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_brusilodiana.domain.NetworkClient
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context,
    private val database: AppDatabase
) : TracksRepository {

    private val tracksDao = database.tracksDao()
    private val playlistsDao = database.playlistsDao()
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
        withContext(Dispatchers.IO) {
            val existingTrack = tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName).first()
            if (existingTrack == null) {
                val trackEntity = track.toEntity().copy(
                    externalId = if (track.id > 0) track.id else null
                )
                tracksDao.insertTrack(trackEntity)
            } else {
                val updatedEntity = track.toEntity().copy(
                    id = existingTrack.id,
                    favorite = existingTrack.favorite,
                    externalId = if (track.id > 0) track.id else existingTrack.externalId
                )
                tracksDao.insertTrack(updatedEntity)
            }
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName)
            .map { entity -> entity?.toTrack() }
    }

    override fun getTrackById(trackId: Long): Flow<Track?> = flow {
        val trackFromSearch = searchResults.find { it.id == trackId }
        if (trackFromSearch != null) {
            emit(trackFromSearch)
        } else {
            tracksDao.getTrackById(trackId).collect { entity ->
                emit(entity?.toTrack())
            }
        }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        withContext(Dispatchers.IO) {
            val trackEntity = getOrCreateTrack(track)

            val join = PlaylistTrackJoin(playlistId = playlistId, trackId = trackEntity.id)
            playlistsDao.addTrackToPlaylist(join)
        }
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Long) {
        withContext(Dispatchers.IO) {
            val trackEntity = tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName).first()
            trackEntity?.let {
                playlistsDao.removeTrackFromPlaylist(playlistId, it.id)
            }
        }
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            val trackEntity = getOrCreateTrack(track)

            tracksDao.updateFavoriteStatus(trackEntity.id, isFavorite)

            val index = searchResults.indexOfFirst {
                it.trackName == track.trackName && it.artistName == track.artistName
            }
            if (index != -1) {
                searchResults[index] = searchResults[index].copy(favorite = isFavorite)
            }
        }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return tracksDao.getAllFavoriteTracks()
            .map { entities -> entities.map { it.toTrack() } }
    }

    override suspend fun saveTrack(track: Track, playlistId: Long?) {
        withContext(Dispatchers.IO) {
            val trackEntity = getOrCreateTrack(track)

            if (playlistId != null) {
                val join = PlaylistTrackJoin(playlistId = playlistId, trackId = trackEntity.id)
                playlistsDao.addTrackToPlaylist(join)
            }
        }
    }

    private suspend fun getOrCreateTrack(track: Track): com.example.playlist_maker_android_brusilodiana.data.database.entity.TrackEntity {
        val existingEntity = tracksDao.getTrackByNameAndArtist(track.trackName, track.artistName).first()

        return if (existingEntity != null) {
            val updatedEntity = track.toEntity().copy(
                id = existingEntity.id,
                favorite = existingEntity.favorite,
                externalId = existingEntity.externalId ?: if (track.id > 0) track.id else null
            )
            tracksDao.insertTrack(updatedEntity)
            updatedEntity
        } else {
            val newEntity = track.toEntity().copy(
                externalId = if (track.id > 0) track.id else null
            )
            val newId = tracksDao.insertTrack(newEntity)
            newEntity.copy(id = newId)
        }
    }
}

private fun com.example.playlist_maker_android_brusilodiana.data.database.entity.TrackEntity.toTrack(): Track {
    return Track(
        id = this.id,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = this.trackTime,
        favorite = this.favorite,
        artworkUrl = this.artworkUrl,
        previewUrl = this.previewUrl
    )
}

private fun Track.toEntity(): com.example.playlist_maker_android_brusilodiana.data.database.entity.TrackEntity {
    return com.example.playlist_maker_android_brusilodiana.data.database.entity.TrackEntity(
        id = this.id,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = this.trackTime,
        artworkUrl = this.artworkUrl,
        previewUrl = this.previewUrl,
        favorite = this.favorite,
        externalId = if (this.id > 0 && this.id != 0L) this.id else null
    )
}