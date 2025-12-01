package com.example.playlist_maker_android_brusilodiana.data.network

import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search")
    suspend fun searchTracks(
        @Query("term") query: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 50
    ): TracksSearchResponse
}