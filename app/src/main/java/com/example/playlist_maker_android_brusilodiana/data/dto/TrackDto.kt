package com.example.playlist_maker_android_brusilodiana.data.dto

data class TrackDto(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String? = null
)