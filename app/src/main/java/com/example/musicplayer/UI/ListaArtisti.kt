package com.example.musicplayer.UI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton


import com.example.musicplayer.model.Artista

@Composable
fun ListaArtisti(
    viewModel: MusicPlayerViewModel,
    artisti: List<Artista>,
    onArtistaClick: (Artista) -> Unit,
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

    val lazyListState = viewModel.lazyListStateListaArtisti

    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        items(artisti) { artista ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onArtistaClick(artista) }
                    .padding(15.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = artista.nome,
                        fontSize = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}