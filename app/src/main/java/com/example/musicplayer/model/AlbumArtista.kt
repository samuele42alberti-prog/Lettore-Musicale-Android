package com.example.musicplayer.model

import androidx.room.Entity

@Entity(
    tableName = "album_artisti",
    primaryKeys = ["albumId", "artistaId"]
)
data class AlbumArtista(
    val albumId: Long,
    val artistaId: Long
)