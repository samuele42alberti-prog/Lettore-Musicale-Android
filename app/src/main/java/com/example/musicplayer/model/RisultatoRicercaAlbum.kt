package com.example.musicplayer.model

import androidx.room.Embedded

data class RisultatoRicercaAlbum(
    @Embedded
    val album: Album,
    val punteggio: Int
)