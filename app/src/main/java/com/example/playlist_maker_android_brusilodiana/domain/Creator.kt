package com.example.playlist_maker_android_brusilodiana.creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.playlist_maker_android_brusilodiana.data.database.AppDatabase
import com.example.playlist_maker_android_brusilodiana.data.network.ITunesApiService
import com.example.playlist_maker_android_brusilodiana.data.network.RetrofitNetworkClient
import com.example.playlist_maker_android_brusilodiana.data.preferences.SearchHistoryPreferences
import com.example.playlist_maker_android_brusilodiana.data.repository.TracksRepositoryImpl
import com.example.playlist_maker_android_brusilodiana.data.repository.PlaylistsRepositoryImpl
import com.example.playlist_maker_android_brusilodiana.data.repository.SearchHistoryRepositoryImpl
import com.example.playlist_maker_android_brusilodiana.domain.PlaylistsRepository
import com.example.playlist_maker_android_brusilodiana.domain.SearchHistoryRepository
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

    private var appDatabase: AppDatabase? = null

    private var searchHistoryPreferences: SearchHistoryPreferences? = null

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

    fun getAppDatabase(context: Context): AppDatabase {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        return appDatabase!!
    }

    fun getSearchHistoryPreferences(context: Context): SearchHistoryPreferences {
        if (searchHistoryPreferences == null) {
            searchHistoryPreferences = SearchHistoryPreferences.create(context)
        }
        return searchHistoryPreferences!!
    }

    fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val preferences = getSearchHistoryPreferences(context)
        return SearchHistoryRepositoryImpl(preferences)
    }

    fun getTracksRepository(context: Context): TracksRepository {
        val apiService = getApiService()
        val networkClient = RetrofitNetworkClient(apiService, context)
        val database = getAppDatabase(context)
        return TracksRepositoryImpl(networkClient, context, database)
    }

    fun getPlaylistsRepository(context: Context): PlaylistsRepository {
        val database = getAppDatabase(context)
        return PlaylistsRepositoryImpl(context, database)
    }

    fun getPlaylistViewModelFactory(context: Context): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlaylistViewModel(context) as T
            }
        }
}