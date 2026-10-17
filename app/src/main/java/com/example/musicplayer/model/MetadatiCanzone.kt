package com.example.musicplayer.model

data class MetadatiCanzone (
    val titolo: String,
    val artista: String,
    val album: String?,
    val numeroTraccia: Int?,
    val durata: Int,
    val percorsoFile: String
)