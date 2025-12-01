package com.example.playlist_maker_android_brusilodiana.creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker_android_brusilodiana.data.network.ITunesApiService
import com.example.playlist_maker_android_brusilodiana.data.network.RetrofitNetworkClient
import com.example.playlist_maker_android_brusilodiana.data.repository.TracksRepositoryImpl
import com.example.playlist_maker_android_brusilodiana.data.repository.PlaylistsRepositoryImpl
import com.example.playlist_maker_android_brusilodiana.domain.PlaylistsRepository
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository
import com.example.playlist_maker_android_brusilodiana.ui.view_model.PlaylistViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Creator {
    private var retrofit: Retrofit? = null
    private var apiService: ITunesApiService? = null

    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    private fun getApiService(): ITunesApiService {
        if (apiService == null) {
            apiService = getRetrofit().create(ITunesApiService::class.java)
        }
        return apiService!!
    }

    fun getTracksRepository(context: Context): TracksRepository {
        val apiService = getApiService()
        val networkClient = RetrofitNetworkClient(apiService, context)
        return TracksRepositoryImpl(networkClient, context)
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