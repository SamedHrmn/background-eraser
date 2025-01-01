package com.imagetool.bgremover.features.erase

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetool.bgremover.common.use_cases.SaveImageUseCase
import com.imagetool.bgremover.features.erase.data.BackgroundEraserResultState
import com.imagetool.bgremover.features.pick_crop.PickCropViewModel
import com.imagetool.bgremover.util.DataStoreHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackgroundEraserViewModel(
    private val pickCropViewModel: PickCropViewModel,
    private val backgroundEraserHelper: BackgroundEraserHelper,
    private val saveImageUseCase: SaveImageUseCase,
) : ViewModel(),
    BackgroundEraserHelper.BackgroundEraserResultListener {

    init {
        backgroundEraserHelper.setListener(this)
    }

    private val _backgroundEraserResultState =
        MutableStateFlow<BackgroundEraserResultState>(BackgroundEraserResultState.NotReady)
    val segmentResult = _backgroundEraserResultState.asStateFlow()

    fun eraseBackground() {
        if (pickCropViewModel.pickedImage.value == null) return
        viewModelScope.launch {
            backgroundEraserHelper.segmentImage(pickCropViewModel.pickedImage.value!!)
        }
    }

    fun initializeEraser() {
        viewModelScope.launch {
            backgroundEraserHelper.initializeSegmenter()
        }
    }

    fun clearResourceOnError() {
        pickCropViewModel.updatePickedImageState(null)
        _backgroundEraserResultState.value = BackgroundEraserResultState.NotReady
        backgroundEraserHelper.close()
    }

    fun clearLastSegmentation() {
        pickCropViewModel.updatePickedImageState(null)
        _backgroundEraserResultState.value = BackgroundEraserResultState.Initial
    }

    private fun saveUserImageCount(context: Context, overrideValue: Int? = null) {
        viewModelScope.launch {
            DataStoreHelper.getInt(DataStoreHelper.USER_IMAGE_COUNT_SHARED_KEY, 0, context)
                .collect { data ->
                    DataStoreHelper.saveInt(
                        DataStoreHelper.USER_IMAGE_COUNT_SHARED_KEY,
                        overrideValue ?: (data + 1),
                        context
                    )
                }
        }
    }

    suspend fun saveSelectedImages(
        context: Context,
        localResources: Resources,
        bitmaps: List<Bitmap>
    ): Boolean {
        return saveImageUseCase.execute(
            context = context,
            localResources = localResources,
            bitmaps = bitmaps
        )
    }

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
        saveUserImageCount(context = context)
    }
}