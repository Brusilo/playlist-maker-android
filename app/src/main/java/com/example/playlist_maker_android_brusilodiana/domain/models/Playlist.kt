package com.example.playlist_maker_android_brusilodiana.domain.models

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverImageUri: String? = null,
    var tracks: List<Track>
)