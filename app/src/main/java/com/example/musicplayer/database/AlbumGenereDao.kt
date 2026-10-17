package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.musicplayer.model.AlbumGenere

@Dao
interface AlbumGenereDao {

    @Insert
    suspend fun inserisci(albumGenere: AlbumGenere): Long

    @Query("""
        SELECT albumId
        FROM album_generi
        WHERE genereId = :genereId
    """)
    suspend fun trovaAlbumGenere(genereId: Long): List<Long>

    @Query("""
        SELECT genereId
        FROM album_generi
        WHERE albumId = :albumId
    """)
    suspend fun trovaGeneriAlbum(albumId: Long): List<Long>
}