package com.example.musicplayer.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog

import android.net.Uri

import coil.compose.AsyncImage

import com.example.musicplayer.model.Album
import com.example.musicplayer.model.Artista

@Composable fun ArtistaScreen(
    viewModel: MusicPlayerViewModel,
    artista: Artista?,
    albumPiuRecente: Album?,
    albumArtista: List<Album>,
    onIndietroSchermata: () -> Unit,
    onModificaArtista: () -> Unit,
    onAnnullaModifica: () -> Unit,
    onEliminaArtista: () -> Unit,
    onAlbumClick: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostraConfermaEliminazione by remember { mutableStateOf(false) }

    if(mostraConfermaEliminazione) {
        AlertDialog(
            onDismissRequest = {
                mostraConfermaEliminazione = false
            },
            title = {
                Text("Eliminare l'artista?")
            },
            text = {
                Text("Non puoi eliminare un artista se ha album associati")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostraConfermaEliminazione = false
                        onEliminaArtista()
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
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

        if(albumPiuRecente?.percorsoCopertina != null) {
            AsyncImage(
                model = Uri.parse(albumPiuRecente.percorsoCopertina),
                contentDescription = "Immagine artista",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
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
                if(viewModel.artistaInModifica.value) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Nome")

                        TextField(
                            value = viewModel.nomeModificato.value,
                            onValueChange = { viewModel.nomeModificato.value = it },
                            label = { Text("Nome") },
                            singleLine = true,
                            modifier = Modifier.width(200.dp)
                        )

                        if(viewModel.erroreModificaArtista.value != null) {
                            Text(
                                text = viewModel.erroreModificaArtista.value!!,
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
                                onClick = onModificaArtista
                            ) {
                                Text("Conferma")
                            }
                        }
                    }
                } else {
                    Text(
                        text = artista?.nome ?: "Artista Sconosciuto",
                        fontSize = 18.sp,
                        modifier = Modifier.widthIn(max = 250.dp)
                    )
                    
                    IconButton(
                        onClick = onModificaArtista,
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

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = modifier) {
            items(albumArtista.size) { indice ->
                val album = albumArtista[indice]

                Spacer(modifier = Modifier.height(5.dp))

                if(indice == 0 || album.anno != albumArtista[indice - 1].anno) {
                    Text(
                        text = album.anno.toString(),
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onAlbumClick(album) }
                        .fillMaxWidth()
                ) {
                    if(album.percorsoCopertina != null) {
                        AsyncImage(
                            model = Uri.parse(album.percorsoCopertina),
                            contentDescription = "copertina album",
                            modifier = Modifier.size(100.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color.Gray)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = album.titolo,
                        fontSize = 16.sp
                    )
                }
            }

        }

        MiniPlayerArea(viewModel)
        MenuNavigazione(viewModel)
    }
}