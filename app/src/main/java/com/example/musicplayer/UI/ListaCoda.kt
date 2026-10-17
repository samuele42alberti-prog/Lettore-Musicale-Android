package com.example.musicplayer.UI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity

import android.net.Uri

import coil.compose.AsyncImage

import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.Album
import com.example.musicplayer.model.Artista

@Composable
fun ListaCoda(
    canzoni: List<Canzone>,
    albums: List<Album>,
    artistiCanzoni: Map<Canzone, List<Artista>>,
    canzoneSelezionata: Canzone?,
    onCanzoneClick: (Canzone) -> Unit,
    onIndietroSchermata: () -> Unit,
    onApriPlayer: () -> Unit,
    onCodaModificata: (Int, Int) -> Unit,
    onCancellaDaCoda: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val lazyListState = rememberLazyListState()

    var indiceTrascinato by remember { mutableStateOf<Int?>(null) }
    var offsetTrascinamento by remember { mutableStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.width(300.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onIndietroSchermata,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Freccia giù",
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(
                onClick = onApriPlayer,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Canzone",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        val indiceCanzoneAttuale = canzoni.indexOf(canzoneSelezionata)

        val coda = if (indiceCanzoneAttuale >= 0) {
            canzoni.drop(indiceCanzoneAttuale)
        } else {
            canzoni
        }

        var codaTrascinabile by remember(coda) {
            mutableStateOf(coda)
        }

        val albumsMap = albums.associateBy { it.id }

        val density = LocalDensity.current
        val altezzaElementoPx = with(density) { 90.dp.toPx() }

        LazyColumn(
            state = lazyListState,
            modifier = modifier
        ) {
            items(
                items = codaTrascinabile,
                key = { it.id }
            ) { canzone ->

                val codaAggiornata = rememberUpdatedState(codaTrascinabile)

                val colore =
                    if (canzone == canzoneSelezionata) {
                        Color(0xFF7B61FF)
                    } else {
                        Color.Black
                    }

                val album = albumsMap[canzone.albumId]

                val artistiCanzone = artistiCanzoni[canzone] ?: emptyList()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(90.dp)
                        .clickable {
                            onCanzoneClick(canzone)
                        }
                        .graphicsLayer {
                            if (
                                indiceTrascinato ==
                                codaAggiornata.value.indexOf(canzone)
                            ) {
                                translationY = offsetTrascinamento
                            }
                        }
                        .pointerInput(canzone.id) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    indiceTrascinato =
                                        codaAggiornata.value.indexOf(canzone)

                                    offsetTrascinamento = 0f
                                },

                                onDrag = { change, dragAmount ->
                                    change.consume()

                                    offsetTrascinamento += dragAmount.y

                                    while (true) {
                                        val vecchioIndice =
                                            indiceTrascinato ?: break

                                        if (vecchioIndice == 0) {
                                            break
                                        }

                                        val nuovoIndice = when {
                                            offsetTrascinamento >
                                                    altezzaElementoPx / 2 &&
                                                    vecchioIndice <
                                                    codaAggiornata.value.lastIndex ->
                                                vecchioIndice + 1

                                            offsetTrascinamento <
                                                    -altezzaElementoPx / 2 &&
                                                    vecchioIndice > 0 ->
                                                vecchioIndice - 1

                                            else -> null
                                        } ?: break

                                        if (nuovoIndice == 0) {
                                            break
                                        }

                                        val direzione =
                                            if (nuovoIndice > vecchioIndice) {
                                                1
                                            } else {
                                                -1
                                            }

                                        val nuovaCoda =
                                            codaAggiornata.value.toMutableList()

                                        val spostata =
                                            nuovaCoda.removeAt(vecchioIndice)

                                        nuovaCoda.add(
                                            nuovoIndice,
                                            spostata
                                        )

                                        codaTrascinabile = nuovaCoda

                                        indiceTrascinato = nuovoIndice

                                        offsetTrascinamento -=
                                            direzione * altezzaElementoPx

                                        onCodaModificata(
                                            vecchioIndice,
                                            nuovoIndice
                                        )
                                    }
                                },

                                onDragEnd = {
                                    indiceTrascinato = null
                                    offsetTrascinamento = 0f
                                },

                                onDragCancel = {
                                    indiceTrascinato = null
                                    offsetTrascinamento = 0f
                                }
                            )
                        }
                ) {
                    if (album?.percorsoCopertina != null) {
                        AsyncImage(
                            model = Uri.parse(album.percorsoCopertina),
                            contentDescription = "Copertina",
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
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = canzone.titolo,
                            color = colore,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp
                        )

                        Text(
                            text =
                                artistiCanzone.joinToString(", ") { it.nome } + " - " +
                                "${canzone.metadatiArtista} - " +
                                "${formattaDurata(canzone.durata)}",
                            color = colore,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 10.sp
                        )
                    }

                    if (canzone != canzoneSelezionata) {
                        IconButton(
                            onClick = {
                                onCancellaDaCoda(
                                    codaAggiornata.value.indexOf(canzone)
                                )
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Cancella",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}