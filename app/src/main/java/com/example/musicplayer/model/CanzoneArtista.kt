package com.example.musicplayer.model

import androidx.room.Entity

@Entity(
    tableName = "canzoni_artisti",
    primaryKeys = ["canzoneId", "artistaId"]
)
data class CanzoneArtista(
    val canzoneId: Long,
    val artistaId: Long
)