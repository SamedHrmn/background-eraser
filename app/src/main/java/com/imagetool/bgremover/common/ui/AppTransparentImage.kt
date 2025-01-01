package com.imagetool.bgremover.common.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun AppTransparentImage(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        null,
        modifier = modifier,
        alignment = Alignment.TopCenter,
        contentScale = contentScale,
    )
}