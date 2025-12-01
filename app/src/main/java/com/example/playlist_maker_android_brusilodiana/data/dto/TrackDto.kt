package com.example.playlist_maker_android_brusilodiana.data.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TrackDto(
    @SerializedName("trackId")
    val id: Long? = null,

    @SerializedName("trackName")
    val trackName: String? = null,

    @SerializedName("artistName")
    val artistName: String? = null,

    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long? = null,

    @SerializedName("previewUrl")
    val previewUrl: String? = null,

    @SerializedName("artworkUrl100")
    val artworkUrl100: String? = null
)