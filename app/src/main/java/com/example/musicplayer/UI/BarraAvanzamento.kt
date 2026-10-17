package com.example.musicplayer.UI

import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp

@Composable
fun BarraAvanzamento(posizione: Long, durataMs: Long, statoTrascinamento: (Boolean) -> Unit, cambioPosizione: (Long) -> Unit) {
    var posizioneLocale by remember { mutableStateOf(posizione.toFloat()) }
    var trascinando by remember { mutableStateOf(false) }

    LaunchedEffect(posizione, trascinando) {
        if(!trascinando) {
            posizioneLocale = posizione.toFloat()
        }
    }

    Slider(
        value = posizioneLocale.toFloat(),
        onValueChange = {
            if(!trascinando) {
                trascinando = true
                statoTrascinamento(true)
            }

            posizioneLocale = it
        },
        onValueChangeFinished = {
            trascinando = false
            statoTrascinamento(false)
            cambioPosizione(posizioneLocale.toLong())
        },
        valueRange = 0f..durataMs.toFloat(),
        modifier = Modifier
            .fillMaxWidth()
    )
}