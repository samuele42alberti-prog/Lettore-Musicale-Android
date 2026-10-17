package com.example.musicplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import android.content.Context
import androidx.room.Room

import com.example.musicplayer.model.Artista
import com.example.musicplayer.model.Album
import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.Genere
import com.example.musicplayer.model.AlbumGenere
import com.example.musicplayer.model.AlbumArtista
import com.example.musicplayer.model.CanzoneArtista
import com.example.musicplayer.model.Ascolto

@TypeConverters(Converters::class)

@Database(
    entities = [
        Artista::class,
        Album::class,
        Canzone::class,
        Genere::class,
        AlbumGenere::class,
        AlbumArtista::class,
        CanzoneArtista::class,
        Ascolto::class
    ],
    version = 11
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun artistaDao(): ArtistaDao

    abstract fun albumDao(): AlbumDao

    abstract fun canzoneDao(): CanzoneDao

    abstract fun genereDao(): GenereDao

    abstract fun albumGenereDao(): AlbumGenereDao

    abstract fun albumArtistaDao(): AlbumArtistaDao

    abstract fun canzoneArtistaDao(): CanzoneArtistaDao

    abstract fun ascoltoDao(): AscoltoDao

    companion object {
        @Volatile
        private var INSTANCE: MusicDatabase? = null

        fun getDatabase(context: Context): MusicDatabase {

            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "music_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also {
                    INSTANCE = it
                }
            }
        }
    }
}