package com.example.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "canzoni")
data class Canzone (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titolo: String,
    val durata: Int,
    val percorsoFile: String,
    val metadatiArtista: String,
    val albumId: Long?,
    val numeroTraccia: Int?
)