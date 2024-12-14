package com.imagetool.bgremover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.imagetool.bgremover.splash.SplashViewModel

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val subscriptionViewModel: SubscriptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        splashViewModel.initializeApp(
            parallelTasks = listOf(
                { mainActivityViewModel.initializeSegmenter(context = applicationContext) },
                { mainActivityViewModel.configAdManager() },
                { subscriptionViewModel.initBillingClient(context = applicationContext) },
                { mainActivityViewModel.initReview(context = applicationContext) }
            )
        )

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.isSplashShow.value
        }

        enableEdgeToEdge()

        setContent {
            MyApp(
                context = applicationContext,
                mainActivityViewModel = mainActivityViewModel,
                subscriptionViewModel = subscriptionViewModel,
            )
        }
    }
}




