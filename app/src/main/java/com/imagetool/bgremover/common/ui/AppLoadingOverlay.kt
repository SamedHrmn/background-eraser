package com.imagetool.bgremover.common.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.imagetool.bgremover.R
import com.imagetool.bgremover.theme.Green1

@Composable
fun AppLoadingOverlay(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    content: @Composable () -> Unit
) {
    val loading = remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
           loading.value = isLoading
    }


    val loadingBlurFadeInState by animateFloatAsState(
        if (loading.value) 10f else 0f,
        label = "loadingBlurFadeIn",

    )

    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = modifier.blur(loadingBlurFadeInState.dp)) {
            content()
        }

        AnimatedVisibility(
            visible = loading.value,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        enabled = false,
                        onClick = {}
                    ),
                contentAlignment = Alignment.Center,
            ) {
                AppLottieAnimation(
                    modifier = modifier.fillMaxSize(),
                    resId = R.raw.lottie_loading,
                    animationSpeed = 2f,
                    dynamicProperty = listOf(
                        ///? Horizontal divider color: Layer 5 Outlines
                        ///? Active color: Shape Layer 3
                        ///? Inactive color: Shape Layer 2
                        rememberLottieDynamicProperty(
                            keyPath = arrayOf("Layer 5 Outlines", "**"),
                            property = LottieProperty.COLOR,
                            value = Green1.toArgb(),
                        ),

                        rememberLottieDynamicProperty(
                            keyPath = arrayOf("Shape Layer 3", "**"),
                            property = LottieProperty.COLOR,
                            value = Green1.toArgb(),
                        )
                    )
                )
            }
        }

    }
}