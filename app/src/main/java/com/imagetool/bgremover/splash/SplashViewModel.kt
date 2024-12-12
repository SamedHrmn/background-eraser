package com.imagetool.bgremover.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val splashShowFlow = MutableStateFlow(true)
    val isSplashShow = splashShowFlow.asStateFlow()

    fun initializeApp(onInit: suspend()-> Unit){
        viewModelScope.launch {
            onInit()
            splashShowFlow.value = false
        }
    }
}