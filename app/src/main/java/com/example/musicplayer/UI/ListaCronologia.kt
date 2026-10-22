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
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import java.time.LocalDate
import java.util.Locale
import java.time.format.DateTimeFormatter

import android.net.Uri

import coil.compose.AsyncImage

import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.Artista
import com.example.musicplayer.model.Album

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListaCronologia(
    ascoltiRecenti: LinkedHashMap<String, MutableList<Canzone>>,
    artistiCanzoni: Map<Canzone, List<Artista>>,
    albums: List<Album>,
    onCanzoneClick: (Canzone) -> Unit,
    onCanzonePremuta: (Canzone) -> Unit,
    onIndietroSchermata: () -> Unit,
    modifier: Modifier = Modifier
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

    LazyColumn(modifier = modifier) {

        val albumsMap = albums.associateBy { it.id }

        ascoltiRecenti.entries.reversed().forEach{ (giorno, canzoni) ->

            item{
                Spacer(modifier = Modifier.height(5.dp))
                
                Text(
                    text = formattaGiorno(giorno),
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(5.dp))
            }

            items(canzoni.reversed()) { canzone ->

                val artistiCanzone = artistiCanzoni[canzone] ?: emptyList()

                val album = albumsMap[canzone.albumId]

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(70.dp)
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
                            model = Uri.parse(album?.percorsoCopertina),
                            contentDescription = "copertina album",
                            modifier = Modifier.size(57.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(57.dp)
                                .background(Color.Gray)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = "${canzone.titolo}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 15.sp
                        )

                        Text(
                            text = artistiCanzone.joinToString(", ") { it.nome } +
                            " - ${canzone.metadatiArtista}" +
                            " - ${formattaDurata(canzone.durata)}",
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

fun formattaGiorno(data: String): String {
    val giorno = LocalDate.parse(data)
    val oggi = LocalDate.now()

    return when (giorno) {
        oggi -> "Oggi"
        oggi.minusDays(1) -> "Ieri"
        else -> giorno.format(
            DateTimeFormatter.ofPattern("EEEE", Locale.ITALIAN)
        ).replaceFirstChar { it.titlecase(Locale.ITALIAN) }
    }
}