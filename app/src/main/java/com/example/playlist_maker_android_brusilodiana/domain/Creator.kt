package com.example.playlist_maker_android_brusilodiana.creator

import com.example.playlist_maker_android_brusilodiana.data.network.RetrofitNetworkClient
import com.example.playlist_maker_android_brusilodiana.data.network.TracksRepositoryImpl
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository

object Creator {
    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(Storage()))
    }
}