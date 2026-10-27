package com.example.musicplayer.repository

import com.example.musicplayer.database.*

import com.example.musicplayer.model.*

import java.time.LocalDate
import java.time.ZoneId

import kotlinx.coroutines.flow.Flow

import androidx.room.withTransaction

class MusicRepository (
    private val database: MusicDatabase,
    private val artistaDao: ArtistaDao,
    private val albumDao: AlbumDao,
    private val canzoneDao: CanzoneDao,
    private val genereDao: GenereDao,
    private val albumGenereDao: AlbumGenereDao,
    private val albumArtistaDao: AlbumArtistaDao,
    private val canzoneArtistaDao: CanzoneArtistaDao,
    private val ascoltoDao: AscoltoDao,
    private val codaSalvataDao: CodaSalvataDao,
    private val codaPrioritariaDao: CodaPrioritariaDao,
    private val statoPlayerDao: StatoPlayerDao
) {
    //COLLEGAMENTI

    suspend fun inserisciAlbumGenere(albumId: Long, genereId: Long) {
        val nuovoAlbumGenere = AlbumGenere(albumId = albumId, genereId = genereId)
        albumGenereDao.inserisci(nuovoAlbumGenere)
    }

    suspend fun inserisciAlbumArtista(albumId: Long, artistaId: Long) {
        val nuovoAlbumArtista = AlbumArtista(albumId = albumId, artistaId = artistaId)
        albumArtistaDao.inserisci(nuovoAlbumArtista)
    }

    suspend fun eliminaAlbumArtista(albumId: Long) {
        albumArtistaDao.eliminaAlbum(albumId)
    }

    suspend fun eliminaCanzoneArtista(canzoneId: Long) {
        canzoneArtistaDao.eliminaCanzone(canzoneId)
    }

    //ARTISTA

    suspend fun inserisciArtista(nome: String): Long {
        val artista = cercaArtistaPerNome(nome)
        
        if(artista != null) {
            return artista.id
        }

        val nuovoArtista = Artista(nome = nome)
        return artistaDao.inserisci(nuovoArtista)
    }

    fun osservaArtisti(): Flow<List<Artista>> {
        return artistaDao.osservaPerNome()
    }

    suspend fun cercaArtistaPerId(id: Long): Artista? {
        return artistaDao.cercaPerId(id)
    }

    suspend fun cercaArtistaPerNome(nome: String): Artista? {
        return artistaDao.cercaPerNome(nome)
    }

    suspend fun cercaArtistiPerCanzoneId(canzoneId: Long): List<Artista> {
        val artistiId = canzoneArtistaDao.trovaArtistiCanzone(canzoneId)

        val artisti = mutableListOf<Artista>()

        artistiId.forEach{ id ->
            val artista = artistaDao.cercaPerId(id)?.let { artisti.add(it) }
        }
        return artisti
    }

    suspend fun cercaArtistiPerAlbumId(albumId: Long): List<Artista> {
        val artistiId = albumArtistaDao.trovaArtistiAlbum(albumId)

        val artisti = mutableListOf<Artista>()

        artistiId.forEach{ id ->
            val artista = artistaDao.cercaPerId(id)?.let { artisti.add(it) }
        }
        return artisti
    }

    suspend fun ricercaArtista(ricerca: String): List<Artista> {
        return artistaDao.ricerca(ricerca)
    }

    suspend fun modificaCampiArtista(
        nuovoNome: String,
        artistaId: Long
    ) {
        artistaDao.modificaNome(nuovoNome, artistaId)
    }

    suspend fun eliminaArtista(id: Long) {
        artistaDao.elimina(id)
    }

    //ALBUM

    fun osservaAlbumPerNome(): Flow<List<Album>> {
        return albumDao.osservaPerNome()
    }

    fun osservaAlbumPerData(): Flow<List<Album>> {
        return albumDao.osservaPerData()
    }

    suspend fun eliminaAlbum(id: Long) {
        albumDao.elimina(id)
    }

    suspend fun eliminaAlbumCompleto(id: Long): Boolean {
        return try {
            database.withTransaction {
                val canzoni = trovaCanzoniAlbum(id)

                canzoni.forEach{ canzone ->
                    eliminaAscolto(canzone.id)
                    eliminaCanzoneArtista(canzone.id)
                    eliminaCanzone(canzone.id)
                }

                eliminaAlbumArtista(id)
                eliminaAlbum(id)
            }

            true
        } catch( e: Exception) {
            false
        } 
    }

    suspend fun inserisciAlbum(
        titolo: String, 
        artistiId: List<Long>, 
        copertinaImportata: String?,
        anno: Int,
        tipoAlbum: TipoAlbum
    ): Long = database.withTransaction {
        val nuovoAlbum = Album(
            titolo = titolo, 
            percorsoCopertina = copertinaImportata,
            anno = anno,
            dataImportazione = System.currentTimeMillis(),
            tipoAlbum = tipoAlbum
        )

        val nuovoAlbumId = albumDao.inserisci(nuovoAlbum)

        artistiId.forEach{ id ->
            val nuovoAlbumArtista = AlbumArtista(nuovoAlbumId, id)
            albumArtistaDao.inserisci(nuovoAlbumArtista)
        }

        nuovoAlbumId
    }

    suspend fun trovaAlbumArtistaPerAnno(artista: Artista): List<Long> {
        return albumArtistaDao.trovaAlbumArtistaPerAnno(artista.id)
    }

    suspend fun controllaEsistenzaAlbum(
        titolo: String, 
        artistiId: List<Long>, 
        tipoAlbum: TipoAlbum,
        albumDaEscludere: Long?
    ): Boolean {
        val album = albumDao.cercaPerNomeEArtisti(titolo, artistiId, artistiId.size, tipoAlbum)

        return album != null && album.id != albumDaEscludere
    }

    suspend fun modificaCampiAlbum(
        nuovoTitolo: String, 
        nuoviArtisti: List<Artista>, 
        nuovoAnno: Int, 
        canzoniAlbum: List<Canzone>,
        albumId: Long
    ) {
        database.withTransaction{
            albumDao.modificaTitolo(nuovoTitolo, albumId)
            albumDao.modificaAnno(nuovoAnno, albumId)

            sostituisciArtistiAlbum(nuoviArtisti, albumId)

            canzoniAlbum.forEach{ canzone ->
                sostituisciArtistiCanzone(nuoviArtisti, canzone.id)
            }
        }
    }

    suspend fun sostituisciArtistiAlbum(nuoviArtisti: List<Artista>, albumId: Long) {
        albumArtistaDao.eliminaAlbum(albumId)

        nuoviArtisti.forEach{ artista ->
            val albumArtista = AlbumArtista(
                albumId,
                artista.id
            )

            albumArtistaDao.inserisci(albumArtista)
        }
    }

    suspend fun cercaAlbumPerId(id: Long): Album? {
        return albumDao.cercaPerId(id)
    }

    suspend fun trovaCanzoniAlbum(id: Long): List<Canzone> {
        return albumDao.trovaCanzoniAlbum(id)
    }

    suspend fun ricercaAlbum(ricercaSeparata: List<String>, ricercaIntera: String): List<Album> {
        val paroleTrovate = mutableMapOf<Album, Int>()
        val punteggi = mutableMapOf<Album, Int>()

        ricercaSeparata.forEach{ parola ->
            val albumParola = albumDao.ricercaSeparata(parola)

            albumParola.forEach{ risultato ->
                paroleTrovate[risultato.album] = (paroleTrovate[risultato.album] ?: 0) + 1
                punteggi[risultato.album] = (punteggi[risultato.album] ?: 0) + risultato.punteggio
            }
        }

        val albumTestoIntero = albumDao.ricercaIntera(ricercaIntera)

        albumTestoIntero.forEach{ risultato ->
            punteggi[risultato.album] = (punteggi[risultato.album] ?: 0) + risultato.punteggio
        }

        return paroleTrovate
            .filter { it.value == ricercaSeparata.size }
            .keys
            .sortedByDescending { punteggi[it] ?: 0 }
    }

    //GENERE

    suspend fun inserisciGenere(nome: String): Long {
        val genere = genereDao.cercaPerNome(nome)

        if(genere != null) {
            return genere.id
        }

        val nuovoGenere = Genere(nome = nome)
        return genereDao.inserisci(nuovoGenere)
    }

    suspend fun leggiTuttiGeneri(): List<Genere> {
        return genereDao.leggiTutti()
    }

    suspend fun cercaGenerePerId(id: Long): Genere? {
        return genereDao.cercaPerId(id)
    }

    //CANZONE

    suspend fun inserisciCanzone(
        titolo: String, 
        durata: Int, 
        percorsoFile: String, 
        metadatiArtista: String,
        artistiId: List<Long>, 
        albumId: Long?,
        numeroTraccia: Int?
    ): Long {
        val canzone = canzoneDao.cercaPerPercorso(percorsoFile)

        if(canzone != null) {
            return canzone.id
        }

        val nuovaCanzone = Canzone(
            titolo = titolo,
            durata = durata, 
            percorsoFile = percorsoFile,
            metadatiArtista = metadatiArtista,
            albumId = albumId,
            numeroTraccia = numeroTraccia
        )

        val nuovaCanzoneId = canzoneDao.inserisci(nuovaCanzone)

        artistiId.forEach{ id ->
            val nuovaCanzoneArtista = CanzoneArtista(nuovaCanzoneId, id)
            canzoneArtistaDao.inserisci(nuovaCanzoneArtista)
        }

        return nuovaCanzoneId
    }

    fun osservaCanzoni(): Flow<List<Canzone>> {
        return canzoneDao.osserva()
    }

    suspend fun eliminaCanzone(id: Long) {
        canzoneDao.elimina(id)
    }

    suspend fun sostituisciArtistiCanzone(nuoviArtisti: List<Artista>, canzoneId: Long) {
        canzoneArtistaDao.eliminaCanzone(canzoneId)

        nuoviArtisti.forEach{ artista ->
            val canzoneArtista = CanzoneArtista(
                canzoneId,
                artista.id
            )

            canzoneArtistaDao.inserisci(canzoneArtista)
        }
    }

    suspend fun cercaCanzonePerId(id: Long): Canzone? {
        return canzoneDao.cercaPerId(id)
    }

    suspend fun ricercaCanzoni(ricercaSeparata: List<String>, ricercaIntera: String): List<Canzone> {
        val paroleTrovate = mutableMapOf<Canzone, Int>()
        val punteggi = mutableMapOf<Canzone, Int>()

        ricercaSeparata.forEach{ parola ->
            val canzoniParola = canzoneDao.ricercaSeparata(parola)

            canzoniParola.forEach{ risultato ->
                paroleTrovate[risultato.canzone] = (paroleTrovate[risultato.canzone] ?: 0) + 1
                punteggi[risultato.canzone] = (punteggi[risultato.canzone] ?: 0) + risultato.punteggio
            }
        }

        val canzoniTestoIntero = canzoneDao.ricercaIntera(ricercaIntera)

        canzoniTestoIntero.forEach{ risultato ->
            punteggi[risultato.canzone] = (punteggi[risultato.canzone] ?: 0) + risultato.punteggio
        }

        return paroleTrovate
            .filter { it.value == ricercaSeparata.size }
            .keys
            .sortedByDescending { punteggi[it] ?: 0 }
    }

    //ASCOLTO

    suspend fun inserisciAscolto(canzoneId: Long, timestamp: Long): Long {
        val ascolto = Ascolto(
            canzoneId = canzoneId,
            timestamp = timestamp
        )

        return ascoltoDao.inserisci(ascolto)
    }

    suspend fun eliminaAscolto(canzoneId: Long) {
        ascoltoDao.elimina(canzoneId)
    }

    suspend fun leggiAscoltiRecenti(): List<Ascolto> {
        val limite = LocalDate
            .now()
            .minusDays(6)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return ascoltoDao.leggiAscoltiRecenti(limite)
    }

    //IMPORTAZIONE

    suspend fun importaCanzone(
        metadati: MetadatiCanzone,
        albumId: Long?,
        artistiId: List<Long>
    ): Long {
        return inserisciCanzone(
            metadati.titolo,
            metadati.durata,
            metadati.percorsoFile,
            metadati.artista,
            artistiId,
            albumId,
            metadati.numeroTraccia
        )
    }

    suspend fun importaAlbum(
        albumProposto: String, 
        artistiId: List<Long>,
        generiSelezionati: List<Genere>,
        copertinaImportata: String?,
        anno: Int,
        tipoAlbum: TipoAlbum
    ): Long? {
        val albumId = inserisciAlbum(albumProposto, artistiId, copertinaImportata, anno, tipoAlbum)

        generiSelezionati.forEach { genere ->
            inserisciAlbumGenere(albumId, genere.id)
        }

        return albumId
    }

    //CODA SALVATA

    suspend fun salvaCoda(canzoni: List<Canzone>) = database.withTransaction {
        codaSalvataDao.eliminaTutto()

        canzoni.forEachIndexed { indice, canzone ->
            val elemento = CodaSalvata(canzoneId = canzone.id, posizione = indice)

            codaSalvataDao.inserisci(elemento)
        }
    }

    suspend fun leggiCodaSalvata(): List<CodaSalvata> {
        return codaSalvataDao.leggiTutto()
    }

    //CODA PRIORITARIA

    suspend fun salvaCodaPrioritaria(canzoni: Map<Int, Canzone>) = database.withTransaction {
        codaPrioritariaDao.eliminaTutto()

        canzoni.forEach { (indice, canzone) ->
            val elemento = CodaPrioritaria(canzoneId = canzone.id, posizione = indice)

            codaPrioritariaDao.inserisci(elemento)
        }
    }

    suspend fun leggiCodaPrioritaria(): List<CodaPrioritaria> {
        return codaPrioritariaDao.leggiTutto()
    }

    //STATO PLAYER
    suspend fun salvaStatoPlayer(
        canzoneId: Long?,
        indiceCorrente: Int,
        posizione: Long
    ) {
        val stato = StatoPlayer(
            id = 1,
            canzoneId = canzoneId, 
            indiceCorrente = indiceCorrente, 
            posizione = posizione
        )

        statoPlayerDao.inserisci(stato)
    }

    suspend fun leggiStatoPlayer(): StatoPlayer? {
        return statoPlayerDao.leggi()
    }
}