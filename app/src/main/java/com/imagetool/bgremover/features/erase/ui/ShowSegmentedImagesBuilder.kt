package com.imagetool.bgremover.features.erase.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.imagetool.bgremover.BuildConfig
import com.imagetool.bgremover.features.erase.BackgroundEraserViewModel
import com.imagetool.bgremover.features.subscription.SubscriptionViewModel
import com.imagetool.bgremover.common.ui.ads.AppInterstitialAd
import com.imagetool.bgremover.features.erase.data.BackgroundEraserResultState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShowSegmentedImagesBuilder(
    backgroundEraserViewModel: BackgroundEraserViewModel = koinViewModel(),
    subscriptionViewModel: SubscriptionViewModel = koinViewModel(),
) {
    val segmentResultState = backgroundEraserViewModel.segmentResult.collectAsState()

    when (val result = segmentResultState.value) {
        is BackgroundEraserResultState.NotReady -> {
            return
        }

        is BackgroundEraserResultState.Initial -> {
            return
        }

        is BackgroundEraserResultState.Loading -> {
            return
        }

        is BackgroundEraserResultState.Error -> {
            ErrorDialog(onDismiss = {
                backgroundEraserViewModel.clearResourceOnError()
                backgroundEraserViewModel.initializeEraser()
            })
        }

        is BackgroundEraserResultState.LoadedButNoResult -> {
            NoResultDialog()
        }

        is BackgroundEraserResultState.Loaded -> {


            val showSheetState = remember { mutableStateOf(true) }
            val isSubscribeState = subscriptionViewModel.isSubscribed.collectAsState()
            val showInterstitialAdState = remember {
                mutableStateOf(false)
            }


            if (showSheetState.value) {
                SegmentedImagesBottomSheet(
                    bitmaps = result.bitmaps,
                    onDismiss = {
                        showSheetState.value = false
                    },
                    onCancelClick = {
                        showSheetState.value = false
                    },
                    onSave = {
                        showSheetState.value = false
                        showInterstitialAdState.value = true
                    },
                )
            }

            if (showInterstitialAdState.value && !isSubscribeState.value) {
                AppInterstitialAd(
                    adUnitId = BuildConfig.AD_ID_INTERSTITIAL,
                    onDismissed = {
                        showInterstitialAdState.value = false
                    },
                    onShown = {
                        showInterstitialAdState.value = false
                    },
                )
            }
        }
    }
}
