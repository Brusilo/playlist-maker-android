package com.example.playlist_maker_android_brusilodiana.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["track_name", "artist_name"], unique = true),
        Index(value = ["favorite"])
    ]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "track_name")
    val trackName: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "track_time")
    val trackTime: String,

    @ColumnInfo(name = "artwork_url")
    val artworkUrl: String,

    @ColumnInfo(name = "preview_url")
    val previewUrl: String = "",

    @ColumnInfo(name = "favorite")
    val favorite: Boolean = false,

    @ColumnInfo(name = "external_id")
    val externalId: Long? = null
)