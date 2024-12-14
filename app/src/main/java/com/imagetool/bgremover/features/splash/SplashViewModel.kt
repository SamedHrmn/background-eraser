package com.imagetool.bgremover.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val splashShowFlow = MutableStateFlow(true)
    val isSplashShow = splashShowFlow.asStateFlow()

    fun initializeApp(
        sequentialTasks: List<suspend () -> Unit> = emptyList(),
        parallelTasks: List<suspend () -> Unit> = emptyList()
    ) {
        viewModelScope.launch {

            sequentialTasks.forEach { task ->
                task()
            }

            coroutineScope {
                parallelTasks.map { task ->
                    launch { task() }
                }.joinAll()
            }

            splashShowFlow.value = false
        }
    }

    fun configAdManager() {
        viewModelScope.launch {
            val requestConfiguration = MobileAds.getRequestConfiguration()
                .toBuilder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
        }
    }
}