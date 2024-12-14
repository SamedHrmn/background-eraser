package com.imagetool.bgremover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.imagetool.bgremover.features.erase.BackgroundEraserViewModel
import com.imagetool.bgremover.features.pick_crop.PickCropViewModel
import com.imagetool.bgremover.features.splash.SplashViewModel
import com.imagetool.bgremover.features.subscription.SubscriptionViewModel
import com.imagetool.bgremover.features.erase.BackgroundEraserHelper
import com.imagetool.bgremover.features.feedback.FeedbackViewModel
import com.imagetool.bgremover.features.rate_us.RateUsViewModel
import com.imagetool.bgremover.features.share_us.ShareUsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModule)
        }

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
            KoinAndroidContext {
                MyApp()
            }
        }
    }
}

val appModule = module {
    single { PickCropViewModel() }
    single<BackgroundEraserHelper> { BackgroundEraserHelper(androidContext()) }
    viewModel { BackgroundEraserViewModel(get<PickCropViewModel>(), get<BackgroundEraserHelper>()) }
    viewModel { SplashViewModel() }
    viewModel { SubscriptionViewModel() }
    viewModel { FeedbackViewModel() }
    viewModel { RateUsViewModel() }
    viewModel { ShareUsViewModel() }
}



