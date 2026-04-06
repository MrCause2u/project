package com.itech.soundwave.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itech.soundwave.ads.BannerAd
import com.itech.soundwave.model.AudioItem
import com.itech.soundwave.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    onNavigateToPlayer: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val audioList by viewModel.audioList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SoundWave Library") },
                actions = {
                    TextButton(onClick = onNavigateToSettings) {
                        Text("Settings")
                    }
                }
            )
        }
    ) { padding ->
        if (audioList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No music found. Please allow permissions and ensure there is local audio.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(audioList) { audio ->
                    AudioListItem(audio = audio) {
                        viewModel.playAudio(audio)
                        onNavigateToPlayer()
                    }
                }
                
                // Banner Ad at the bottom of the list
                item {
                    BannerAd(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AudioListItem(audio: AudioItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(48.dp).padding(8.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = audio.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
            Text(text = audio.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}
