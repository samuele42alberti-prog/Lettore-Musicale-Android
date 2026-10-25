package com.example.musicplayer.UI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.rememberCoroutineScope

import kotlinx.coroutines.launch

import androidx.activity.compose.BackHandler

@Composable
fun HomeScreen(
    viewModel: MusicPlayerViewModel,
    onImportaCanzone: () -> Unit,
    onImportaAlbum: () -> Unit
) {
    var mostraOperazioneRiuscita by remember { mutableStateOf(false) }
    var mostraOperazioneNonRiuscita by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    if(mostraOperazioneRiuscita) {
        AlertDialog(
            onDismissRequest = {
                mostraOperazioneRiuscita = false
            },
            title = {
                Text("Operazione riuscita!")
            },
            confirmButton = {
                TextButton(
                    onClick = { mostraOperazioneRiuscita = false }
                ) {
                    Text("Ok")
                }
            }
        )
    }

    if(mostraOperazioneNonRiuscita) {
        AlertDialog(
            onDismissRequest = {
                mostraOperazioneNonRiuscita = false
            },
            title = {
                Text("Operazione non riuscita")
            },
            confirmButton = {
                TextButton(
                    onClick = { mostraOperazioneNonRiuscita = false }
                ) {
                    Text("Ok")
                }
            }
        )
    }

    if(viewModel.mostraAzioniCanzone.value) {
        AlertDialog(
            onDismissRequest = {
                viewModel.pulisciCanzonePremuta()
            },
            title = {
                Text(
                    text = viewModel.canzonePremuta.value?.titolo ?: "Titolo sconosciuto"
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val canzonePremuta = viewModel.canzonePremuta?.value
                                if(canzonePremuta != null) {
                                    viewModel.aggiungiACoda(canzonePremuta)
                                }
                                viewModel.pulisciCanzonePremuta()
                            }
                            .padding(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = "Aggiungi alla coda",
                            modifier = Modifier.size(25.dp)
                        )

                        Text(
                            text = "Aggiungi alla coda",
                            fontSize = 20.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                scope.launch {
                                    val albumId = viewModel.canzonePremuta?.value?.albumId

                                    if(albumId != null) {
                                        val album = viewModel.cercaAlbumPerId(albumId)

                                        if(album != null) {
                                            viewModel.apriAlbum(album)
                                        }
                                    }

                                    if(viewModel.stackSchermate.contains(SchermataSecondaria.CODA)) {
                                        viewModel.stackSchermate.remove(SchermataSecondaria.PLAYER)
                                        viewModel.stackSchermate.remove(SchermataSecondaria.CODA)
                                    }

                                    viewModel.pulisciCanzonePremuta()
                                }
                            }
                            .padding(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = "Vai all'album",
                            modifier = Modifier.size(25.dp)
                        )

                        Text(
                            text = "Vai all'album",
                            fontSize = 20.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                scope.launch {
                                    val artistiCanzone = viewModel.artistiCanzoni.value[viewModel.canzonePremuta.value]
                                    viewModel.apriArtista(artistiCanzone!![0])

                                    if(viewModel.stackSchermate.contains(SchermataSecondaria.CODA)) {
                                        viewModel.stackSchermate.remove(SchermataSecondaria.PLAYER)
                                        viewModel.stackSchermate.remove(SchermataSecondaria.CODA)
                                    }

                                    viewModel.pulisciCanzonePremuta()
                                }
                            }
                            .padding(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Vai all'artista",
                            modifier = Modifier.size(25.dp)
                        )

                        Text(
                            text = "Vai all'artista",
                            fontSize = 20.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                viewModel.pulisciCanzonePremuta()
                            }
                            .padding(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = "Aggiungi a una playlist",
                            modifier = Modifier.size(25.dp)
                        )

                        Text(
                            text = "Aggiungi a una playlist",
                            fontSize = 20.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.pulisciCanzonePremuta() }
                ) {
                    Text("Chiudi")
                }
            }
        )
    }

    BackHandler(
        enabled = viewModel.stackSchermate.isNotEmpty() ||
            viewModel.schermataPrincipale.value == SchermataPrincipale.CRONOLOGIA ||
            viewModel.schermataPrincipale.value == SchermataPrincipale.LISTA_ARTISTI
    ) {
        if(viewModel.stackSchermate.isNotEmpty()) {
            scope.launch {
                viewModel.tornaIndietroSchermata()
            }
        } else {
            viewModel.cambiaSchermata(SchermataPrincipale.LIBRERIA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        if(viewModel.stackSchermate.lastOrNull() == SchermataSecondaria.CODA) {
            ListaCoda(
                canzoni = viewModel.canzoniInCoda.value,
                albums = viewModel.album.value,
                artistiCanzoni = viewModel.artistiCanzoni.value,
                canzoneSelezionata = viewModel.canzoneSelezionata.value,
                onCanzoneClick = { viewModel.riproduci(it) },
                onMaggioriInformazioni = { viewModel.canzonePremuta(it) },
                onIndietroSchermata = { 
                    scope.launch {
                        viewModel.tornaIndietroSchermata()
                        viewModel.tornaIndietroSchermata()
                    };
                },
                onApriPlayer = { 
                    scope.launch {
                        viewModel.tornaIndietroSchermata()
                    }
                 },
                onCodaModificata = { vecchioIndice, nuovoIndice ->
                    viewModel.spostaCanzone(vecchioIndice, nuovoIndice)
                },
                onCancellaDaCoda = { viewModel.cancellaCanzoneDaCoda(it) },
                modifier = Modifier.weight(1f)
            )
            return
        }

        if(viewModel.stackSchermate.lastOrNull() == SchermataSecondaria.PLAYER) {
            PlayerCompleto(
                canzone = viewModel.canzoneSelezionata.value!!,
                artisti = viewModel.artistiCanzoneAttuale.value!!,
                album = viewModel.albumSelezionato.value,
                riproducendo = viewModel.riproducendo.value,
                onIndietro = { viewModel.indietro() },
                onPlayPausa = { viewModel.playPausa() },
                onAvanti = { viewModel.avanti() },
                onIndietroSchermata = { 
                    scope.launch {
                        viewModel.tornaIndietroSchermata()
                    }
                 },
                onApriCoda = { viewModel.apriSchermata(SchermataSecondaria.CODA) },
                onApriArtista = { 
                    scope.launch {
                        viewModel.apriArtista(viewModel.artistiCanzoneAttuale.value[0])
                        viewModel.stackSchermate.remove(SchermataSecondaria.PLAYER)
                    } 
                },
                onApriAlbum = { 
                    scope.launch {
                        viewModel.apriAlbum(viewModel.albumSelezionato.value!!)
                        viewModel.stackSchermate.remove(SchermataSecondaria.PLAYER)
                    } 
                },
                viewModel = viewModel
            )
            return
        }

        if(viewModel.stackSchermate.lastOrNull() == SchermataSecondaria.ARTISTA) {
            ArtistaScreen(
                viewModel = viewModel,
                artista = viewModel.artistaCliccato.value,
                albumPiuRecente = viewModel.albumPiuRecente.value,
                albumArtista = viewModel.albumArtista.value,
                onIndietroSchermata = { 
                    scope.launch {
                        viewModel.tornaIndietroSchermata()
                    }
                },
                onModificaArtista = { viewModel.onModificaArtista() },
                onAnnullaModifica = { viewModel.annullaModificaArtista() },
                onEliminaArtista = { 
                    if(viewModel.eliminaArtista()) {
                        mostraOperazioneRiuscita = true
                    } else {
                        mostraOperazioneNonRiuscita = true
                    }
                },
                onAlbumClick = { 
                    scope.launch {
                        viewModel.apriAlbum(it)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            return
        }

        if(viewModel.stackSchermate.lastOrNull() == SchermataSecondaria.ALBUM) {
            CanzoniScreen(
                viewModel = viewModel,
                album = viewModel.albumCliccato.value,
                onIndietroSchermata = { 
                    scope.launch {
                        viewModel.tornaIndietroSchermata()
                    }
                },
                onModificaAlbum = { viewModel.onModificaAlbum() },
                onAnnullaModifica = { viewModel.annullaModificaAlbum() },
                onEliminaAlbum = { 
                    val album = viewModel.albumCliccato.value

                    if(album != null) {
                        scope.launch {
                            if(viewModel.eliminaAlbum(album)) {
                                mostraOperazioneRiuscita = true
                            } else {
                                mostraOperazioneNonRiuscita = true
                            }
                        }
                    } else {
                        mostraOperazioneNonRiuscita = true
                    }
                },
                onApriArtista = { 
                    scope.launch {
                        viewModel.apriArtista(it) 
                    }
                }
            )
            return
        }

        when (viewModel.schermataPrincipale.value) {
            SchermataPrincipale.LIBRERIA -> {

                Text(
                    text = "Libreria",
                    fontSize = 18.sp
                )

                IconButton(
                    onClick = { viewModel.cambiaSchermata(SchermataPrincipale.CRONOLOGIA) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "cronologia ascolti",
                        modifier = Modifier.size(30.dp)
                    )
                }

                IconButton(
                    onClick = { 
                        viewModel.cambiaSchermata(SchermataPrincipale.LISTA_ARTISTI);
                        viewModel.azzeraPosizioneListaArtisti()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "lista artisti",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconButton(
                        onClick = { 
                            viewModel.ordinaAlbumPerNome.value = true;
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SortByAlpha,
                            contentDescription = "Ordine alfabetico",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    IconButton(
                        onClick = { 
                            viewModel.ordinaAlbumPerNome.value = false;
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Ordine aggiunta",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                ListaAlbum(
                    albums = viewModel.album.value,
                    artistiAlbums = viewModel.artistiAlbums.value,
                    canzoneSelezionata = viewModel.canzoneSelezionata.value,
                    onAlbumClick = { 
                        scope.launch {
                            viewModel.apriAlbum(it) 
                        }
                    },
                    posizioneListaAlbum = viewModel.posizioneListaAlbum.value,
                    offsetListaAlbum = viewModel.offsetListaAlbum.value,
                    onPosizioneListaAlbumChange = { indice, offset ->
                        viewModel.posizioneListaAlbum.value = indice
                        viewModel.offsetListaAlbum.value = offset
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            SchermataPrincipale.RICERCA -> {
                RicercaScreen(
                    viewModel = viewModel,
                    onCanzoneClick = { viewModel.riproduci(it) },
                    onAlbumClick = { 
                        scope.launch{
                            viewModel.apriAlbum(it) 
                        }
                    },
                    onCanzonePremuta = {
                        viewModel.canzonePremuta(it)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            SchermataPrincipale.CRONOLOGIA -> {

                ListaCronologia(
                    ascoltiRecenti = viewModel.ascoltiRecentiMap,
                    artistiCanzoni = viewModel.artistiCanzoni.value,
                    albums = viewModel.album.value,
                    onCanzoneClick = { viewModel.riproduci(it) },
                    onCanzonePremuta = { viewModel.canzonePremuta(it) },
                    onIndietroSchermata = { 
                        viewModel.cambiaSchermata(SchermataPrincipale.LIBRERIA)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            SchermataPrincipale.IMPORTAZIONE -> {

                Column(
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = onImportaCanzone
                    ) {
                        Text("Importa Singolo")
                    }

                    Button(
                        onClick = onImportaAlbum
                    ) {
                        Text("Importa Album")
                    }

                    if(viewModel.importazioneInCorso.value) {
                        FormImportazione(
                            titolo = viewModel.titoloCanzoneImportata.value,
                            album = viewModel.albumProposto.value,
                            artisti = viewModel.artistiTrovati.value,
                            artistiSelezionati = viewModel.artistiSelezionati.value,
                            ricercaArtista = viewModel.ricercaArtista.value,
                            onArtistaClick = { viewModel.cliccaArtista(it) },
                            onRicercaArtistaChange = { 
                                viewModel.aggiornaRicercaArtista(it)
                            },
                            onNuovoArtista = { viewModel.aggiungiArtista(it) },
                            nuovoGenere = viewModel.nuovoGenere.value,
                            generi = viewModel.generi.value,
                            generiSelezionati = viewModel.generiSelezionati.value,
                            copertina = viewModel.copertinaImportata.value?.toString(),
                            annoUscita = viewModel.annoUscita.value,
                            onGenereClick = { viewModel.cliccaGenere(it) },
                            onNuovoGenere = { viewModel.aggiungiGenere(it) },
                            onAlbumChange = { viewModel.albumProposto.value = it },
                            onGenereChange = { viewModel.nuovoGenere.value = it },
                            onAnnoChange = { viewModel.annoUscita.value = it },
                            onConferma = { viewModel.confermaImportazione() },
                            onAnnulla = { viewModel.annullaImportazione() },
                            errore = viewModel.erroreImportazione.value
                        )
                    }
                }
            }

            SchermataPrincipale.LISTA_ARTISTI -> {
                ListaArtisti(
                    viewModel = viewModel,
                    artisti = viewModel.artisti.value,
                    onArtistaClick = { 
                        scope.launch {
                            viewModel.apriArtista(it) 
                        }
                    },
                    onIndietroSchermata = { 
                        viewModel.cambiaSchermata(SchermataPrincipale.LIBRERIA)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        MiniPlayerArea(viewModel)
        MenuNavigazione(viewModel)
    }
}