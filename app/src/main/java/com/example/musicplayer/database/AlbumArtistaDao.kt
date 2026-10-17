package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.musicplayer.model.AlbumArtista

@Dao
interface AlbumArtistaDao {

    @Insert
    suspend fun inserisci(albumArtista: AlbumArtista): Long

    @Query("""
        DELETE FROM album_artisti
        WHERE albumId = :albumId
    """)
    suspend fun eliminaAlbum(albumId: Long)

    @Query("""
        SELECT albumId
        FROM album_artisti
        WHERE artistaId = :artistaId
    """)
    suspend fun trovaAlbumArtista(artistaId: Long): List<Long>

    @Query("""
        SELECT artistaId
        FROM album_artisti
        WHERE albumId = :albumId
    """)
    suspend fun trovaArtistiAlbum(albumId: Long): List<Long>

    @Query("""
        SELECT aa.albumId
        FROM album_artisti aa
        JOIN album a ON aa.albumid = a.id
        WHERE aa.artistaId = :artistaId
        ORDER BY a.anno DESC
    """)
    suspend fun trovaAlbumArtistaPerAnno(artistaId: Long): List<Long>
}