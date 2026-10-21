package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.musicplayer.model.Artista

import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistaDao {
    
    @Insert
    suspend fun inserisci(artista: Artista): Long

    @Query("SELECT * FROM artisti ORDER BY nome COLLATE NOCASE ASC")
    fun osservaPerNome(): Flow<List<Artista>>

    @Query("""
        UPDATE artisti
        SET nome = :nuovoNome
        WHERE id = :id
    """)
    suspend fun modificaNome(nuovoNome: String, id: Long)

    @Query("""
        DELETE FROM artisti
        WHERE id = :id
    """)
    suspend fun elimina(id: Long)

    @Query("SELECT * FROM artisti WHERE nome = :nome LIMIT 1")
    suspend fun cercaPerNome(nome: String): Artista?

    @Query("""
        SELECT *
        FROM artisti
        WHERE nome LIKE '%' || :ricerca || '%'
        ORDER BY nome COLLATE NOCASE ASC
    """)
    suspend fun ricerca(ricerca: String): List<Artista>

    @Query("""
        SELECT * FROM artisti
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun cercaPerId(id: Long): Artista

}