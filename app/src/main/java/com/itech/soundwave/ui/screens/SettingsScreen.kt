package com.itech.soundwave.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itech.soundwave.ads.AdManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    
    // Premium features state
    var showEqualizerDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var equalizerUnlocked by remember { mutableStateOf(false) }
    var sleepTimerUnlocked by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "SoundWave Player",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Version 1.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Premium Features Section
            Text(
                text = "Premium Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Equalizer Feature
            PremiumFeatureCard(
                icon = Icons.Default.Equalizer,
                title = "Equalizer",
                description = "Customize your sound with professional EQ",
                isUnlocked = equalizerUnlocked,
                onUnlock = { showEqualizerDialog = true }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Sleep Timer Feature
            PremiumFeatureCard(
                icon = Icons.Default.Headphones,
                title = "Sleep Timer",
                description = "Fall asleep to music with auto-stop",
                isUnlocked = sleepTimerUnlocked,
                onUnlock = { showSleepTimerDialog = true }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // About Section
            Divider(modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Made by iTECH",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    
    // Equalizer Unlock Dialog
    if (showEqualizerDialog) {
        AlertDialog(
            onDismissRequest = { showEqualizerDialog = false },
            title = { Text("Unlock Equalizer") },
            text = { Text("Watch a short ad to unlock the professional equalizer and customize your sound experience!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEqualizerDialog = false
                        if (AdManager.isRewardedAdAvailable()) {
                            AdManager.showRewardedAd(context) {
                                equalizerUnlocked = true
                            }
                        }
                    }
                ) {
                    Text("Watch Ad")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEqualizerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Sleep Timer Unlock Dialog
    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = { Text("Unlock Sleep Timer") },
            text = { Text("Watch a short ad to unlock the sleep timer feature and fall asleep to your favorite music!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSleepTimerDialog = false
                        if (AdManager.isRewardedAdAvailable()) {
                            AdManager.showRewardedAd(context) {
                                sleepTimerUnlocked = true
                            }
                        }
                    }
                ) {
                    Text("Watch Ad")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PremiumFeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isUnlocked: Boolean,
    onUnlock: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.tertiaryContainer 
                            else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = if (isUnlocked) MaterialTheme.colorScheme.onTertiaryContainer 
                       else MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onTertiaryContainer 
                            else MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            
            if (!isUnlocked) {
                FilledTonalIconButton(
                    onClick = onUnlock,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Unlock",
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = "Unlocked",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
