package com.example.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ascolti")
data class Ascolto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val canzoneId: Long,
    val timestamp: Long
)