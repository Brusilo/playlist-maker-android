package com.example.playlist_maker_android_brusilodiana.data.database.dao

import androidx.room.*
import com.example.playlist_maker_android_brusilodiana.data.database.entity.PlaylistEntity
import com.example.playlist_maker_android_brusilodiana.data.database.entity.PlaylistTrackJoin
import com.example.playlist_maker_android_brusilodiana.data.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity?>

    @Query("SELECT * FROM playlists ORDER BY created_at DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Transaction
    @Query("""
        SELECT tracks.* FROM tracks 
        INNER JOIN playlist_track_join ON tracks.id = playlist_track_join.trackId 
        WHERE playlist_track_join.playlistId = :playlistId 
        ORDER BY tracks.track_name
    """)
    fun getTracksForPlaylist(playlistId: Long): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(playlistTrackJoin: PlaylistTrackJoin)

    @Query("DELETE FROM playlist_track_join WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_track_join WHERE playlistId = :playlistId")
    suspend fun removeAllTracksFromPlaylist(playlistId: Long)

    @Query("SELECT COUNT(*) FROM playlist_track_join WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Int

    @Query("SELECT COUNT(*) FROM playlist_track_join WHERE playlistId = :playlistId")
    suspend fun getTrackCountForPlaylist(playlistId: Long): Int
}