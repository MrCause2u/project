package com.itech.soundwave.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.itech.soundwave.ads.AdManager
import com.itech.soundwave.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentTrack by viewModel.playerController.currentTrack.collectAsState()
    val isPlaying by viewModel.playerController.isPlaying.collectAsState()
    val context = LocalContext.current
    
    // Track premium features unlocked via rewarded ads
    var isAdFreeModeUnlocked by remember { mutableStateOf(false) }
    var showRewardedAdDialog by remember { mutableStateOf(false) }
    
    // Load rewarded ad on composition
    androidx.compose.runtime.LaunchedEffect(Unit) {
        AdManager.loadRewardedAd(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Now Playing") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentTrack?.albumArtUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.large)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = currentTrack?.title ?: "No Track Selected",
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1
            )
            Text(
                text = currentTrack?.artist ?: "Unknown Artist",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.playerController.skipToPrevious() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(48.dp))
                }

                FloatingActionButton(
                    onClick = {
                        if (isPlaying) viewModel.playerController.pause() else viewModel.playerController.play()
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.playerController.skipToNext() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(48.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Premium Features Section - Unlocked via Rewarded Ads
            if (!isAdFreeModeUnlocked) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Unlock Premium Features",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Unlock Ad-Free Mode
                            PremiumFeatureButton(
                                icon = Icons.Default.Lock,
                                label = "Ad-Free",
                                isUnlocked = false,
                                onClick = { showRewardedAdDialog = true }
                            )
                            
                            // More premium features can be added here
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Watch a short ad to unlock",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Show ad-free mode indicator
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ad-Free Mode Active",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
    
    // Rewarded Ad Dialog
    if (showRewardedAdDialog) {
        AlertDialog(
            onDismissRequest = { showRewardedAdDialog = false },
            title = { Text("Unlock Ad-Free Mode") },
            text = { Text("Watch a short video ad to enjoy 30 minutes of ad-free music playback!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRewardedAdDialog = false
                        if (AdManager.isRewardedAdAvailable()) {
                            AdManager.showRewardedAd(context) {
                                isAdFreeModeUnlocked = true
                            }
                        }
                    }
                ) {
                    Text("Watch Ad")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRewardedAdDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Auto-reset ad-free mode after 30 minutes
    if (isAdFreeModeUnlocked) {
        DisposableEffect(Unit) {
            val job = kotlinx.coroutines.MainScope().launch {
                kotlinx.coroutines.delay(30 * 60 * 1000L) // 30 minutes
                isAdFreeModeUnlocked = false
            }
            onDispose { job.cancel() }
        }
    }
}

@Composable
private fun PremiumFeatureButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        FilledTonalIconButton(
            onClick = if (isUnlocked) { {} } else onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
