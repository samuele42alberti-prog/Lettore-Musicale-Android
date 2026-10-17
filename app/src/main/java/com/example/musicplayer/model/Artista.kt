package com.example.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artisti")
data class Artista (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String
)