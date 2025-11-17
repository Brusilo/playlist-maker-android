package com.example.playlist_maker_android_brusilodiana.data.network

import com.example.playlist_maker_android_brusilodiana.data.network.Track
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_brusilodiana.domain.NetworkClient
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository
import kotlinx.coroutines.delay

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        delay(1000)

        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map {
                val seconds = it.trackTimeMillis / 1000
                val minutes = seconds / 60
                val formatted = "%02d:%02d".format(minutes, seconds % 60)

                Track(it.trackName, it.artistName, formatted)
            }
        } else emptyList()
    }
}