package com.example.musicplayer.model

import androidx.room.Entity

@Entity(
    tableName = "album_generi",
    primaryKeys = ["albumId", "genereId"]
)
data class AlbumGenere(
    val albumId: Long,
    val genereId: Long
)