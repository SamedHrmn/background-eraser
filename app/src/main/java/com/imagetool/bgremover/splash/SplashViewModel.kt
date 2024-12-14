package com.imagetool.bgremover.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}