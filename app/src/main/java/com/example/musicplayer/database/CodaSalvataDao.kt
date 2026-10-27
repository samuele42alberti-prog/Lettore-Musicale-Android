package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.musicplayer.model.CodaSalvata

@Dao
interface CodaSalvataDao {
    @Insert
    suspend fun inserisci(codaSalvata: CodaSalvata): Long

    @Query("""
        DELETE FROM coda_salvata
        WHERE id = :id
    """)
    suspend fun elimina(id: Int)

    @Query("""
        DELETE FROM coda_salvata
    """)
    suspend fun eliminaTutto()

    @Query("""
        SELECT * FROM coda_salvata
        ORDER BY posizione
    """)
    suspend fun leggiTutto(): List<CodaSalvata>
}
