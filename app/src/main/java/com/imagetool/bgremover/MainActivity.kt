package com.imagetool.bgremover


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import coil3.compose.SubcomposeAsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.imagetool.bgremover.splash.SplashViewModel
import com.imagetool.bgremover.ui.composables.AppElevatedButton
import com.imagetool.bgremover.ui.composables.AppLoadingOverlay
import com.imagetool.bgremover.ui.composables.AppTopBar
import com.imagetool.bgremover.ui.composables.OpenDirectoryButton
import com.imagetool.bgremover.ui.composables.PickAndCropImageGallery
import com.imagetool.bgremover.ui.composables.SegmentedImagesBottomSheet
import com.imagetool.bgremover.ui.composables.TapToSelectBox
import com.imagetool.bgremover.ui.composables.ads.AppBannerAd
import com.imagetool.bgremover.ui.composables.ads.AppInterstitialAd
import com.imagetool.bgremover.ui.composables.dialogs.ErrorDialog
import com.imagetool.bgremover.ui.composables.dialogs.NoResultDialog
import com.imagetool.bgremover.ui.composables.dialogs.PermissionRationaleDialog
import com.imagetool.bgremover.ui.theme.BlackText
import com.imagetool.bgremover.ui.theme.Green1
import com.imagetool.bgremover.ui.theme.ImagetoolbackgroundremoverTheme
import com.imagetool.bgremover.util.AppLocaleProvider
import com.imagetool.bgremover.util.LocalResources
import com.imagetool.bgremover.util.openAppSettings

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val splashScreen = installSplashScreen()
        splashViewModel.initializeApp {
            mainActivityViewModel.initializeSegmenter()
        }

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.isSplashShow.value
        }



        setContent {
            AppLocaleProvider(context = applicationContext) {
                ImagetoolbackgroundremoverTheme {

                    val segmentResultState = mainActivityViewModel.segmentResult.collectAsState()
                    val scrollState = rememberScrollState()


                    AppLoadingOverlay(isLoading = (segmentResultState.value == SegmentResultState.Loading)) {
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
                                        mainActivityViewModel = mainActivityViewModel
                                    )
                                }
                                OpenDirectoryButton(
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(vertical = 16.dp),
                                )
                                Spacer(Modifier.weight(1f))
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
}

@Composable
fun PickAndCropImageFromGalleryBuilder(
    mainActivityViewModel: MainActivityViewModel
) {
    val context = LocalContext.current
    val selectedImageState = mainActivityViewModel.selectedPhoto.collectAsState()

    when (val bitmap = selectedImageState.value) {
        null -> {
            PickAndCropImageGallery(
                cropActivityToolbarColor = Green1.toArgb(),
                cropMenuCropButtonTitle = R.string.crop_activity_title,
                onCropSuccess = { uri ->
                    mainActivityViewModel.setSelectedPhoto(uri = uri, context = context)
                }
            ) { onPickImage ->
                TapToSelectBox(
                    onTap = {
                        onPickImage()
                    },
                )
            }
        }

        else -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PickAndCropImageGallery(
                    cropActivityToolbarColor = Green1.toArgb(),
                    cropMenuCropButtonTitle = R.string.crop_activity_title,
                    onCropSuccess = { uri ->
                        mainActivityViewModel.setSelectedPhoto(uri = uri, context = context)
                    }) { onPickImage ->

                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable {
                                onPickImage()
                            },

                        model = bitmap,
                        contentDescription = "",
                        alignment = Alignment.TopCenter,
                        contentScale = ContentScale.Crop,
                    )

                }
                Spacer(Modifier.height(16.dp))
                AppElevatedButton(
                    onClick = {
                        mainActivityViewModel.segmentImage()
                    }) {
                    Text(
                        text = LocalResources.current.getString(R.string.analyze_image_button_text),
                        color = BlackText,
                    )
                }
            }
        }
    }
}

@Composable
fun ShowSegmentedImagesBuilder(

    mainActivityViewModel: MainActivityViewModel
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
                mainActivityViewModel.initializeSegmenter()
            })
        }

        is SegmentResultState.LoadedButNoResult -> {
            NoResultDialog()
        }

        is SegmentResultState.Loaded -> {


            val showSheetState = remember { mutableStateOf(true) }
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

            if (showInterstitialAdState.value) {
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestStoragePermission(content: @Composable (onPermissionRequest: () -> Unit) -> Unit) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    } else {
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)
    val permissionRequestedOnceState = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current



    if (permissionState.status.isGranted) {
        content {
            //Permission granted already
        }
    } else if (permissionState.status.shouldShowRationale) {
        PermissionRationaleDialog {
            permissionState.launchPermissionRequest()
            permissionRequestedOnceState.value = true
        }
    } else if (permissionRequestedOnceState.value) {
        PermissionRationaleDialog {
            context.openAppSettings()
            permissionRequestedOnceState.value = false
        }
    } else {
        content {
            permissionState.launchPermissionRequest()
            permissionRequestedOnceState.value = true
        }
    }
}

