package com.example.musicplayer.UI

import androidx.media3.common.Player
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

import android.net.Uri

import java.io.File

import com.example.musicplayer.model.Canzone
import com.example.musicplayer.model.Artista
import com.example.musicplayer.model.Album

class PlayerController(private val player: Player) {

    fun impostaCoda(
        artistiCanzoni: Map<Canzone, List<Artista>>,
        canzoni: List<Canzone>, 
        albums: List<Album>, 
        indice: Int,
        posizione: Long,
        riproduci: Boolean
    ) {
        val albumsMap = albums.associateBy { it.id }

        val mediaItems = canzoni.map { canzone ->
            val artistiCanzone = artistiCanzoni[canzone] ?: emptyList()
            val album = albumsMap[canzone.albumId]

            creaMediaItem(canzone, artistiCanzone, album)
        }

        player.setMediaItems(mediaItems, indice, posizione)
        player.repeatMode = Player.REPEAT_MODE_ALL

        player.prepare()

        if(riproduci) {
            player.play()
        } else {
            player.pause()
        }
    }

    fun creaMediaItem(
        canzone: Canzone, 
        artisti: List<Artista>, 
        album: Album?
    ): MediaItem {
        return MediaItem.Builder()
            .setMediaId(canzone.id.toString())
            .setUri(canzone.percorsoFile)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(canzone.titolo)
                    .setArtist("${artisti.joinToString(", ") { it.nome }} - ${canzone.metadatiArtista}")
                    .setArtworkUri(album?.percorsoCopertina?.let { Uri.parse(it)})
                    .build()
            )
            .build()
    }

    fun aggiungiCanzoneACoda(
        indice: Int, 
        canzone: Canzone,
        artisti: List<Artista>,
        album: Album?
    ) {
        player.addMediaItem(indice, creaMediaItem(canzone, artisti, album))
    }

    fun spostaCanzone(indiceVecchio: Int, indiceNuovo: Int) {
        player.moveMediaItem(indiceVecchio, indiceNuovo)
    }

    fun cancellaCanzoneDaCoda(indice: Int) {
        player.removeMediaItem(indice)
    }

    fun indiceCanzoneCorrente(): Int {
        return player.currentMediaItemIndex
    }

    fun numeroElementiCoda(): Int {
        return player.mediaItemCount
    }

    fun idCanzoniInCoda(): List<Long> {
        return (0 until player.mediaItemCount).mapNotNull { indice ->
            player.getMediaItemAt(indice).mediaId.toLongOrNull()
        }
    }

    fun pausa() {
        player.pause()
    }

    fun riprendi() {
        player.play()
    }

    fun regolaPosizione(posizione: Long) {
        player.seekTo(posizione)
    }

    fun posizioneAttuale(): Long {
        return player.currentPosition
    }

    fun avanti() {
        player.seekToNextMediaItem()
    }

    fun indietro() {
        player.seekToPreviousMediaItem()
    }

    fun vaiACanzone(indice: Int) {
        player.seekTo(indice, 0L)
        player.play()
    }
}