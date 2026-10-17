package com.example.musicplayer.model

import androidx.room.Embedded

data class RisultatoRicercaCanzone(
    @Embedded
    val canzone: Canzone,
    val punteggio: Int
)