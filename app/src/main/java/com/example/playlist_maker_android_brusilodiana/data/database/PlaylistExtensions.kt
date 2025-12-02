package com.example.playlist_maker_android_brusilodiana.data.database

import com.example.playlist_maker_android_brusilodiana.data.database.entity.PlaylistEntity
import com.example.playlist_maker_android_brusilodiana.domain.models.Playlist
import com.example.playlist_maker_android_brusilodiana.domain.models.Track

fun PlaylistEntity.toPlaylist(tracks: List<Track> = emptyList()): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        coverImageUri = this.coverImageUri,
        tracks = tracks
    )
}

fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        coverImageUri = this.coverImageUri
    )
}