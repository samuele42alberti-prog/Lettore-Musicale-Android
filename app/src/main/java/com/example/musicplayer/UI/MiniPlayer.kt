package com.example.musicplayer.UI

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow

import android.net.Uri

import coil.compose.AsyncImage

import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.Artista
import com.example.musicplayer.model.Album

@Composable
fun MiniPlayer(
    canzone: Canzone,
    artisti: List<Artista>,
    album: Album?,
    riproducendo: Boolean,
    onIndietro: () -> Unit,
    onPlayPausa: () -> Unit,
    onAvanti: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .clickable {
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = canzone.titolo,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 14.sp
            )

            Text(
                text = "${artisti.joinToString(", ") { it.nome }} - ${canzone.metadatiArtista}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 12.sp
            )
        }

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
    }

    Spacer(modifier = Modifier.height(12.dp))

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
            compatto = true
        )
    }
}
