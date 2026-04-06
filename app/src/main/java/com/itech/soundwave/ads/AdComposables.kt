package com.itech.soundwave.ads

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Banner Ad Composable for SoundWave Music Player
 * Strategic placement: Bottom of Library screen (non-intrusive)
 */
@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = AdManager.getBannerAdUnitId()
) {
    val context = LocalContext.current
    
    var isAdLoaded by remember { mutableStateOf(false) }
    var isAdFailed by remember { mutableStateOf(false) }
    
    AndroidView(
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                listener = object : AdListener() {
                    override fun onAdLoaded() {
                        isAdLoaded = true
                        isAdFailed = false
                        Log.d("BannerAd", "Ad loaded successfully")
                    }
                    
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        isAdFailed = true
                        isAdLoaded = false
                        Log.e("BannerAd", "Ad failed to load: ${adError.message}")
                    }
                    
                    override fun onAdOpened() {
                        Log.d("BannerAd", "Ad opened")
                    }
                    
                    override fun onAdClosed() {
                        Log.d("BannerAd", "Ad closed")
                    }
                    
                    override fun onAdImpression() {
                        Log.d("BannerAd", "Ad impression recorded")
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(vertical = 4.dp)
    )
    
    // Clean up when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            Log.d("BannerAd", "Banner ad cleaned up")
        }
    }
}

/**
 * Native Ad Placeholder Composable
 * Strategic placement: Between song list items in Library (blended with content)
 * This is a placeholder - full native ad implementation requires more complex setup
 */
@Composable
fun NativeAdPlaceholder(
    modifier: Modifier = Modifier,
    onAdClicked: () -> Unit = {}
) {
    // Native ads require more complex setup with AdLoader and NativeAdView
    // This placeholder shows where native ads would be integrated
    // For now, we'll use a simple placeholder that can be expanded later
    
    androidx.compose.foundation.Background(
        color = androidx.compose.ui.graphics.Color(0xFFF5F5F5),
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(8.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            androidx.compose.material3.Text(
                text = "Sponsored Content",
                style = androidx.compose.ui.text.TextStyle(
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            )
        }
    }
}

/**
 * Ad Indicator Composable
 * Shows a small indicator that content is sponsored
 */
@Composable
fun AdIndicator(
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .padding(vertical = 4.dp)
    ) {
        androidx.compose.material3.Text(
            text = "Advertisement",
            style = androidx.compose.ui.text.TextStyle(
                color = androidx.compose.ui.graphics.Color.Gray,
                fontSize = androidx.compose.ui.unit.TextUnit.Companion.sp(10)
            )
        )
    }
}