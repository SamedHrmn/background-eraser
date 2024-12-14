package com.imagetool.bgremover.ui.composables

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.imagetool.bgremover.BuildConfig
import com.imagetool.bgremover.MainActivityViewModel
import com.imagetool.bgremover.SegmentResultState
import com.imagetool.bgremover.SubscriptionViewModel
import com.imagetool.bgremover.ui.composables.ads.AppInterstitialAd
import com.imagetool.bgremover.ui.composables.dialogs.ErrorDialog
import com.imagetool.bgremover.ui.composables.dialogs.NoResultDialog

@Composable
fun ShowSegmentedImagesBuilder(
    mainActivityViewModel: MainActivityViewModel,
    subscriptionViewModel: SubscriptionViewModel,
    context: Context,
) {
    val segmentResultState = mainActivityViewModel.segmentResult.collectAsState()

    when (val result = segmentResultState.value) {
        is SegmentResultState.NotReady -> {
            return
        }

        is SegmentResultState.Initial -> {
            return
        }

        is SegmentResultState.Loading -> {
            return
        }

        is SegmentResultState.Error -> {
            ErrorDialog(onDismiss = {
                mainActivityViewModel.clearResourceOnError()
                mainActivityViewModel.initializeSegmenter(context = context)
            })
        }

        is SegmentResultState.LoadedButNoResult -> {
            NoResultDialog()
        }

        is SegmentResultState.Loaded -> {


            val showSheetState = remember { mutableStateOf(true) }
            val isSubscribeState = subscriptionViewModel.isSubscribed.collectAsState()
            val showInterstitialAdState = remember {
                mutableStateOf(false)
            }


            if (showSheetState.value) {
                SegmentedImagesBottomSheet(
                    bitmaps = result.bitmaps,
                    mainActivityViewModel = mainActivityViewModel,
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
