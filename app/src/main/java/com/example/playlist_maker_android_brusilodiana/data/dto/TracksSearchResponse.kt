package com.example.playlist_maker_android_brusilodiana.data.dto

import com.example.playlist_maker_android_brusilodiana.domain.models.BaseResponse

class TracksSearchResponse(
    val results: List<TrackDto>
) : BaseResponse()
