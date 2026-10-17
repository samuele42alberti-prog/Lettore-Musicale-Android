package com.example.musicplayer.UI

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.activity.compose.BackHandler

@Composable
fun HomeScreen(
    viewModel: MusicPlayerViewModel,
    onImportaCanzone: () -> Unit,
    onImportaAlbum: () -> Unit
) {
    BackHandler(
        enabled = viewModel.stackSchermate.isNotEmpty() ||
            viewModel.schermataPrincipale.value == SchermataPrincipale.CRONOLOGIA
    ) {
        if(viewModel.stackSchermate.isNotEmpty()) {
            val artista = viewModel.stackSchermate.lastOrNull() == SchermataSecondaria.ARTISTA
            val album = viewModel.stackSchermate.lastOrNull() == SchermataSecondaria.ALBUM

            viewModel.tornaIndietroSchermata()

            if(artista) {
                viewModel.artistaCliccato.value = null
                viewModel.albumPiuRecente.value = null
                viewModel.albumArtista.value = emptyList()
            } else if(album) {
                viewModel.albumCliccato.value = null
                viewModel.artistiAlbumCliccato.value = emptyList()
                viewModel.annullaModifica()
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
                onIndietroSchermata = { 
                    viewModel.tornaIndietroSchermata();
                    viewModel.tornaIndietroSchermata()
                },
                onApriPlayer = { viewModel.tornaIndietroSchermata() },
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
                onIndietroSchermata = { viewModel.tornaIndietroSchermata() },
                onApriCoda = { viewModel.apriSchermata(SchermataSecondaria.CODA) },
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
                    viewModel.tornaIndietroSchermata();
                    viewModel.artistaCliccato.value = null;
                    viewModel.albumPiuRecente.value = null;
                    viewModel.albumArtista.value = emptyList()
                },
                onModificaArtista = {},
                onAnnullaModifica = {},
                onEliminaArtista = {},
                onAlbumClick = { viewModel.apriAlbum(it) },
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
                    viewModel.tornaIndietroSchermata();
                    viewModel.albumCliccato.value = null;
                    viewModel.artistiAlbumCliccato.value = emptyList();
                    viewModel.annullaModifica()
                },
                onModificaAlbum = { viewModel.onModificaAlbum() },
                onAnnullaModifica = { viewModel.annullaModifica() },
                onEliminaAlbum = { viewModel.eliminaAlbum() },
                onApriArtista = { viewModel.apriArtista(it) }
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
                    onAlbumClick = { viewModel.apriAlbum(it) },
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
                    onAlbumClick = { viewModel.apriAlbum(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            SchermataPrincipale.CRONOLOGIA -> {

                ListaCronologia(
                    ascoltiRecenti = viewModel.ascoltiRecentiMap,
                    artistiCanzoni = viewModel.artistiCanzoni.value,
                    albums = viewModel.album.value,
                    onCanzoneClick = { viewModel.riproduci(it) },
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
        }

        MiniPlayerArea(viewModel)
        MenuNavigazione(viewModel)
    }
}