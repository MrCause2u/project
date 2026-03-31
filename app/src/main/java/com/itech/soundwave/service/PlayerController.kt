package com.itech.soundwave.service

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.itech.soundwave.model.AudioItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerController(context: Context) {
    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val _currentTrack = MutableStateFlow<AudioItem?>(null)
    val currentTrack: StateFlow<AudioItem?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var currentPlaylist: List<AudioItem> = emptyList()

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            setupListener()
        }, ContextCompat.getMainExecutor(context))
    }

    private fun setupListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val audioId = mediaItem?.mediaId?.toLongOrNull()
                if (audioId != null) {
                    _currentTrack.value = currentPlaylist.find { it.id == audioId }
                }
            }
        })
    }

    fun setPlaylist(playlist: List<AudioItem>, startIndex: Int) {
        currentPlaylist = playlist
        val mediaItems = playlist.map { 
            MediaItem.Builder()
                .setMediaId(it.id.toString())
                .setUri(it.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setArtist(it.artist)
                        .setAlbumTitle(it.album)
                        .setArtworkUri(it.albumArtUri)
                        .build()
                )
                .build()
        }
        mediaController?.run {
            setMediaItems(mediaItems, startIndex, 0L)
            prepare()
            play()
        }
    }

    fun play() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun skipToNext() {
        mediaController?.seekToNextMediaItem()
    }

    fun skipToPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }

    fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }
}
