package com.example.playlist_maker_android_brusilodiana.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playlist_maker_android_brusilodiana.data.database.dao.PlaylistsDao
import com.example.playlist_maker_android_brusilodiana.data.database.dao.TracksDao
import com.example.playlist_maker_android_brusilodiana.data.database.entity.PlaylistEntity
import com.example.playlist_maker_android_brusilodiana.data.database.entity.PlaylistTrackJoin
import com.example.playlist_maker_android_brusilodiana.data.database.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackJoin::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao
    abstract fun playlistsDao(): PlaylistsDao

    companion object {
        const val DATABASE_NAME = "playlist_maker.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = database
                database
            }
        }
    }
}