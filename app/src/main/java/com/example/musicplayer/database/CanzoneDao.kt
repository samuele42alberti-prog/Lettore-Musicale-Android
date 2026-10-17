package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.RisultatoRicercaCanzone

import kotlinx.coroutines.flow.Flow

@Dao
interface CanzoneDao {

    @Insert
    suspend fun inserisci(canzone: Canzone): Long

    @Query("SELECT * FROM canzoni")
    fun osserva(): Flow<List<Canzone>>

    @Query("""
        DELETE FROM canzoni
        WHERE id = :id
    """)
    suspend fun elimina(id: Long)

    @Query("""
        SELECT * FROM canzoni
        WHERE percorsoFile = :percorsoFile
        LIMIT 1
    """)
    suspend fun cercaPerPercorso(percorsoFile: String): Canzone?

    @Query("""
        SELECT * FROM canzoni
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun cercaPerId(id: Long): Canzone?

    @Query("""
        SELECT c.*,
            (
                CASE WHEN c.titolo LIKE '%' || :ricerca || '%' THEN 2 ELSE 0 END +
                CASE WHEN EXISTS (
                    SELECT 1
                    FROM canzoni_artisti ca
                    JOIN artisti art ON ca.artistaId = art.id
                    WHERE ca.canzoneId = c.id
                    AND art.nome LIKE '%' || :ricerca || '%'
                ) THEN 5 ELSE 0 END +
                CASE WHEN c.metadatiArtista LIKE '%' || :ricerca || '%' THEN 2 ELSE 0 END +
                CASE WHEN al.titolo LIKE '%' || :ricerca || '%' THEN 3 ELSE 0 END
            ) AS punteggio
        FROM canzoni c
        LEFT JOIN album al ON al.id = c.albumId
        WHERE
            c.titolo LIKE '%' || :ricerca || '%'
            OR EXISTS (
                SELECT 1
                FROM canzoni_artisti ca
                JOIN artisti art ON ca.artistaId = art.id
                WHERE ca.canzoneId = c.id
                AND art.nome LIKE '%' || :ricerca || '%'
            )
            OR c.metadatiArtista LIKE '%' || :ricerca || '%'
            OR al.titolo LIKE '%' || :ricerca || '%'
        ORDER BY punteggio DESC
    """)
    suspend fun ricercaSeparata(ricerca: String): List<RisultatoRicercaCanzone>

    @Query("""
        SELECT c.*,
            (
                CASE WHEN c.titolo LIKE '%' || :ricerca || '%' THEN 100 ELSE 0 END +
                CASE WHEN EXISTS (
                    SELECT 1
                    FROM canzoni_artisti ca
                    JOIN artisti art ON ca.artistaId = art.id
                    WHERE ca.canzoneId = c.id
                    AND art.nome LIKE '%' || :ricerca || '%'
                ) THEN 100 ELSE 0 END +
                CASE WHEN c.metadatiArtista LIKE '%' || :ricerca || '%' THEN 100 ELSE 0 END +
                CASE WHEN al.titolo LIKE '%' || :ricerca || '%' THEN 100 ELSE 0 END
            ) AS punteggio
        FROM canzoni c
        LEFT JOIN album al ON al.id = c.albumId
        WHERE 
            c.titolo LIKE '%' || :ricerca || '%'
            OR EXISTS(
                SELECT 1
                FROM canzoni_artisti ca
                JOIN artisti art ON ca.artistaId = art.id
                WHERE ca.canzoneId = c.id
                AND art.nome LIKE '%' || :ricerca || '%'
            )
            OR c.metadatiArtista LIKE '%' || :ricerca || '%'
            OR al.titolo LIKE '%' || :ricerca || '%'
        ORDER BY punteggio DESC
    """)
    suspend fun ricercaIntera(ricerca: String): List<RisultatoRicercaCanzone>
}