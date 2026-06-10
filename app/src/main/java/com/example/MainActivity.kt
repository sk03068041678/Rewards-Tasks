package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.ViewModelFactory
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.AppNavigation

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory((application as RewardApp).repository)
    }

    private var mInterstitialAd: InterstitialAd? = null
    private val adUnitId = "ca-app-pub-5650025299710338/3186961764" // Live Interstitial Ad Unit ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(this) {}
        loadInterstitialAd()

        enableEdgeToEdge()
        setContent {
            val appState by viewModel.uiState.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = appState.isDarkMode, dynamicColor = false) {
                AppNavigation(viewModel, ::showAd)
            }
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AdMob", adError.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("AdMob", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            }
        )
    }

    private fun showAd(onComplete: () -> Unit) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdMob", "Ad dismissed fullscreen content.")
                    mInterstitialAd = null
                    loadInterstitialAd() // Load the next ad
                    onComplete()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("AdMob", "Ad failed to show fullscreen content.")
                    mInterstitialAd = null
                    onComplete()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("AdMob", "Ad showed fullscreen content.")
                }
            }
            mInterstitialAd?.show(this)
        } else {
            Log.d("AdMob", "The interstitial ad wasn't ready yet.")
            onComplete()
            loadInterstitialAd() // Try to load an ad next time
        }
    }
}
