package com.example.musicplayer.UI

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

import android.media.MediaMetadataRetriever

import com.example.musicplayer.model.MetadatiCanzone

fun leggiMetaDati(uri: android.net.Uri, context: android.content.Context): MetadatiCanzone {
    val retriever = MediaMetadataRetriever()

    retriever.setDataSource(context, uri)

    val titolo = retriever.extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_TITLE
    ) ?: "Titolo sconosciuto"

    val artista = retriever.extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_ARTIST
    ) ?: "Artista sconosciuto"

    val album = retriever.extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_ALBUM
    )

    val numeroTraccia = retriever.extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER
    )?.substringBefore("/")?.toIntOrNull()

    val durata = retriever.extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_DURATION
    )?.toIntOrNull() ?: 0

    retriever.release()

    return MetadatiCanzone(
        titolo,
        artista,
        album,
        numeroTraccia,
        durata / 1000,
        uri.toString()
    )
}

fun trovaFileAudio(
    uriCartella: Uri,
    context: Context
): List<DocumentFile> {

    val cartella = DocumentFile.fromTreeUri(
        context,
        uriCartella
    )

    return cartella 
        ?.listFiles()
        ?.filter {
            it.isFile &&
            it.type?.startsWith("audio/") == true
        }
        ?.sortedBy { it.name?.lowercase() }
        ?: emptyList()
}