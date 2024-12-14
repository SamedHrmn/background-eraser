package com.imagetool.bgremover.features.erase.data

import android.content.Context
import android.graphics.Bitmap
import com.imagetool.bgremover.features.erase.BackgroundEraserHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BackgroundEraserListenerHandler : BackgroundEraserHelper.BackgroundEraserResultListener{
    private val _backgroundEraserResultState = MutableStateFlow<BackgroundEraserResultState>(BackgroundEraserResultState.Initial)
    val backgroundEraserResultState = _backgroundEraserResultState.asStateFlow()

    override fun onInitialized() {
        _backgroundEraserResultState.value = BackgroundEraserResultState.Initial
    }

    override fun onProcessing() {
        _backgroundEraserResultState.value = BackgroundEraserResultState.Loading
    }

    override fun onError(message: String?) {
        _backgroundEraserResultState.value = BackgroundEraserResultState.Error(message = message)
    }

    override fun onResult(bitmaps: List<Bitmap>, context: Context) {
        if (bitmaps.isEmpty()) {
            _backgroundEraserResultState.value = BackgroundEraserResultState.LoadedButNoResult
            return
        }

        _backgroundEraserResultState.value = BackgroundEraserResultState.Loaded(bitmaps = bitmaps)

    }
}