package com.example.musicplayer.UI

import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.KeyboardArrowDown

import android.net.Uri

import coil.compose.AsyncImage

import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.Artista
import com.example.musicplayer.model.Album

@Composable
fun PlayerCompleto(
    canzone: Canzone,
    artisti: List<Artista>,
    album: Album?,
    riproducendo: Boolean,
    onIndietro: () -> Unit,
    onPlayPausa: () -> Unit,
    onAvanti: () -> Unit,
    onIndietroSchermata: () -> Unit,
    onApriCoda: () -> Unit,
    viewModel: MusicPlayerViewModel
) {
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
                onClick = onApriCoda,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = "Coda",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        if(album?.percorsoCopertina != null) {
            AsyncImage(
                model = Uri.parse(album.percorsoCopertina),
                contentDescription = "copertina album",
                modifier = Modifier.size(300.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.Gray)
            )
        }

        Column (
            modifier = Modifier.width(300.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "${canzone.titolo}",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp
            )

            Text(
                text = "${artisti.joinToString(", ") { it.nome }} - ${canzone.metadatiArtista}",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Controlli(
                riproducendo = riproducendo,
                onIndietro = onIndietro,
                onPlayPausa = onPlayPausa,
                onAvanti = onAvanti,
                compatto = false
            )
        }

        BarraAvanzamento(
            posizione = viewModel.posizioneAttuale.value,
            durataMs = viewModel.canzoneSelezionata.value?.durata?.times(1000L) ?: 0L,
            statoTrascinamento = { viewModel.trascinandoSlider.value = it },
            cambioPosizione = { viewModel.regolaPosizione(it) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                formattaDurata((viewModel.posizioneAttuale.value / 1000).toInt()),
                fontSize = 16.sp
            )

            Text(
                formattaDurata(viewModel.canzoneSelezionata.value?.durata ?: 0),
                fontSize = 16.sp
            )
        }
    }
}

fun formattaDurata(secondi: Int): String {
    val minuti = secondi / 60
    val ore = minuti / 60
    val minutiRimanenti = minuti % 60
    val secondiRimanenti = secondi % 60

    if(ore > 0) {
        return "%d:%02d:%02d".format(ore, minutiRimanenti, secondiRimanenti)
    }

    return "%d:%02d".format(minuti, secondiRimanenti)
}