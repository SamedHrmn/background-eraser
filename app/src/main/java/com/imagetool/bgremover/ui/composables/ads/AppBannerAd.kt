package com.imagetool.bgremover.ui.composables.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AppBannerAd(modifier: Modifier= Modifier,adUnitId: String) {

    val adState = remember { mutableStateOf<BannerAdState>(BannerAdState.Loading) }

    LaunchedEffect(adUnitId) {
        adState.value = BannerAdState.Loading
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                setAdUnitId(adUnitId)
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        adState.value = BannerAdState.Loaded
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        adState.value = BannerAdState.Error(p0.message)
                    }
                }
                loadAd(AdRequest.Builder().build())
            }

        }
    )

    when (adState.value) {
        is BannerAdState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is BannerAdState.Loaded -> {
            Box(modifier = Modifier.fillMaxSize()) {
                // The banner ad is automatically displayed by AndroidView
            }
        }

        is BannerAdState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Failed to load ad")
            }
        }
    }
}

  sealed class BannerAdState {
    data object Loading : BannerAdState()
    data object Loaded : BannerAdState()
    data class Error(val message: String) : BannerAdState()
}