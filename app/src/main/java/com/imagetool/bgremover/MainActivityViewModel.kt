package com.imagetool.bgremover

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.imagetool.bgremover.remover.ImageSegmenterHelper
import com.imagetool.bgremover.util.DataStoreHelper
import com.imagetool.bgremover.util.ImageUtil
import com.imagetool.bgremover.util.ReviewUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel(), ImageSegmenterHelper.ImageSegmenterListener {
    private val _selectedPhotoState = MutableStateFlow<Bitmap?>(null)

    private val _showReviewState = MutableStateFlow(false)
    val showReviewState = _showReviewState.asStateFlow()


    @Volatile
    private var imageSegmenterHelper: ImageSegmenterHelper? = null

    val selectedPhotoState = _selectedPhotoState.asStateFlow()
    private val segmentResultState =
        MutableStateFlow<SegmentResultState>(SegmentResultState.NotReady)
    val segmentResult = segmentResultState.asStateFlow()

    fun initializeSegmenter(context: Context) {
        viewModelScope.launch {
            imageSegmenterHelper =
                ImageSegmenterHelper(this@MainActivityViewModel, context = context)
            imageSegmenterHelper!!.initializeSegmenter()
        }
    }

    fun segmentImage() {
        if (selectedPhotoState.value == null || imageSegmenterHelper == null) return

        viewModelScope.launch {
            imageSegmenterHelper!!.segmentImage(selectedPhotoState.value!!)
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
                        _selectedPhotoState.value = bitmap
                    },
                )
            }
        }
    }

    fun initReview(context: Context) {
        ReviewUtil.initReview(context)
    }

    fun launchReview(context: Context,activity: Activity) {
        viewModelScope.launch {
            DataStoreHelper.getInt(
                DataStoreHelper.USER_IMAGE_COUNT_SHARED_KEY,
                0,
                context = context
            ).collect {
                if (it == 2 || it == 40 || it == 100) {
                    ReviewUtil.launchReview(activity)
                }
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
        _selectedPhotoState.value = null
        segmentResultState.value = SegmentResultState.NotReady
        if (imageSegmenterHelper != null) {
            imageSegmenterHelper!!.close()
            imageSegmenterHelper = null
        }
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


    fun clearLastSegmentation() {
        _selectedPhotoState.value = null
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

    override fun onResult(bitmaps: List<Bitmap>, context: Context) {
        if (bitmaps.isEmpty()) {
            segmentResultState.value = SegmentResultState.LoadedButNoResult
            return
        }

        segmentResultState.value = SegmentResultState.Loaded(bitmaps = bitmaps)
        saveUserImageCount(context = context)
    }

    fun configAdManager() {
        viewModelScope.launch {
            val requestConfiguration = MobileAds.getRequestConfiguration()
                .toBuilder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
        }
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