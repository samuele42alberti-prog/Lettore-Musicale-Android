package com.example.musicplayer.UI

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable

import android.net.Uri

import coil.compose.AsyncImage

import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.Album
import com.example.musicplayer.model.TipoAlbum

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RicercaScreen(
    viewModel: MusicPlayerViewModel,
    onCanzoneClick: (Canzone) -> Unit,
    onCanzonePremuta: (Canzone) -> Unit,
    onAlbumClick: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = viewModel.lazyListStateRicerca

    Column(
        modifier = modifier
    ) {

        TextField(
            value = viewModel.testoRicercaGlobale.value,
            onValueChange = {
                viewModel.aggiornaRicercaGlobale(it)
            },
            placeholder = {
                Text("Cerca...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "ricerca"
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.weight(1f)
        ) {
            val albums = viewModel.album.value
            val albumsMap = albums.associateBy { it.id }

            val albumFiltrati = viewModel.albumRicerca.value.filter { it.tipoAlbum == TipoAlbum.ALBUM }
            val singoliFiltrati = viewModel.albumRicerca.value.filter { it.tipoAlbum == TipoAlbum.SINGOLO }

            if(albumFiltrati.isNotEmpty()) {

                item {
                    Text(
                        text = "ALBUM",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                items(albumFiltrati) { album ->

                    val artistiAlbum = viewModel.artistiAlbums.value[album.id] ?: emptyList()

                    val colore = if(viewModel.canzoneSelezionata.value?.albumId == album.id) Color(0xFF7B61FF) else Color.Black

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .height(50.dp)
                            .clickable { onAlbumClick(album) }
                    ) {
                        if(album.percorsoCopertina != null) {
                            AsyncImage(
                                model = Uri.parse(album.percorsoCopertina),
                                contentDescription = "copertina album",
                                modifier = Modifier.size(50.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.Gray)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = album.titolo,
                                color = colore,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = artistiAlbum.joinToString(", ") { it.nome },
                                color = colore,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            if(singoliFiltrati.isNotEmpty()) {

                item {
                    Text(
                        text = "SINGOLI",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                items(singoliFiltrati) { album ->

                    val artistiAlbum = viewModel.artistiAlbums.value[album.id] ?: emptyList()

                    val colore = if(viewModel.canzoneSelezionata.value?.albumId == album.id) Color(0xFF7B61FF) else Color.Black

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .height(50.dp)
                            .clickable { onAlbumClick(album) }
                    ) {
                        if(album.percorsoCopertina != null) {
                            AsyncImage(
                                model = Uri.parse(album.percorsoCopertina),
                                contentDescription = "copertina album",
                                modifier = Modifier.size(50.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.Gray)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = album.titolo,
                                color = colore,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = artistiAlbum.joinToString(", ") { it.nome },
                                color = colore,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            if(viewModel.canzoniRicerca.value.isNotEmpty()) {

                item {
                    Text(
                        text = "CANZONI",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                items(viewModel.canzoniRicerca.value) { canzone ->

                    val artistiCanzone = viewModel.artistiCanzoni.value[canzone] ?: emptyList()

                    val album = albumsMap[canzone.albumId]

                    val colore = if(canzone == viewModel.canzoneSelezionata.value) Color(0xFF7B61FF) else Color.Black

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .height(50.dp)
                            .combinedClickable(
                                onClick = { 
                                    onCanzoneClick(canzone) 
                                },
                                onLongClick = {
                                    onCanzonePremuta(canzone)
                                }
                            )
                    ) {
                        if(album?.percorsoCopertina != null) {
                            AsyncImage(
                                model = Uri.parse(album.percorsoCopertina),
                                contentDescription = "copertina album",
                                modifier = Modifier.size(50.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.Gray)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = canzone.titolo,
                                color = colore,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "${artistiCanzone.joinToString(", ") { it.nome }} - ${canzone.metadatiArtista} - ${formattaDurata(canzone.durata)}",
                                color = colore,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}