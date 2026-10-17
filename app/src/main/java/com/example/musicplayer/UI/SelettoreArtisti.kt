package com.example.musicplayer.UI

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.musicplayer.model.Artista

@Composable
fun SelettoreArtista(
    artisti: List<Artista>,
    artistiSelezionati: List<Artista>,
    ricercaArtista: String,
    onArtistaClick: (Artista) -> Unit,
    onRicercaArtistaChange: (String) -> Unit,
    onNuovoArtista: (String) -> Unit
) {
    Text("Artisti")

    if(artistiSelezionati.isNotEmpty()) {
        artistiSelezionati.forEach{ artista ->
            Text("• ${artista.nome}")
        }
    }

    TextField(
        value = ricercaArtista,
        onValueChange = onRicercaArtistaChange,
        label = { Text("Cerca o aggiungi un artista") },
        singleLine = true,
        modifier = Modifier.width(200.dp)
    )

    Button(
        onClick = {
            onNuovoArtista(ricercaArtista)
        }
    ) {
        Text("Aggiungi")
    }

    artisti.forEach{ artista ->
        FilterChip(
            selected = artistiSelezionati.contains(artista),
            onClick = {
                onArtistaClick(artista)
            },
            label = {
                Text(artista.nome)
            }
        )
    }
}