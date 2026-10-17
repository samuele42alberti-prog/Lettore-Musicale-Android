package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.musicplayer.model.Ascolto

@Dao
interface AscoltoDao {

    @Insert 
    suspend fun inserisci(ascolto: Ascolto): Long

    @Query("""
        DELETE FROM ascolti
        WHERE canzoneId = :canzoneId
    """)
    suspend fun elimina(canzoneId: Long)

    @Query("SELECT * FROM ascolti")
    suspend fun leggiTutti(): List<Ascolto>

    @Query("""
        SELECT *
        FROM ascolti
        WHERE timestamp >= :limite
        ORDER BY timestamp ASC
    """)
    suspend fun leggiAscoltiRecenti(limite: Long): List<Ascolto>
}