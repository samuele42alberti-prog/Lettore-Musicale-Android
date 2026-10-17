package com.example.musicplayer.UI

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.LocalTextStyle

import android.net.Uri

import coil.compose.AsyncImage

import com.example.musicplayer.model.Genere
import com.example.musicplayer.model.Artista

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormImportazione(
    titolo: String,
    album: String,
    artisti: List<Artista>,
    artistiSelezionati: List<Artista>,
    ricercaArtista: String,
    onArtistaClick: (Artista) -> Unit,
    onRicercaArtistaChange: (String) -> Unit,
    onNuovoArtista: (String) -> Unit,
    nuovoGenere: String,
    generi: List<Genere>,
    generiSelezionati: List<Genere>,
    copertina: String?,
    annoUscita: String,
    onGenereClick: (Genere) -> Unit,
    onNuovoGenere: (String) -> Unit,
    onAlbumChange: (String) -> Unit,
    onGenereChange: (String) -> Unit,
    onAnnoChange: (String) -> Unit,
    onConferma: () -> Unit,
    onAnnulla: () -> Unit,
    errore: String?
) {
    if(!titolo.isEmpty()) {
        Text("Importazione canzone - ${titolo}")
    } else {
        Text("Importazione album")

        if(copertina != null) {
            AsyncImage(
                model = Uri.parse(copertina),
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
    }

    Text("Album")

    TextField(
        value = album,
        onValueChange = onAlbumChange,
        label = { Text("Album o Singolo") },
        singleLine = true,
        modifier = Modifier.width(200.dp)
    )

    SelettoreArtista(
        artisti = artisti,
        artistiSelezionati = artistiSelezionati,
        ricercaArtista = ricercaArtista,
        onArtistaClick = onArtistaClick,
        onRicercaArtistaChange = onRicercaArtistaChange,
        onNuovoArtista = onNuovoArtista
    )

    Text("Anno di uscita")

    TextField(
        value = annoUscita,
        onValueChange = {
            if(it.all { carattere -> carattere.isDigit() }) {
                onAnnoChange(it)
            }
        },
        label = { Text("Anno di uscita") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        modifier = Modifier.width(200.dp)
    )

    Text("Generi")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        generi.forEach{ genere ->
            FilterChip(
                selected = generiSelezionati.contains(genere),
                onClick = {
                    onGenereClick(genere)
                },
                label = {
                    Text(genere.nome)
                }
            )
        }
    }

    TextField(
        value = nuovoGenere,
        onValueChange = onGenereChange,
        label = { Text("Nuovo Genere") },
        modifier = Modifier.width(200.dp),
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center
        ),
        singleLine = true
    )

    Button(onClick = { onNuovoGenere(nuovoGenere) }) {
        Text("Aggiungi")
    }

    if(errore != null) {
        Text(
            text = errore,
            color = Color.Red
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onAnnulla) {
            Text("Annulla")
        }

        Button(onClick = onConferma) {
            Text("Conferma")
        }
    }
}