package com.imagetool.bgremover.common.ui.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
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
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources

@Composable
fun AppBannerAd(modifier: Modifier = Modifier, adUnitId: String, maxRetryCountOnError: Int = 3) {

    val adState = remember { mutableStateOf<BannerAdState>(BannerAdState.Loading) }
    val retryCounter = remember { mutableIntStateOf(0) }
    val localResource = LocalResources.current

    LaunchedEffect(adUnitId, retryCounter.intValue) {
        if (retryCounter.intValue <= maxRetryCountOnError) {
            adState.value = BannerAdState.Loading
        }
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
        },
    )

    when (adState.value) {
        is BannerAdState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is BannerAdState.Loaded -> {
            Box(modifier = Modifier.fillMaxSize()) {

            }
        }

        is BannerAdState.Error -> {
            if (retryCounter.intValue < maxRetryCountOnError) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000L)
                    retryCounter.intValue++
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = localResource.getString(R.string.failed_load_ad_text))
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = localResource.getString(R.string.unable_load_ad_text),
                    )
                }
            }
        }
    }
}

sealed class BannerAdState {
    data object Loading : BannerAdState()
    data object Loaded : BannerAdState()
    data class Error(val message: String) : BannerAdState()
}