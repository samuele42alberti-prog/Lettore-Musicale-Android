package com.example.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stato_player")
data class StatoPlayer(
    @PrimaryKey
    val id: Int = 1,
    val canzoneId: Long?,
    val indiceCorrente: Int,
    val posizione: Long
)