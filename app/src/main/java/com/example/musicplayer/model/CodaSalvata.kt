package com.example.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coda_salvata")
data class CodaSalvata(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val canzoneId: Long,
    val posizione: Int
)