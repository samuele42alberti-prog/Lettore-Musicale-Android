package com.example.musicplayer.UI

import androidx.media3.session.MediaSessionService
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.common.Player

import kotlinx.coroutines.*

import com.example.musicplayer.database.MusicDatabase
import com.example.musicplayer.repository.MusicRepository

class PlayerService : MediaSessionService() {
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var repository: MusicRepository

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    override fun onCreate() {
        super.onCreate()

        val database = MusicDatabase.getDatabase(application)

        repository = MusicRepository(
            database,
            database.artistaDao(),
            database.albumDao(),
            database.canzoneDao(),
            database.genereDao(),
            database.albumGenereDao(),
            database.albumArtistaDao(),
            database.canzoneArtistaDao(),
            database.ascoltoDao(),
            database.codaSalvataDao(),
            database.codaPrioritariaDao(),
            database.statoPlayerDao()
        )

        player = ExoPlayer.Builder(this).build()

        mediaSession = MediaSession.Builder(this, player).build()

        avviaSalvataggioPeriodico()
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        serviceScope.cancel()
        mediaSession.release()
        player.release()
        super.onDestroy()
    }

    private fun avviaSalvataggioPeriodico() {
        serviceScope.launch {
            while(isActive) {
                salvaStatoPlayer()
                delay(5000)
            }
        }
    }

    private suspend fun salvaStatoPlayer() {
        val mediaItem = player.currentMediaItem ?: return
        val canzoneId = mediaItem.mediaId.toLongOrNull() ?: return

        repository.salvaStatoPlayer(
            canzoneId = canzoneId,
            indiceCorrente = player.currentMediaItemIndex,
            posizione = player.currentPosition
        )
    }
}