package com.example.playlist_maker_android_brusilodiana.data.database

import com.example.playlist_maker_android_brusilodiana.data.database.entity.TrackEntity
import com.example.playlist_maker_android_brusilodiana.domain.models.Track

fun TrackEntity.toTrack(): Track {
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

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
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