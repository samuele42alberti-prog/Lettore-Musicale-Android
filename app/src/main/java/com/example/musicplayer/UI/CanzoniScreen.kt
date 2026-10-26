package com.example.musicplayer.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog

import android.net.Uri

import coil.compose.AsyncImage

import com.example.musicplayer.model.Album
import com.example.musicplayer.model.Artista

@Composable
fun CanzoniScreen(
    viewModel: MusicPlayerViewModel,
    album: Album?,
    onIndietroSchermata: () -> Unit,
    onModificaAlbum: () -> Unit,
    onAnnullaModifica: () -> Unit,
    onEliminaAlbum: () -> Unit,
    onApriArtista: (Artista) -> Unit
) {

    val durataAlbum = viewModel.canzoniAlbum.value.sumOf { it.durata }

    var mostraConfermaEliminazione by remember { mutableStateOf(false) }

    var sceltaArtista by remember { mutableStateOf(false) }

    if(mostraConfermaEliminazione) {
        AlertDialog(
            onDismissRequest = {
                mostraConfermaEliminazione = false
            },
            title = {
                Text("Eliminare l'album?")
            },
            text = {
                Text("Verranno eliminati definitivamente l'album e le canzoni associate")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostraConfermaEliminazione = false
                        onEliminaAlbum()
                        onIndietroSchermata()
                    }
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostraConfermaEliminazione = false
                    }
                ) {
                    Text("Annulla")
                }
            }
        )
    }

    if(sceltaArtista) {
        AlertDialog(
            onDismissRequest = {
                sceltaArtista = false
            },
            title = {
                Text("Scegli l'artista da visualizzare")
            },
            text = {
                Column {
                    viewModel.artistiAlbumCliccato.value.forEach{ artista ->
                        TextButton(
                            onClick = { 
                                onApriArtista(artista)
                                sceltaArtista = false
                            }
                        ) {
                            Text(
                                text = artista.nome
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { sceltaArtista = false }
                ) {
                    Text("Annulla")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
       Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onIndietroSchermata,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Freccia indietro",
                    modifier = Modifier.size(25.dp)
                )
            }

            IconButton(
                onClick = {
                    mostraConfermaEliminazione = true
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Elimina",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            TextButton(
                onClick = { 
                    val artisti = viewModel.artistiAlbumCliccato.value

                    when(artisti.size) {
                        0 -> {}
                        1 -> (onApriArtista(artisti.first()))
                        else -> sceltaArtista = true 
                    }
                }
            ) {
                Text(
                    text = viewModel.artistiAlbumCliccato.value.joinToString(", ") { it.nome },
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            if(album?.percorsoCopertina != null) {
                AsyncImage(
                    model = Uri.parse(album.percorsoCopertina),
                    contentDescription = "copertina album",
                    modifier = Modifier.size(150.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(viewModel.albumInModifica.value) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Titolo")

                            TextField(
                                value = viewModel.titoloModificato.value,
                                onValueChange = { viewModel.titoloModificato.value = it },
                                label = { Text("Titolo") },
                                singleLine = true,
                                modifier = Modifier.width(200.dp)
                            )

                            Text("Anno di uscita")

                            TextField(
                                value = viewModel.annoModificato.value,
                                onValueChange = { 
                                    if(it.all { carattere -> carattere.isDigit() }) {
                                        viewModel.annoModificato.value = it
                                    } 
                                },
                                label = { Text("Anno di uscita") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                modifier = Modifier.width(200.dp)
                            )

                            SelettoreArtista(
                                artisti = viewModel.artistiTrovati.value,
                                artistiSelezionati = viewModel.artistiSelezionati.value,
                                ricercaArtista = viewModel.ricercaArtista.value,
                                onArtistaClick = { viewModel.cliccaArtista(it) },
                                onRicercaArtistaChange = { viewModel.aggiornaRicercaArtista(it) },
                                onNuovoArtista = { viewModel.aggiungiArtista(it) }
                            )

                            if(viewModel.erroreModificaAlbum.value != null) {
                                Text(
                                    text = viewModel.erroreModificaAlbum.value!!,
                                    color = Color.Red
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {

                                Button(
                                    onClick = onAnnullaModifica
                                ) {
                                    Text("Annulla")
                                }

                                Button(
                                    onClick = onModificaAlbum
                                ) {
                                    Text("Conferma")
                                }
                            }
                        }
                    } else {
                        Text(
                            text = album?.titolo ?: "Album Sconosciuto",
                            fontSize = 16.sp,
                            modifier = Modifier.widthIn(max = 250.dp)
                        )
                    
                        IconButton(
                            onClick = onModificaAlbum,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Modifica",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = "${formattaDurata(durataAlbum)} • ${album?.anno}",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            ListaCanzoni(
                canzoni = viewModel.canzoniAlbum.value,
                artistiCanzoni = viewModel.artistiCanzoni.value,
                canzoneSelezionata = viewModel.canzoneSelezionata.value,
                onCanzoneClick = { 
                    viewModel.riproduci(it)
                },
                onCanzonePremuta = { viewModel.canzonePremuta(it) },
                album = viewModel.albumCliccato.value,
                modifier = Modifier.weight(1f)
            )
        }
        MiniPlayerArea(viewModel)
        MenuNavigazione(viewModel)
    }
}