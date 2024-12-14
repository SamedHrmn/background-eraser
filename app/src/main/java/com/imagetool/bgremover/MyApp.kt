package com.imagetool.bgremover

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.imagetool.bgremover.ui.composables.AppLoadingOverlay
import com.imagetool.bgremover.ui.composables.AppTopBar
import com.imagetool.bgremover.ui.composables.OpenDirectoryButton
import com.imagetool.bgremover.ui.composables.PickAndCropImageFromGalleryBuilder
import com.imagetool.bgremover.ui.composables.RequestStoragePermission
import com.imagetool.bgremover.ui.composables.ShowSegmentedImagesBuilder
import com.imagetool.bgremover.ui.composables.ads.AppBannerAd
import com.imagetool.bgremover.ui.theme.ImagetoolbackgroundremoverTheme
import com.imagetool.bgremover.util.AppLocaleProvider

@Composable
fun MyApp(
    context: Context,
    mainActivityViewModel: MainActivityViewModel,
    subscriptionViewModel: SubscriptionViewModel,
) {
    AppLocaleProvider(context = context) {
        ImagetoolbackgroundremoverTheme {

            val segmentResultState = mainActivityViewModel.segmentResult.collectAsState()
            val scrollState = rememberScrollState()

            val showReviewState = mainActivityViewModel.showReviewState.collectAsState()
            val localCtx = LocalContext.current

            val isSubscribeState = subscriptionViewModel.isSubscribed.collectAsState()

            LaunchedEffect(showReviewState.value) {
                mainActivityViewModel.launchReview(
                    activity = localCtx as Activity,
                    context = context
                )
            }


            AppLoadingOverlay(isLoading = (segmentResultState.value == SegmentResultState.Loading)) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    topBar = {
                        AppTopBar(subscriptionViewModel = subscriptionViewModel)
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                            .verticalScroll(scrollState)

                    ) {
                        Box(
                            modifier = Modifier.height(
                                LocalConfiguration.current.screenHeightDp.dp.times(
                                    0.5f
                                )
                            )
                        ) {
                            PickAndCropImageFromGalleryBuilder(
                                mainActivityViewModel = mainActivityViewModel
                            )
                        }

                        RequestStoragePermission {
                            ShowSegmentedImagesBuilder(
                                mainActivityViewModel = mainActivityViewModel,
                                subscriptionViewModel = subscriptionViewModel,
                                context = context,
                            )
                        }
                        OpenDirectoryButton(
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(vertical = 16.dp),
                        )
                        Spacer(Modifier.weight(1f))
                        if (!isSubscribeState.value) {
                            AppBannerAd(
                                adUnitId = BuildConfig.AD_ID_BANNER
                            )
                        }
                    }
                }
            }
        }
    }
}