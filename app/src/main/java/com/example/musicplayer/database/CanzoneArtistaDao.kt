package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.musicplayer.model.CanzoneArtista

@Dao
interface CanzoneArtistaDao {

    @Insert
    suspend fun inserisci(canzoneArtista: CanzoneArtista): Long

    @Query("""
        DELETE FROM canzoni_artisti
        WHERE canzoneId = :canzoneId
    """)
    suspend fun eliminaCanzone(canzoneId: Long)

    @Query("""
        SELECT canzoneId
        FROM canzoni_artisti
        WHERE artistaId = :artistaId
    """)
    suspend fun trovaCanzoniArtista(artistaId: Long): List<Long>

    @Query("""
        SELECT artistaId
        FROM canzoni_artisti
        WHERE canzoneId = :canzoneId
    """)
    suspend fun trovaArtistiCanzone(canzoneId: Long): List<Long>
}