package com.example.musicplayer.UI

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Modifier

@Composable
fun MenuNavigazione(
    viewModel: MusicPlayerViewModel
) {
    Spacer(modifier = Modifier.height(5.dp))

    NavigationBar(modifier = Modifier.height(30.dp)) {
        NavigationBarItem(
            selected = viewModel.schermataPrincipale.value == SchermataPrincipale.LIBRERIA,
            onClick = { 
                viewModel.cambiaSchermata(SchermataPrincipale.LIBRERIA);
                viewModel.chiudiTutteSchermateSecondarie()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = "libreria"
                )
            }
        )

        NavigationBarItem(
            selected = viewModel.schermataPrincipale.value == SchermataPrincipale.RICERCA,
            onClick = { 
                viewModel.cambiaSchermata(SchermataPrincipale.RICERCA);
                viewModel.chiudiTutteSchermateSecondarie()
            },
            icon = { 
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "ricerca"
                )
            }
        )

        NavigationBarItem(
            selected = viewModel.schermataPrincipale.value == SchermataPrincipale.IMPORTAZIONE,
            onClick = { 
                viewModel.cambiaSchermata(SchermataPrincipale.IMPORTAZIONE);
                viewModel.chiudiTutteSchermateSecondarie()
            },
            icon = { 
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "importazione"
                )
            }
        )
    }
}