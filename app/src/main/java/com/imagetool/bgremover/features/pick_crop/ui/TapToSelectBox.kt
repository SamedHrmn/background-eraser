package com.imagetool.bgremover.features.pick_crop.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.ui.AppLottieAnimation
import com.imagetool.bgremover.theme.Green1

@Composable
fun TapToSelectBox(modifier: Modifier = Modifier, onTap: () -> Unit) {
    Box(modifier = modifier
        .clickable {
            onTap()
        }
        .drawBehind {
            val stroke = Stroke(
                width = 8f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 10f), 0f)
            )
            drawRoundRect(color = Green1, style = stroke)
        }
    ) {
        AppLottieAnimation(
            modifier = modifier.fillMaxSize(),
            resId = R.raw.lottie_tap,
            color = Green1
        )
    }
}