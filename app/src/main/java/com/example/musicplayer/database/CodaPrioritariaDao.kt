package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.musicplayer.model.CodaPrioritaria

@Dao
interface CodaPrioritariaDao {
    @Insert
    suspend fun inserisci(codaPrioritaria: CodaPrioritaria): Long

    @Query("""
        DELETE FROM coda_prioritaria
        WHERE id = :id
    """)
    suspend fun elimina(id: Int)

    @Query("""
        DELETE FROM coda_prioritaria
    """)
    suspend fun eliminaTutto()

    @Query("""
        SELECT * FROM coda_prioritaria
        ORDER BY posizione
    """)
    suspend fun leggiTutto(): List<CodaPrioritaria>
}