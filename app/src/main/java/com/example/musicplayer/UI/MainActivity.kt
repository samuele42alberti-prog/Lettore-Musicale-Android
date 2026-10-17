package com.example.musicplayer.UI

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable

import android.media.MediaMetadataRetriever

import com.example.musicplayer.model.MetadatiCanzone

import kotlinx.coroutines.launch

import android.content.Intent

import androidx.documentfile.provider.DocumentFile

import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MusicPlayerViewModel = viewModel()

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicPlayerScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MusicPlayerScreen(viewModel: MusicPlayerViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.riproducendo.value) {
        while(viewModel.riproducendo.value) {
            viewModel.aggiornaPosizione()
            kotlinx.coroutines.delay(500)
        }
    }

    val selettoreFile = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if(uri != null)
        {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            context.contentResolver.takePersistableUriPermission(
                uri,
                flags
            )

            val metadati = leggiMetaDati(uri, context)

            viewModel.impostaMetadatiFile(metadati)

            viewModel.importazioneInCorso.value = true
        }
    }

    val selettoreCartella = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if(uri != null) {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            context.contentResolver.takePersistableUriPermission(
                uri,
                flags
            )

            val fileAudio = trovaFileAudio(uri, context)

            scope.launch {
                val listaMetadati = mutableListOf<MetadatiCanzone>()

                fileAudio.forEachIndexed { indice, canzone ->
                    val metadati = leggiMetaDati(canzone.uri, context)

                    val metadatiConTraccia = metadati.copy(
                        numeroTraccia = indice + 1
                    )

                    listaMetadati.add(metadatiConTraccia)
                }

                viewModel.impostaMetadatiCartella(listaMetadati, uri)

                viewModel.importazioneInCorso.value = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 0.dp,
                bottom = 0.dp,
                start = 10.dp,
                end = 10.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Music Player",
            style = MaterialTheme.typography.headlineMedium
        )

        HomeScreen(
            viewModel,
            onImportaCanzone = { selettoreFile.launch(arrayOf("audio/*")) },
            onImportaAlbum = { selettoreCartella.launch(null) }
        )
    }
}