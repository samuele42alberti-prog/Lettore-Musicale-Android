
package com.example.musicplayer.UI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MiniPlayerArea(
    viewModel: MusicPlayerViewModel
) {

    if(viewModel.canzoneSelezionata.value != null && viewModel.artistiCanzoneAttuale.value.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(-4.dp),
            modifier = Modifier
                .height(110.dp)
        ) {
            MiniPlayer(
                canzone = viewModel.canzoneSelezionata.value!!,
                artisti = viewModel.artistiCanzoneAttuale.value!!,
                album = viewModel.albumSelezionato.value,
                riproducendo = viewModel.riproducendo.value,
                onIndietro = { viewModel.indietro() },
                onPlayPausa = { viewModel.playPausa() },
                onAvanti = { viewModel.avanti() },
                onClick = { viewModel.apriSchermata(SchermataSecondaria.PLAYER) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            BarraAvanzamento(
                posizione = viewModel.posizioneAttuale.value,
                durataMs = viewModel.canzoneSelezionata.value?.durata?.times(1000L) ?: 0L,
                statoTrascinamento = { viewModel.trascinandoSlider.value = it },
                cambioPosizione = { viewModel.regolaPosizione(it) }
            )
        }
    }
}