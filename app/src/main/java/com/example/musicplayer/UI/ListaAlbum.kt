package com.example.musicplayer.UI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextOverflow

import android.net.Uri

import coil.compose.AsyncImage

import com.example.musicplayer.model.Album
import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.Artista
import com.example.musicplayer.model.TipoAlbum

@Composable
fun ListaAlbum(
    albums: List<Album>,
    artistiAlbums: Map<Long, List<Artista>>,
    canzoneSelezionata: Canzone?,
    onAlbumClick: (Album) -> Unit,
    posizioneListaAlbum: Int,
    offsetListaAlbum: Int,
    onPosizioneListaAlbumChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = posizioneListaAlbum,
        initialFirstVisibleItemScrollOffset = offsetListaAlbum
    )

    LaunchedEffect(lazyListState) {
        snapshotFlow{
            Pair(
                lazyListState.firstVisibleItemIndex,
                lazyListState.firstVisibleItemScrollOffset
            )
        }.collectLatest { (indice, offset) ->
            onPosizioneListaAlbumChange(indice, offset)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        items(albums) { album ->
            val artistiAlbum = artistiAlbums[album.id] ?: emptyList()

            val colore = if(canzoneSelezionata?.albumId == album.id) Color(0xFF7B61FF) else Color.Black

            val sottotitolo = if(album.tipoAlbum == TipoAlbum.ALBUM) {
                artistiAlbum.joinToString(", ") { it.nome } + " • Album"
            } else {
                artistiAlbum.joinToString(", ") { it.nome } + " • Singolo"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAlbumClick(album) }
                    .padding(12.dp)
                    .height(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(album.percorsoCopertina != null) {
                    AsyncImage(
                        model = Uri.parse(album.percorsoCopertina),
                        contentDescription = "copertina album",
                        modifier = Modifier.size(80.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = album.titolo,
                        color = colore,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = sottotitolo,
                        color = colore,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}