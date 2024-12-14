package com.imagetool.bgremover.common.ui.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

sealed class InterstitialAdState {
    data object Loading : InterstitialAdState()
    data class Loaded(var interstitialAd: InterstitialAd?) : InterstitialAdState()
    data class Error(val message: String) : InterstitialAdState()
}

@Composable
fun AppInterstitialAd(adUnitId: String, onShown: () -> Unit, onDismissed: () -> Unit) {
    val context = LocalContext.current
    val adState = remember { mutableStateOf<InterstitialAdState>(InterstitialAdState.Loading) }


    DisposableEffect(adUnitId) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                super.onAdLoaded(ad)
                adState.value = InterstitialAdState.Loaded(ad)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                adState.value = InterstitialAdState.Error(error.message)
            }
        })

        onDispose {
            if (adState.value is InterstitialAdState.Loaded) {
                (adState.value as InterstitialAdState.Loaded).interstitialAd = null
            }
        }
    }

    LaunchedEffect(adState.value) {
        if (adState.value is InterstitialAdState.Loaded) {
            val loadedAd = (adState.value as InterstitialAdState.Loaded).interstitialAd
            loadedAd?.let {
                it.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(error: AdError) {
                        super.onAdFailedToShowFullScreenContent(error)
                        adState.value = InterstitialAdState.Error(error.message)
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        onShown()
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        onDismissed()
                    }
                }

                it.show(context as? android.app.Activity ?: return@LaunchedEffect)
            }
        }
    }
}