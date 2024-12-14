package com.imagetool.bgremover.features.erase.data

import android.graphics.Bitmap

sealed interface BackgroundEraserResultState {
    data object NotReady : BackgroundEraserResultState
    data object Initial : BackgroundEraserResultState
    data object Loading : BackgroundEraserResultState
    data class Loaded(var bitmaps: List<Bitmap>) : BackgroundEraserResultState
    data object LoadedButNoResult : BackgroundEraserResultState
    data class Error(var message: String?) : BackgroundEraserResultState
}