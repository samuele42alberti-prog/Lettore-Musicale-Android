package com.example.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

import com.example.musicplayer.model.StatoPlayer

@Dao
interface StatoPlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserisci(statoPlayer: StatoPlayer): Long

    @Query("SELECT * FROM stato_player")
    suspend fun leggi(): StatoPlayer?
}