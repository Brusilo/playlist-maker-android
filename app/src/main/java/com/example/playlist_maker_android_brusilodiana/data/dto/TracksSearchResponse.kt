package com.example.playlist_maker_android_brusilodiana.data.dto

import androidx.annotation.Keep
import com.example.playlist_maker_android_brusilodiana.domain.models.BaseResponse
import com.google.gson.annotations.SerializedName

@Keep
class TracksSearchResponse(
    @SerializedName("resultCount")
    val resultCount: Int = 0,

    @SerializedName("results")
    val results: List<TrackDto> = emptyList()
) : BaseResponse()