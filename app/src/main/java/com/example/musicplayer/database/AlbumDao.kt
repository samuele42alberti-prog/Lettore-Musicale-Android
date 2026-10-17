package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.musicplayer.model.Album
import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.RisultatoRicercaAlbum
import com.example.musicplayer.model.TipoAlbum

import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert
    suspend fun inserisci(album: Album): Long

    @Query("""
        SELECT * FROM album
        ORDER BY titolo COLLATE NOCASE ASC
    """)
    fun osservaPerNome(): Flow<List<Album>>

    @Query("""
        SELECT * FROM album
        ORDER BY dataImportazione DESC
    """)
    fun osservaPerData(): Flow<List<Album>>

    @Query("""
        UPDATE album
        SET titolo = :titolo
        WHERE id = :id
    """)
    suspend fun modificaTitolo(titolo: String, id: Long)

    @Query("""
        UPDATE album
        SET anno = :anno
        WHERE id = :id
    """)
    suspend fun modificaAnno(anno: Int, id: Long)

    @Query("""
        DELETE FROM album
        WHERE id = :id
    """)
    suspend fun elimina(id: Long)
    
    @Query("""
        SELECT a.* FROM album a
        WHERE a.titolo = :titolo
        AND a.tipoAlbum = :tipoAlbum
        AND (
            SELECT COUNT(DISTINCT aa.artistaId)
            FROM album_artisti aa
            WHERE aa.albumId = a.id
        ) = :numeroArtisti
        AND (
            SELECT COUNT(DISTINCT aa.artistaId)
            FROM album_artisti aa
            WHERE aa.albumId = a.id
            AND aa.artistaId IN (:artistiId)
        ) = :numeroArtisti
        LIMIT 1
    """)
    suspend fun cercaPerNomeEArtisti(
        titolo: String, 
        artistiId: List<Long>,
        numeroArtisti: Int,
        tipoAlbum: TipoAlbum
    ): Album?

    @Query("""
        SELECT * FROM album
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun cercaPerId(id: Long): Album?

    @Query("""
        SELECT * FROM canzoni
        WHERE albumId = :id
        ORDER BY numeroTraccia ASC
    """)
    suspend fun trovaCanzoniAlbum(id: Long): List<Canzone>

    @Query("""
        SELECT a.*,
            (
                CASE WHEN a.titolo LIKE '%' || :ricerca || '%' THEN 3 ELSE 0 END +
                CASE WHEN EXISTS (
                    SELECT 1
                    FROM album_artisti aa
                    JOIN artisti art ON aa.artistaId = art.id
                    WHERE aa.albumId = a.id
                    AND art.nome LIKE '%' || :ricerca || '%'
                ) THEN 5 ELSE 0 END
            ) AS punteggio
        FROM album a
        WHERE
            a.titolo LIKE '%' || :ricerca || '%'
            OR EXISTS (
                SELECT 1
                FROM album_artisti aa
                JOIN artisti art ON aa.artistaId = art.id
                WHERE aa.albumId = a.id
                AND art.nome LIKE '%' || :ricerca || '%'
            )
        ORDER BY punteggio DESC
    """)
    suspend fun ricercaSeparata(ricerca: String): List<RisultatoRicercaAlbum>

    @Query("""
        SELECT a.*,
            (
                CASE WHEN a.titolo LIKE '%' || :ricerca || '%' THEN 100 ELSE 0 END +
                CASE WHEN EXISTS (
                    SELECT 1
                    FROM album_artisti aa
                    JOIN artisti art ON aa.artistaId = art.id
                    WHERE aa.albumId = a.id
                    AND art.nome LIKE '%' || :ricerca || '%'
                ) THEN 100 ELSE 0 END
            ) AS punteggio
        FROM album a
        WHERE
            a.titolo LIKE '%' || :ricerca || '%'
            OR EXISTS (
                SELECT 1
                FROM album_artisti aa
                JOIN artisti art ON aa.artistaId = art.id
                WHERE aa.albumId = a.id
                AND art.nome LIKE '%' || :ricerca || '%'
            )
        ORDER BY punteggio DESC
    """)
    suspend fun ricercaIntera(ricerca: String): List<RisultatoRicercaAlbum>
}