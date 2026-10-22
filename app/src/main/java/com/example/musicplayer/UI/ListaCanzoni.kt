package com.example.musicplayer.UI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment

import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.Artista
import com.example.musicplayer.model.Album
import com.example.musicplayer.model.TipoAlbum

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListaCanzoni(
    canzoni: List<Canzone>,
    artistiCanzoni: Map<Canzone, List<Artista>>,
    canzoneSelezionata: Canzone?,
    onCanzoneClick: (Canzone) -> Unit,
    onCanzonePremuta: (Canzone) -> Unit,
    album: Album?, 
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(canzoni) { canzone ->

            val artistiCanzone = artistiCanzoni[canzone] ?: emptyList()

            val colore = if(canzone == canzoneSelezionata) Color(0xFF7B61FF) else Color.Black

            val testo = if(album != null && album.tipoAlbum == TipoAlbum.ALBUM) {
                "${canzone.numeroTraccia} - ${canzone.titolo}"
            } else {
                "${canzone.titolo}"
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(70.dp)
            ) {
                Column(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = { 
                                onCanzoneClick(canzone) 
                            },
                            onLongClick = {
                                onCanzonePremuta(canzone)
                            }
                        )
                        .padding(4.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = testo,
                        color = colore,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp
                    )

                    Text(
                        text = artistiCanzone.joinToString(", ") { it.nome } +
                         " - ${canzone.metadatiArtista}" +
                         " - ${formattaDurata(canzone.durata)}",
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