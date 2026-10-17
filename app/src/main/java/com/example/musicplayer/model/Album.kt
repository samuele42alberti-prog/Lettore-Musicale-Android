package com.example.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoAlbum {
    ALBUM,
    SINGOLO
}
@Entity(tableName = "album")
data class Album (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titolo: String,
    val percorsoCopertina: String?,
    val anno: Int,
    val dataImportazione: Long,
    val tipoAlbum: TipoAlbum
)