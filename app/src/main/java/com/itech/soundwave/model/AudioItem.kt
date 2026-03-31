package com.itech.soundwave.model

import android.net.Uri

data class AudioItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val size: Int,
    val albumArtUri: Uri? = null
)
