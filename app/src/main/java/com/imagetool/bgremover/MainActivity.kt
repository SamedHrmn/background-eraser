package com.imagetool.bgremover

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.imagetool.bgremover.common.use_cases.SaveImageUseCase
import com.imagetool.bgremover.features.erase.BackgroundEraserViewModel
import com.imagetool.bgremover.features.pick_crop.PickCropViewModel
import com.imagetool.bgremover.features.splash.SplashViewModel
import com.imagetool.bgremover.features.subscription.SubscriptionViewModel
import com.imagetool.bgremover.features.erase.BackgroundEraserHelper
import com.imagetool.bgremover.features.erase_by_hand.EraseByHandViewModel
import com.imagetool.bgremover.features.feedback.FeedbackViewModel
import com.imagetool.bgremover.features.rate_us.RateUsViewModel
import com.imagetool.bgremover.features.share_us.ShareUsViewModel
import com.imagetool.bgremover.util.ImageUtil
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val splashViewModel = getViewModel<SplashViewModel>()
        val backgroundEraserViewModel = getViewModel<BackgroundEraserViewModel>()
        val subscriptionViewModel = getViewModel<SubscriptionViewModel>()
        val rateUsViewModel = getViewModel<RateUsViewModel>()


        val splashScreen = installSplashScreen()
        splashViewModel.initializeApp(
            parallelTasks = listOf(
                { backgroundEraserViewModel.initializeEraser() },
                { splashViewModel.configAdManager() },
                { subscriptionViewModel.initBillingClient(context = applicationContext) },
                { rateUsViewModel.initReview(context = applicationContext) }
            )
        )

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.isSplashShow.value
        }

        enableEdgeToEdge()

        setContent {
            KoinAndroidContext{
                MyApp()
            }
        }
    }
}

class MyApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}

val appModule = module {
    single { ImageUtil() }
    single { PickCropViewModel(imageUtil = get<ImageUtil>()) }
    single<BackgroundEraserHelper> { BackgroundEraserHelper(androidContext()) }
    factory { SaveImageUseCase(imageUtils = get<ImageUtil>()) }
    viewModel {
        BackgroundEraserViewModel(
            get<PickCropViewModel>(),
            get<BackgroundEraserHelper>(),
            get<SaveImageUseCase>()
        )
    }
    viewModel { SplashViewModel() }
    viewModel { SubscriptionViewModel() }
    viewModel { FeedbackViewModel() }
    viewModel { RateUsViewModel() }
    viewModel { ShareUsViewModel() }
    viewModel {
        EraseByHandViewModel(
            imageUtil = get<ImageUtil>(),
            saveImageUseCase = get<SaveImageUseCase>()
        )
    }
}



