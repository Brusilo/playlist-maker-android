package com.example.playlist_maker_android_brusilodiana.creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker_android_brusilodiana.data.network.RetrofitNetworkClient
import com.example.playlist_maker_android_brusilodiana.data.network.TracksRepositoryImpl
import com.example.playlist_maker_android_brusilodiana.data.repository.PlaylistsRepositoryImpl
import com.example.playlist_maker_android_brusilodiana.domain.PlaylistsRepository
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository
import com.example.playlist_maker_android_brusilodiana.ui.view_model.PlaylistViewModel

object Creator {
    fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(Storage()), context)
    }

    fun getPlaylistsRepository(context: Context): PlaylistsRepository {
        return PlaylistsRepositoryImpl(context)
    }

    fun getPlaylistViewModelFactory(context: Context): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlaylistViewModel(context) as T
            }
        }
}