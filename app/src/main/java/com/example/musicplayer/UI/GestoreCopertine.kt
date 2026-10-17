package com.example.musicplayer.UI

import android.net.Uri

import androidx.documentfile.provider.DocumentFile

object GestoreCopertine {
    fun trovaCopertina(cartella: DocumentFile): Uri? {
        val immagini = cartella.listFiles()
            .filter { file ->
                val nome = file.name?.lowercase() ?: return@filter false

                nome == "cover.jpg" ||
                nome == "cover.jpeg" ||
                nome == "cover.png" ||
                nome == "cover.webp" ||
                nome == "folder.jpg" ||
                nome == "folder.jpeg" ||
                nome == "folder.png" ||
                nome == "folder.webp" ||
                nome == "album.jpg" ||
                nome == "album.jpeg" ||
                nome == "album.png" ||
                nome == "album.webp"
            }
        return immagini.firstOrNull()?.uri
    }
}