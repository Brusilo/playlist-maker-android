package com.example.playlist_maker_android_brusilodiana.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val id: Long = 0,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val playlistId: Long = 0,
    val favorite: Boolean = false,
    val artworkUrl: String = ""
) : Parcelable