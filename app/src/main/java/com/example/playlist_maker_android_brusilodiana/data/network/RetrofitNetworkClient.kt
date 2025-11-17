package com.example.playlist_maker_android_brusilodiana.data.network

import com.example.playlist_maker_android_brusilodiana.creator.Storage
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_brusilodiana.domain.NetworkClient

class RetrofitNetworkClient(
    private val storage: Storage
) : NetworkClient {

    override fun doRequest(dto: Any): TracksSearchResponse {
        val request = dto as TracksSearchRequest
        val tracks = storage.search(request.expression)
        return TracksSearchResponse(tracks).apply { resultCode = 200 }
    }
}
