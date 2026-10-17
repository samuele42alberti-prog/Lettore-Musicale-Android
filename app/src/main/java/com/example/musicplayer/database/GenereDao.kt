package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query

import com.example.musicplayer.model.Genere

@Dao
interface GenereDao {

    @Insert
    suspend fun inserisci(genere: Genere): Long

    @Query("SELECT * FROM generi")
    suspend fun leggiTutti(): List<Genere>

    @Query("""
        SELECT * FROM generi
        WHERE nome = :nome
        LIMIT 1
    """)
    suspend fun cercaPerNome(nome: String): Genere?

    @Query("""
        SELECT * FROM generi 
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun cercaPerId(id: Long): Genere?

    @Delete
    suspend fun elimina(genere: Genere)
}