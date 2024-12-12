package com.imagetool.bgremover.ui.composables

import android.annotation.SuppressLint
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

@SuppressLint("RestrictedApi")
@Composable
fun AppLottieAnimation(
    modifier: Modifier = Modifier,
    @RawRes resId: Int,
    color: Color? = null,
    animationSpeed: Float = 1f,
    dynamicProperty: List<LottieDynamicProperty<*>>? = null,
) {
    val lottieCompositionState = rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId = resId)
    )


    val lottieAnimationState = animateLottieCompositionAsState(
        composition = lottieCompositionState.value,
        iterations = LottieConstants.IterateForever,
        speed = animationSpeed,
    )



    var dynamicProperties:LottieDynamicProperties? = null

    if(dynamicProperty != null){
        dynamicProperties = rememberLottieDynamicProperties(
            *dynamicProperty.toTypedArray()
        )
    }else if(color != null){
      dynamicProperties =  rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    color.hashCode(),
                    BlendModeCompat.SRC_ATOP
                ),
                keyPath = arrayOf(
                    "**"
                )
            )
        )
    }



    LottieAnimation(
        modifier = modifier,
        composition = lottieCompositionState.value,
        dynamicProperties = dynamicProperties,
        safeMode = true,
        progress = {
            lottieAnimationState.progress
        }
    )
}