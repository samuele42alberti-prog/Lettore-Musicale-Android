package com.example.musicplayer.UI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size

@Composable
fun Controlli(
    riproducendo: Boolean,
    onIndietro: () -> Unit,
    onPlayPausa: () -> Unit,
    onAvanti: () -> Unit,
    compatto: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onIndietro,
            modifier = Modifier.size( if(compatto) 36.dp else 60.dp)
        ) { 
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Precedente",
                modifier = Modifier.size( if(compatto) 24.dp else 40.dp)
            )
        }

        IconButton(
            onClick = onPlayPausa,
            modifier = Modifier.size( if(compatto) 36.dp else 60.dp)
        ) { 
            if(riproducendo) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pausa",
                    modifier = Modifier.size( if(compatto) 24.dp else 40.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    modifier = Modifier.size( if(compatto) 24.dp else 40.dp)
                )
            }
        }

        IconButton(
            onClick = onAvanti,
            modifier = Modifier.size( if(compatto) 36.dp else 60.dp)
        ) { 
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Successiva",
                modifier = Modifier.size( if(compatto) 24.dp else 40.dp)
            )
        }
    }
}