package com.example.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generi")
data class Genere(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String
)