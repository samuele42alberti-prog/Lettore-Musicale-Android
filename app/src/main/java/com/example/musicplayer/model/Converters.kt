package com.example.musicplayer.database

import androidx.room.TypeConverter
import com.example.musicplayer.model.TipoAlbum

class Converters {
    @TypeConverter
    fun daTipoAlbum(tipo: TipoAlbum): String = tipo.name

    @TypeConverter
    fun aTipoAlbum(tipo: String): TipoAlbum = TipoAlbum.valueOf(tipo)
}