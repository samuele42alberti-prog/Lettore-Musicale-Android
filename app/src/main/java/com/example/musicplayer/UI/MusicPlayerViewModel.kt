package com.example.musicplayer.UI

import android.app.Application
import android.net.Uri

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.mutableStateListOf

import androidx.compose.foundation.lazy.LazyListState

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.common.MediaItem

import androidx.documentfile.provider.DocumentFile

import com.example.musicplayer.database.MusicDatabase
import com.example.musicplayer.repository.MusicRepository
import com.example.musicplayer.model.*

import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.collect

import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


enum class SchermataPrincipale {
    LIBRERIA,
    RICERCA,
    CRONOLOGIA,
    IMPORTAZIONE,
    LISTA_ARTISTI
}

enum class SchermataSecondaria {
    ALBUM,
    ARTISTA,
    PLAYER,
    CODA
}

class MusicPlayerViewModel(application: Application): AndroidViewModel(application) {

    private val playerConnection = PlayerConnection(application)
    private var mediaController: MediaController? = null

    private val repository: MusicRepository

    private var playerController: PlayerController? = null

    private var ricercaGlobaleJob: Job? = null
    private var ricercaArtistaJob: Job? = null

    //SLIDER
    var trascinandoSlider = mutableStateOf(false)
    var posizioneAttuale = mutableStateOf(0L)

    //CANZONE ATTUALE
    var canzoneSelezionata = mutableStateOf<Canzone?>(null)
    var albumSelezionato = mutableStateOf<Album?>(null)
    var artistiCanzoneAttuale = mutableStateOf<List<Artista>>(emptyList())

    //LISTE DI CANZONI
    var canzoniInCoda = mutableStateOf<List<Canzone>>(emptyList())
    var canzoniAlbum = mutableStateOf<List<Canzone>>(emptyList())
    var canzoni = mutableStateOf<List<Canzone>>(emptyList())
    
    //LISTA ALBUM
    var album = mutableStateOf<List<Album>>(emptyList())
    var ordinaAlbumPerNome = mutableStateOf(false)
    var posizioneListaAlbum = mutableStateOf(0)
    var offsetListaAlbum = mutableStateOf(0)

    //SCHERMATA ALBUM
    var albumCliccato = mutableStateOf<Album?>(null)
    var artistiAlbumCliccato = mutableStateOf<List<Artista>>(emptyList())

    //SCHERMATA ARTISTA
    var artistaCliccato = mutableStateOf<Artista?>(null)
    var albumPiuRecente = mutableStateOf<Album?>(null)
    var albumArtista = mutableStateOf<List<Album>>(emptyList())

    //ARTISTI
    var artisti = mutableStateOf<List<Artista>>(emptyList())
    var artistiCanzoni = mutableStateOf<Map<Canzone, List<Artista>>>(mapOf())
    var artistiAlbums = mutableStateOf<Map<Long, List<Artista>>>(mapOf())
    val lazyListStateListaArtisti = LazyListState()

    //IMPORTAZIONE ALBUM
    var importazioneInCorso = mutableStateOf(false)
    var listaMetadati = mutableStateOf<List<MetadatiCanzone>>(emptyList())
    var copertinaImportata = mutableStateOf<Uri?>(null)
    var albumProposto = mutableStateOf("")
    val tipoAlbum = mutableStateOf(TipoAlbum.ALBUM)
    var artistiSelezionati = mutableStateOf<List<Artista>>(emptyList())
    var artistiTrovati = mutableStateOf<List<Artista>>(emptyList())
    var ricercaArtista = mutableStateOf("")
    var generi = mutableStateOf<List<Genere>>(emptyList())
    var generiSelezionati = mutableStateOf<List<Genere>>(emptyList())
    var annoUscita = mutableStateOf<String>("")
    var titoloCanzoneImportata = mutableStateOf("")
    var nuovoGenere = mutableStateOf("")
    var erroreImportazione = mutableStateOf<String?>(null)

    //STATO
    var riproducendo = mutableStateOf(false)

    //SCHERMATE UI
    val stackSchermate = mutableStateListOf<SchermataSecondaria>()
    var schermataPrincipale = mutableStateOf(SchermataPrincipale.LIBRERIA)
    val albumAperti = mutableStateListOf<Album>()
    val artistiAperti = mutableStateListOf<Artista>()

    //RICERCA GLOBALE
    var testoRicercaGlobale = mutableStateOf("")
    var albumRicerca = mutableStateOf<List<Album>>(emptyList())
    var canzoniRicerca = mutableStateOf<List<Canzone>>(emptyList())
    val lazyListStateRicerca = LazyListState()

    //ASCOLTI RECENTI
    val ascoltiRecentiMap = LinkedHashMap<String, MutableList<Canzone>>()

    //MODIFICA ALBUM
    var inModifica = mutableStateOf(false)
    var titoloModificato = mutableStateOf("")
    var annoModificato = mutableStateOf("")
    var erroreModifica = mutableStateOf<String?>(null)
    
    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(
            mediaItem: MediaItem?,
            reason: Int
        ) {
            posizioneAttuale.value = playerController?.posizioneAttuale() ?: 0L

            val nuovaCanzone = sincronizzaCanzoneCorrente()
            if(nuovaCanzone != null) {
                aggiungiAdAscoltiRecenti(nuovaCanzone)
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            riproducendo.value = isPlaying
        }
    }

    init {
        val database = MusicDatabase.getDatabase(application)
        repository = MusicRepository(
            database,
            database.artistaDao(),
            database.albumDao(),
            database.canzoneDao(),
            database.genereDao(),
            database.albumGenereDao(),
            database.albumArtistaDao(),
            database.canzoneArtistaDao(),
            database.ascoltoDao()
        )
        osservaAlbum()
        osservaCanzoni()
        caricaAscolti()
        osservaArtisti()
        caricaGeneri()

        playerConnection.controllerFuture.addListener({
            mediaController = playerConnection.controllerFuture.get()
            mediaController?.addListener(playerListener)

            riproducendo.value = mediaController?.isPlaying == true

            playerController = PlayerController(mediaController!!)

            sincronizzaCoda()
        }, Runnable::run)
    }

    override fun onCleared() {
        mediaController?.removeListener(playerListener)
        mediaController?.release()
        super.onCleared()
    }

    fun onModificaAlbum() {
        if(inModifica.value) {
            erroreModifica.value = null

            if(titoloModificato.value.isBlank()) {
                erroreModifica.value = "Inserisci un titolo per l'album/singolo"
                return
            }

            if(annoModificato.value.isBlank()) {
                erroreModifica.value = "Inserisci l'anno di uscita"
                return
            } 

            if(artistiSelezionati.value.isEmpty()) {
                erroreModifica.value = "Inserisci almeno un artista"
                return
            } 

            val artistiId = artistiSelezionati.value.map { it.id }

            viewModelScope.launch {
                if(repository.controllaEsistenzaAlbum(
                    titoloModificato.value, 
                    artistiId, 
                    albumCliccato.value!!.tipoAlbum,
                    albumCliccato.value!!.id
                )) {
                    erroreModifica.value = "Questo album/singolo è già presente nella libreria"
                    return@launch
                }

                modificaCampiAlbum(titoloModificato.value, annoModificato.value)

                annullaModifica()
            }
        } else {
            titoloModificato.value = albumCliccato.value?.titolo ?: ""
            annoModificato.value = albumCliccato.value?.anno.toString() ?: ""
            artistiSelezionati.value = artistiAlbumCliccato.value
            inModifica.value = true
        }
    }

    fun annullaModifica() {
        inModifica.value = false
        artistiTrovati.value = emptyList()
        artistiSelezionati.value = emptyList()
        ricercaArtista.value = ""
        erroreModifica.value = null
    }

    suspend fun modificaCampiAlbum(nuovoTitolo: String, nuovoAnno: String) {
        val albumCorrente = albumCliccato.value ?: return

        val titolo = nuovoTitolo.trim()
        val anno = nuovoAnno.toIntOrNull() ?: return

        repository.modificaCampiAlbum(
            titolo, 
            artistiSelezionati.value, 
            anno, 
            canzoniAlbum.value,
            albumCorrente.id
        )

        val albumModificato = albumCorrente.copy(
            titolo = titolo,
            anno = anno
        )

        caricaCanzoniAlbum(albumModificato)
        caricaArtistiCanzoni(canzoni.value)
        caricaArtistiAlbum(album.value)

        val canzone = canzoniAlbum.value.find {
            it.id == canzoneSelezionata.value?.id
        }

        if(canzone != null) {
            aggiornaDatiCanzone(canzone)
        }
    }

    suspend fun eliminaAlbum(album: Album): Boolean {

        val annullaRiproduzione = canzoniAlbum.value.contains(canzoneSelezionata.value)

        val eliminato = repository.eliminaAlbumCompleto(album.id)
        
        if(eliminato) {
            caricaAscolti()
            
            if(annullaRiproduzione) {
                annullaCanzoneRiprodotta()
            }
        }

        return eliminato
    }

    fun eliminaArtista(): Boolean {
        val id = artistaCliccato.value?.id ?: return false

        if(albumArtista.value.size > 0) return false

        viewModelScope.launch {
            repository.eliminaArtista(id)
        }
        return true
    }

    fun apriArtista(artista: Artista) {
        caricaArtistaEAlbum(artista)
        apriSchermata(SchermataSecondaria.ARTISTA)
    }

    fun azzeraPosizioneListaArtisti() {
        viewModelScope.launch {
            lazyListStateListaArtisti.scrollToItem(0)
        }
    }

    fun caricaArtistaEAlbum(artista: Artista) {
        viewModelScope.launch{
            artistaCliccato.value = artista
            albumArtista.value = emptyList()

            val albumArtistaId = repository.trovaAlbumArtistaPerAnno(artista)

            albumArtistaId.forEach{ id ->
                val album = repository.cercaAlbumPerId(id)

                if(album != null) {
                    albumArtista.value = albumArtista.value + album
                }
            }
            
            albumPiuRecente.value = albumArtista.value.firstOrNull()
        }
    }

    fun cambiaSchermata(schermata: SchermataPrincipale) {
        schermataPrincipale.value = schermata
    }

    fun apriSchermata(schermata: SchermataSecondaria) {
        stackSchermate.add(schermata)
        if(schermata == SchermataSecondaria.ALBUM) {
            albumAperti.add(albumCliccato.value!!)
        } else if (schermata == SchermataSecondaria.ARTISTA) {
            artistiAperti.add(artistaCliccato.value!!)
        }
    }

    fun tornaIndietroSchermata() {
        if(stackSchermate.isNotEmpty()) {

            if(stackSchermate.lastOrNull() == SchermataSecondaria.ALBUM && albumAperti.isNotEmpty()) {
                albumAperti.removeAt(albumAperti.lastIndex)
            } else if (stackSchermate.lastOrNull() == SchermataSecondaria.ARTISTA && artistiAperti.isNotEmpty()) {
                artistiAperti.removeAt(artistiAperti.lastIndex)
            }
            stackSchermate.removeAt(stackSchermate.lastIndex)

            if(stackSchermate.lastOrNull() == SchermataSecondaria.ALBUM && albumAperti.isNotEmpty()) {
                val indice = albumAperti.lastIndex
                caricaCanzoniAlbum(albumAperti[indice])
            } else if (stackSchermate.lastOrNull() == SchermataSecondaria.ARTISTA && artistiAperti.isNotEmpty()) {
                val indice = artistiAperti.lastIndex
                caricaArtistaEAlbum(artistiAperti[indice])
            }
        }
    }

    fun chiudiTutteSchermateSecondarie() {
        stackSchermate.clear()
        albumAperti.clear()
        artistiAperti.clear()
    }

    private fun sincronizzaCoda() {

        viewModelScope.launch{
            val idCanzoniInCoda = playerController?.idCanzoniInCoda() ?: return@launch

            val coda = idCanzoniInCoda.mapNotNull { id ->
                repository.cercaCanzonePerId(id)
            }

            canzoniInCoda.value = coda

            sincronizzaCanzoneCorrente()
        }
    }

    private fun sincronizzaCanzoneCorrente(): Canzone? {
        val indice = playerController?.indiceCanzoneCorrente() ?: return null

        if(indice < 0 || indice >= canzoniInCoda.value.size) {
            return null
        }

        val canzone = canzoniInCoda.value[indice]

        aggiornaDatiCanzone(canzone)

        return canzone
    }

    fun aggiungiAdAscoltiRecenti(canzone: Canzone) {
        val oggi = LocalDate.now().toString()

        if(ascoltiRecentiMap.containsKey(oggi)) {
            ascoltiRecentiMap[oggi]?.add(canzone)
        } else {
            ascoltiRecentiMap[oggi] = mutableListOf(canzone)
        }

        viewModelScope.launch{
            val timestamp = System.currentTimeMillis()
            repository.inserisciAscolto(canzone.id, timestamp)
        }
    }

    private fun aggiornaDatiCanzone(canzone: Canzone) {
        canzoneSelezionata.value = canzone

        posizioneAttuale.value = playerController?.posizioneAttuale() ?: 0L

        viewModelScope.launch {
            artistiCanzoneAttuale.value = repository.cercaArtistiPerCanzoneId(canzone.id)
            albumSelezionato.value = canzone.albumId?.let { repository.cercaAlbumPerId(it) }
        }
    }

    fun aggiornaRicercaGlobale(testo: String) {
        testoRicercaGlobale.value = testo

        ricercaGlobaleJob?.cancel()

        ricercaGlobaleJob = viewModelScope.launch {
            delay(300)
            lazyListStateRicerca.scrollToItem(0)

            if(!testo.isBlank()) {
                val parole = testo.trim().split(Regex("\\s+"))

                albumRicerca.value = repository.ricercaAlbum(parole, testo.trim())
                canzoniRicerca.value = repository.ricercaCanzoni(parole, testo.trim())
            } else {
                albumRicerca.value = emptyList()
                canzoniRicerca.value = emptyList()
            }
        }
    }

    fun azzeraPosizioneRicerca() {
        viewModelScope.launch {
            lazyListStateRicerca.scrollToItem(0)
        }
    }

    fun aggiornaRicercaArtista(testo: String) {
        ricercaArtista.value = testo

        ricercaArtistaJob?.cancel()

        ricercaArtistaJob = viewModelScope.launch {
            delay(300)

            if(!testo.isBlank()) {
                artistiTrovati.value = repository.ricercaArtista(testo)
            } else {
                artistiTrovati.value = emptyList()
            }
        }
    }

    fun osservaAlbum() {
        viewModelScope.launch{
            snapshotFlow { ordinaAlbumPerNome.value }
                .distinctUntilChanged()
                .flatMapLatest { perNome ->
                    if(perNome) {
                        repository.osservaAlbumPerNome()
                    } else {
                        repository.osservaAlbumPerData()
                    }
                }
                .collect { albums ->
                    album.value = albums
                    caricaArtistiAlbum(albums)
                }
        }
    }

    fun osservaArtisti() {
        viewModelScope.launch {
            repository.osservaArtisti().collect { listaArtisti ->
                artisti.value = listaArtisti
            }
        }
    }

    fun osservaCanzoni() {
        viewModelScope.launch {
            repository.osservaCanzoni().collect { listaCanzoni ->
                canzoni.value = listaCanzoni
                caricaArtistiCanzoni(listaCanzoni)
            }
        }
    }

    fun caricaGeneri() {
        viewModelScope.launch {
            generi.value = repository.leggiTuttiGeneri()
        }
    }

    fun caricaAscolti() {
        viewModelScope.launch{
            val ascoltiRecenti = repository.leggiAscoltiRecenti()
            caricaAscoltiMap(ascoltiRecenti)
        }
    }

    suspend fun caricaAscoltiMap(ascoltiRecenti: List<Ascolto>) {
        ascoltiRecentiMap.clear()

        ascoltiRecenti.forEach{ ascolto ->
            val giorno = formattaTimestamp(ascolto.timestamp)
            val canzone = repository.cercaCanzonePerId(ascolto.canzoneId)

            if(ascoltiRecentiMap.containsKey(giorno) && canzone != null)
            {
                ascoltiRecentiMap[giorno]?.add(canzone)
            } else if(canzone != null) {
                ascoltiRecentiMap[giorno] = mutableListOf(canzone)
            }
        }
    }

    fun cliccaArtista(artista: Artista) {
        if(artistiSelezionati.value.contains(artista)) {
            artistiSelezionati.value = artistiSelezionati.value - artista
        } else {
            artistiSelezionati.value = artistiSelezionati.value + artista
        }
        ricercaArtista.value = ""
        artistiTrovati.value = emptyList()
    }

    fun aggiungiArtista(nome: String) {
        viewModelScope.launch {
            if(!nome.isBlank()) {
                val id = repository.inserisciArtista(nome.trim())
                val artista = repository.cercaArtistaPerId(id)

                if(artista != null) {
                    cliccaArtista(artista)
                }
            }

            ricercaArtista.value = ""
        }
    }

    fun cliccaGenere(genere: Genere) {
        if(generiSelezionati.value.contains(genere)) {
            generiSelezionati.value = generiSelezionati.value - genere
        } else {
            generiSelezionati.value = generiSelezionati.value + genere
        }
    }

    fun aggiungiGenere(nome: String) {
        viewModelScope.launch {
            if(!nome.isBlank()) {
                repository.inserisciGenere(nome.trim())
            }

            caricaGeneri()
            nuovoGenere.value = ""
        }
    }

    fun apriAlbum(album: Album) {
        caricaCanzoniAlbum(album)
        apriSchermata(SchermataSecondaria.ALBUM)
    }

    fun caricaCanzoniAlbum(album: Album) {
        viewModelScope.launch{
            canzoniAlbum.value = emptyList()

            albumCliccato.value = album
            artistiAlbumCliccato.value = repository.cercaArtistiPerAlbumId(album.id)
            canzoniAlbum.value = repository.trovaCanzoniAlbum(album.id)
        }
    }

    suspend fun trovaArtistiCanzone(canzoneId: Long): List<Artista> {
        return repository.cercaArtistiPerCanzoneId(canzoneId)
    }
    
    suspend fun trovaArtistiAlbum(albumId: Long): List<Artista> {
        return repository.cercaArtistiPerAlbumId(albumId)
    }

    suspend fun caricaArtistiCanzoni(canzoni: List<Canzone>) {
        val artistiCanzoniMap = mutableMapOf<Canzone, List<Artista>>()

        canzoni.forEach{ canzone ->
            val artistiCanzone = trovaArtistiCanzone(canzone.id)
            artistiCanzoniMap[canzone] = artistiCanzone
        }

        artistiCanzoni.value = artistiCanzoniMap
    }

    suspend fun caricaArtistiAlbum(albums: List<Album>) {
        val artistiAlbumsMap = mutableMapOf<Long, List<Artista>>()

        albums.forEach{ album ->
            val artistiAlbum = trovaArtistiAlbum(album.id)
            artistiAlbumsMap[album.id] = artistiAlbum
        }

        artistiAlbums.value = artistiAlbumsMap
    }

    fun riproduci(canzone: Canzone) {
        viewModelScope.launch {
            val coda: List<Canzone>

            if(stackSchermate.lastOrNull() == SchermataSecondaria.CODA) {
                coda = canzoniInCoda.value
            } else if(stackSchermate.lastOrNull() == SchermataSecondaria.ALBUM && albumCliccato.value != null) {
                coda = canzoniAlbum.value
            } else {
                coda = listOf(canzone)
            }

            val indice = coda.indexOf(canzone)
            if(indice == -1) return@launch

            canzoniInCoda.value = coda

            aggiornaDatiCanzone(canzone)

            playerController?.impostaCoda(artistiCanzoni.value, coda, album.value, indice)
        }
    }

    fun spostaCanzone(indiceVecchio: Int, indiceNuovo: Int) {
        val indiceCorrente = playerController?.indiceCanzoneCorrente() ?: return

        val indiceVecchioReale = indiceCorrente + indiceVecchio
        val indiceNuovoReale = indiceCorrente + indiceNuovo

        playerController?.spostaCanzone(indiceVecchioReale, indiceNuovoReale)
        
        val nuovaCoda = canzoniInCoda.value.toMutableList()

        val spostata = nuovaCoda.removeAt(indiceVecchioReale)
        nuovaCoda.add(indiceNuovoReale, spostata)

        canzoniInCoda.value = nuovaCoda
    }

    fun cancellaCanzoneDaCoda(indice: Int) {
        val indiceCorrente = playerController?.indiceCanzoneCorrente() ?: return

        val indiceReale = indiceCorrente + indice

        playerController?.cancellaCanzoneDaCoda(indiceReale)

        val nuovaCoda = canzoniInCoda.value.toMutableList()

        nuovaCoda.removeAt(indiceReale)

        canzoniInCoda.value = nuovaCoda
    }
 
    fun playPausa() {
        if(riproducendo.value) {
            playerController?.pausa()
        }
        else {
            playerController?.riprendi()
        }
    }

    fun avanti() {
        playerController?.avanti()
    }

    fun indietro() {
        playerController?.indietro()
    }

    fun aggiornaPosizione() {
        if(!trascinandoSlider.value) {
            posizioneAttuale.value = playerController?.posizioneAttuale() ?: 0L
        }
    }

    fun regolaPosizione(posizione: Long) {
        posizioneAttuale.value = posizione
        playerController?.regolaPosizione(posizione)
    }

    fun impostaMetadatiFile(metadati: MetadatiCanzone) {
        listaMetadati.value = listOf(metadati)

        ricercaArtista.value = metadati.artista
        albumProposto.value = metadati.album ?: metadati.titolo
        titoloCanzoneImportata.value = metadati.titolo
        tipoAlbum.value = TipoAlbum.SINGOLO
        aggiornaRicercaArtista(ricercaArtista.value)
    }

    fun impostaMetadatiCartella(metadati: List<MetadatiCanzone>, uriCartella: Uri) {

        val cartella = DocumentFile.fromTreeUri(
            getApplication(),
            uriCartella
        )

        copertinaImportata.value = cartella?.let { GestoreCopertine.trovaCopertina(it) }

        listaMetadati.value = metadati

        val albumPiuFrequente = metadati
            .mapNotNull { it.album }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

        val artistaPiuFrequente = metadati
            .mapNotNull { it.artista }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
        
        albumProposto.value = albumPiuFrequente ?: ""
        ricercaArtista.value = artistaPiuFrequente ?: ""
        titoloCanzoneImportata.value = ""
        tipoAlbum.value = TipoAlbum.ALBUM
        aggiornaRicercaArtista(ricercaArtista.value)
    }

    fun confermaImportazione() {
        viewModelScope.launch {
            erroreImportazione.value = null

            albumProposto.value = albumProposto.value.trim()

            val anno = annoUscita.value.toIntOrNull()

            if(albumProposto.value.isBlank()) {
                erroreImportazione.value = "Inserisci un titolo per l'album/singolo"
                return@launch
            }

            if(artistiSelezionati.value.isEmpty()) {
                erroreImportazione.value = "Inserisci almeno un artista"
                return@launch
            }
            val artistiId = artistiSelezionati.value.map { it.id }

            if(anno == null) {
                erroreImportazione.value = "Inserisci l'anno di uscita"
                return@launch
            }

            if(repository.controllaEsistenzaAlbum(
                albumProposto.value, 
                artistiId, 
                tipoAlbum.value,
                null
            )) {
                erroreImportazione.value = "Questo album/singolo è già presente nella libreria"
                return@launch
            }

            val albumId = repository.importaAlbum(
                albumProposto.value, 
                artistiId, 
                generiSelezionati.value, 
                copertinaImportata.value?.toString(),
                anno,
                tipoAlbum.value
            )

            listaMetadati.value.forEach { metadati ->
                repository.importaCanzone(
                    metadati, 
                    albumId,
                    artistiId
                )
            }

            albumCliccato.value = null
            artistiAlbumCliccato.value = emptyList()

            annullaCanzoneRiprodotta()
            annullaImportazione()
        }
    }

    fun annullaImportazione() {
        albumProposto.value = ""
        ricercaArtista.value = ""
        artistiSelezionati.value = emptyList()
        nuovoGenere.value = ""
        generiSelezionati.value = emptyList()
        annoUscita.value = ""
        listaMetadati.value = emptyList()
        importazioneInCorso.value = false
        copertinaImportata.value = null
        titoloCanzoneImportata.value = ""
        erroreImportazione.value = null
    }

    fun annullaCanzoneRiprodotta() {
        playerController?.pausa()

        canzoneSelezionata.value = null
        albumSelezionato.value = null
        artistiCanzoneAttuale.value = emptyList()
    }

    fun formattaTimestamp(timestamp: Long): String {
        return Instant
            .ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}