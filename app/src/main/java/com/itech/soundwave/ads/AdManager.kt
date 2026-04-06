package com.itech.soundwave.ads

import android.app.Activity
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

/**
 * Centralized Ad Manager for SoundWave Music Player
 * Implements strategic ad placement to minimize user disruption
 */
object AdManager : LifecycleObserver {
    
    private const val TAG = "AdManager"
    
    // Production Ad Unit IDs
    private const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-1351624039173419/4083399639"
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-1351624039173419/3254262649"
    private const val REWARDED_AD_UNIT_ID = "ca-app-pub-1351624039173419/4184200935"
    private const val REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-1351624039173419/7816324188"
    
    // Ad load intervals (in milliseconds) to prevent overwhelming users
    private const val APP_OPEN_COOLDOWN = 4 * 60 * 60 * 1000L // 4 hours
    private const val INTERSTITIAL_COOLDOWN = 3 * 60 * 1000L // 3 minutes
    private const val REWARDED_COOLDOWN = 30 * 1000L // 30 seconds
    
    // Tracking variables
    private var appOpenAd: AppOpenAd? = null
    private var appOpenAdLoadTime: Long = 0
    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdLoadTime: Long = 0
    private var rewardedAd: RewardedAd? = null
    private var rewardedAdLoadTime: Long = 0
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isAppOpenAdLoading = false
    private var isInterstitialAdLoading = false
    
    // Callbacks for rewarded ads
    private var onRewardedAdCompleted: (() -> Unit)? = null
    
    /**
     * Initialize AdMob SDK
     * Call this in Application.onCreate() or MainActivity.onCreate()
     */
    fun initialize(activity: Activity) {
        MobileAds.initialize(activity) { initializationStatus ->
            Log.d(TAG, "Mobile Ads initialized: ${initializationStatus.applicationStatus}")
        }
    }
    
    /**
     * Load App Open Ad (shows when app is opened from background)
     * Strategic placement: Only shows after 4+ hours of app being in background
     */
    fun loadAppOpenAd(activity: Activity) {
        if (isAppOpenAdLoading) return
        
        val currentTime = System.currentTimeMillis()
        if (currentTime - appOpenAdLoadTime < APP_OPEN_COOLDOWN) {
            Log.d(TAG, "App Open Ad on cooldown")
            return
        }
        
        isAppOpenAdLoading = true
        val adRequest = AdRequest.Builder().build()
        
        AppOpenAd.load(
            activity,
            APP_OPEN_AD_UNIT_ID,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    appOpenAdLoadTime = System.currentTimeMillis()
                    isAppOpenAdLoading = false
                    Log.d(TAG, "App Open Ad loaded")
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isAppOpenAdLoading = false
                    Log.e(TAG, "App Open Ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }
    
    /**
     * Show App Open Ad if available
     * Call this in MainActivity.onResume() when app returns from background
     */
    fun showAppOpenAdIfAvailable(activity: Activity): Boolean {
        if (!isAppOpenAdAvailable()) return false
        
        appOpenAd?.show(activity)
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                loadAppOpenAd(activity) // Preload next ad
            }
        }
        
        return true
    }
    
    /**
     * Check if App Open Ad is available and not on cooldown
     */
    private fun isAppOpenAdAvailable(): Boolean {
        val currentTime = System.currentTimeMillis()
        return appOpenAd != null && (currentTime - appOpenAdLoadTime >= APP_OPEN_COOLDOWN)
    }
    
    /**
     * Load Interstitial Ad (full-screen ad)
     * Strategic placement: Shows after completing 3 songs or when user navigates between screens
     */
    fun loadInterstitialAd(activity: Activity) {
        if (isInterstitialAdLoading) return
        
        val currentTime = System.currentTimeMillis()
        if (currentTime - interstitialAdLoadTime < INTERSTITIAL_COOLDOWN) {
            Log.d(TAG, "Interstitial Ad on cooldown")
            return
        }
        
        isInterstitialAdLoading = true
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            activity,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    interstitialAdLoadTime = System.currentTimeMillis()
                    isInterstitialAdLoading = false
                    Log.d(TAG, "Interstitial Ad loaded")
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isInterstitialAdLoading = false
                    Log.e(TAG, "Interstitial Ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }
    
    /**
     * Show Interstitial Ad if available
     * Call this at natural breakpoints (e.g., after song completion, screen navigation)
     */
    fun showInterstitialAdIfAvailable(activity: Activity): Boolean {
        if (interstitialAd == null) {
            Log.d(TAG, "Interstitial Ad not loaded")
            return false
        }
        
        val currentTime = System.currentTimeMillis()
        if (currentTime - interstitialAdLoadTime < INTERSTITIAL_COOLDOWN) {
            Log.d(TAG, "Interstitial Ad still on cooldown")
            return false
        }
        
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadInterstitialAd(activity) // Preload next ad
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                loadInterstitialAd(activity)
            }
        }
        
        interstitialAd?.show(activity)
        return true
    }
    
    /**
     * Load Rewarded Ad (user chooses to watch for benefits)
     * Strategic placement: Offer benefits like premium features, ad-free listening, etc.
     */
    fun loadRewardedAd(activity: Activity) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - rewardedAdLoadTime < REWARDED_COOLDOWN) {
            Log.d(TAG, "Rewarded Ad on cooldown")
            return
        }
        
        if (rewardedAd != null) return // Already loaded
        
        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(
            activity,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    rewardedAdLoadTime = System.currentTimeMillis()
                    Log.d(TAG, "Rewarded Ad loaded")
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Rewarded Ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }
    
    /**
     * Show Rewarded Ad with callback
     * Call this when user opts-in to watch ad for benefits
     */
    fun showRewardedAd(activity: Activity, onCompleted: () -> Unit): Boolean {
        if (rewardedAd == null) {
            Log.d(TAG, "Rewarded Ad not loaded")
            return false
        }
        
        onRewardedAdCompleted = onCompleted
        
        rewardedAd?.show(activity) { rewardItem ->
            Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            onRewardedAdCompleted?.invoke()
            rewardedAd = null
            loadRewardedAd(activity) // Preload next ad
        }
        
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadRewardedAd(activity)
            }
        }
        
        return true
    }
    
    /**
     * Load Rewarded Interstitial Ad (shows automatically but rewards user)
     * Strategic placement: Between songs or during natural breaks
     */
    fun loadRewardedInterstitialAd(activity: Activity) {
        val adRequest = AdRequest.Builder().build()
        
        RewardedInterstitialAd.load(
            activity,
            REWARDED_INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    Log.d(TAG, "Rewarded Interstitial Ad loaded")
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Rewarded Interstitial Ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }
    
    /**
     * Show Rewarded Interstitial Ad if available
     */
    fun showRewardedInterstitialAdIfAvailable(activity: Activity, onCompleted: () -> Unit): Boolean {
        if (rewardedInterstitialAd == null) {
            Log.d(TAG, "Rewarded Interstitial Ad not loaded")
            return false
        }
        
        onRewardedAdCompleted = onCompleted
        
        rewardedInterstitialAd?.show(activity) { rewardItem ->
            Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            onRewardedAdCompleted?.invoke()
            rewardedInterstitialAd = null
            loadRewardedInterstitialAd(activity)
        }
        
        rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedInterstitialAd = null
                loadRewardedInterstitialAd(activity)
            }
        }
        
        return true
    }
    
    /**
     * Check if rewarded ad is available
     */
    fun isRewardedAdAvailable(): Boolean = rewardedAd != null
    
    /**
     * Check if rewarded interstitial ad is available
     */
    fun isRewardedInterstitialAdAvailable(): Boolean = rewardedInterstitialAd != null
    
    /**
     * Get Banner Ad Unit ID for use in Compose UI
     */
    fun getBannerAdUnitId(): String = "ca-app-pub-1351624039173419/7803152795"
    
    /**
     * Get Native Ad Unit ID for use in Compose UI
     */
    fun getNativeAdUnitId(): String = "ca-app-pub-1351624039173419/1433185563"
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        appOpenAd?.fullScreenContentCallback = null
        interstitialAd?.fullScreenContentCallback = null
        rewardedAd?.fullScreenContentCallback = null
        rewardedInterstitialAd?.fullScreenContentCallback = null
        
        appOpenAd = null
        interstitialAd = null
        rewardedAd = null
        rewardedInterstitialAd = null
        onRewardedAdCompleted = null
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        cleanup()
    }
}