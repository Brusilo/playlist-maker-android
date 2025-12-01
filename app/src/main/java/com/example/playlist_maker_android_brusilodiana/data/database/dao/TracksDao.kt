package com.example.playlist_maker_android_brusilodiana.data.database.dao

import androidx.room.*
import com.example.playlist_maker_android_brusilodiana.data.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    @Query("SELECT * FROM tracks WHERE id = :trackId")
    fun getTrackById(trackId: Long): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks WHERE track_name = :trackName AND artist_name = :artistName")
    fun getTrackByNameAndArtist(trackName: String, artistName: String): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks WHERE favorite = 1 ORDER BY track_name")
    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("UPDATE tracks SET favorite = :isFavorite WHERE id = :trackId")
    suspend fun updateFavoriteStatus(trackId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM tracks WHERE external_id = :externalId")
    fun getTrackByExternalId(externalId: Long): Flow<TrackEntity?>

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks ORDER BY track_name")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("SELECT COUNT(*) FROM tracks WHERE track_name = :trackName AND artist_name = :artistName")
    suspend fun trackExists(trackName: String, artistName: String): Int

    @Query("SELECT * FROM tracks WHERE id IN (:trackIds) ORDER BY track_name")
    suspend fun getTracksByIds(trackIds: List<Long>): List<TrackEntity>
}