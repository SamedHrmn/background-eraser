package com.imagetool.bgremover

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.preload.PreloadCallback
import com.google.android.gms.ads.preload.PreloadConfiguration
import com.imagetool.bgremover.remover.ImageSegmenterHelper
import com.imagetool.bgremover.util.ImageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel(), ImageSegmenterHelper.ImageSegmenterListener {
    private val selectedPhotoState = MutableStateFlow<Bitmap?>(null)


    @Volatile
    private var imageSegmenterHelper: ImageSegmenterHelper? = null

    val selectedPhoto = selectedPhotoState.asStateFlow()
    private val segmentResultState =
        MutableStateFlow<SegmentResultState>(SegmentResultState.NotReady)
    val segmentResult = segmentResultState.asStateFlow()

    fun initializeSegmenter() {
        viewModelScope.launch {
            imageSegmenterHelper = ImageSegmenterHelper(this@MainActivityViewModel)
            imageSegmenterHelper!!.initializeSegmenter()
        }
    }

    fun segmentImage() {
        if (selectedPhoto.value == null || imageSegmenterHelper == null) return

        viewModelScope.launch {
            imageSegmenterHelper!!.segmentImage(selectedPhoto.value!!)
        }

    }


    fun setSelectedPhoto(uri: Uri?, context: Context) {
        viewModelScope.launch {
            uri?.let {
                ImageUtil.uriToBitmap(
                    uri = it,
                    context = context,
                    scope = viewModelScope,
                    onSuccess = { bitmap ->
                        selectedPhotoState.value = bitmap
                    },
                )
            }
        }
    }

    fun saveSelectedImages(
        context: Context,
        localResources: Resources,
        bitmaps: List<Bitmap>
    ): Boolean {
        if (bitmaps.isEmpty()) return false

        try {
            viewModelScope.launch(Dispatchers.IO) {
                ImageUtil.saveBitmapsAsPngToGallery(
                    context = context,
                    localResources = localResources,
                    bitmaps = bitmaps
                )
            }
            return true
        } catch (e: Exception) {
            return false
        }

    }

    fun clearResourceOnError() {
        selectedPhotoState.value = null
        segmentResultState.value = SegmentResultState.NotReady
        if (imageSegmenterHelper != null) {
            imageSegmenterHelper!!.close()
            imageSegmenterHelper = null
        }
    }





    fun clearLastSegmentation(){
        selectedPhotoState.value = null
        segmentResultState.value = SegmentResultState.Initial
    }

    override fun onInitialized() {
        segmentResultState.value = SegmentResultState.Initial
    }

    override fun onProcessing() {
        segmentResultState.value = SegmentResultState.Loading
    }

    override fun onError(message: String?) {
        segmentResultState.value = SegmentResultState.Error(message = message)
    }

    override fun onResult(bitmaps: List<Bitmap>) {
        if (bitmaps.isEmpty()) {
            segmentResultState.value = SegmentResultState.LoadedButNoResult
            return
        }

        segmentResultState.value = SegmentResultState.Loaded(bitmaps = bitmaps)
    }
}

sealed interface SegmentResultState {
    data object NotReady : SegmentResultState
    data object Initial : SegmentResultState
    data object Loading : SegmentResultState
    data class Loaded(var bitmaps: List<Bitmap>) : SegmentResultState
    data object LoadedButNoResult : SegmentResultState
    data class Error(var message: String?) : SegmentResultState
}