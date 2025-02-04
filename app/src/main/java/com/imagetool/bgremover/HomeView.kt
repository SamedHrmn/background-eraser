package com.imagetool.bgremover

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.navigation.NavController
import com.imagetool.bgremover.common.ui.AppLoadingOverlay
import com.imagetool.bgremover.common.ui.AppTopBar
import com.imagetool.bgremover.common.ui.OpenDirectoryButton
import com.imagetool.bgremover.common.ui.ads.AppBannerAd
import com.imagetool.bgremover.features.erase.BackgroundEraserViewModel
import com.imagetool.bgremover.features.erase.data.BackgroundEraserResultState
import com.imagetool.bgremover.features.erase.ui.ShowSegmentedImagesBuilder
import com.imagetool.bgremover.features.pick_crop.ui.PickAndCropImageFromGalleryBuilder
import com.imagetool.bgremover.features.pick_crop.ui.RequestStoragePermission
import com.imagetool.bgremover.features.rate_us.RateUsViewModel
import com.imagetool.bgremover.features.subscription.SubscriptionViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun HomeView(
    subscriptionViewModel: SubscriptionViewModel = koinViewModel(),
    backgroundEraserViewModel: BackgroundEraserViewModel = koinViewModel(),
    rateUsViewModel: RateUsViewModel = koinViewModel(),
) {


    val segmentResultState = backgroundEraserViewModel.segmentResult.collectAsState()
    val showReviewState = rateUsViewModel.showReviewState.collectAsState()
    val isSubscribeState = subscriptionViewModel.isSubscribed.collectAsState()

    val scrollState = rememberScrollState()
    val localContext = LocalContext.current

    LaunchedEffect(showReviewState.value) {
        rateUsViewModel.launchReview(
            activity = localContext as Activity,
            context = localContext
        )
    }


    AppLoadingOverlay(isLoading = (segmentResultState.value == BackgroundEraserResultState.Loading)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),

            topBar = {
                AppTopBar()
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
                            0.6f
                        )
                    )
                ) {
                    PickAndCropImageFromGalleryBuilder()
                }

                RequestStoragePermission {
                    ShowSegmentedImagesBuilder()
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