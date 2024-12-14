package com.imagetool.bgremover.features.pick_crop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetool.bgremover.util.ImageUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PickCropViewModel:ViewModel() {
    private val _pickedImageState = MutableStateFlow<Bitmap?>(null)
    val pickedImage = _pickedImageState.asStateFlow()

    fun updatePickedImageState(bitmap: Bitmap?){
        _pickedImageState.value = bitmap
    }

    fun setSelectedPhoto(uri: Uri?, context: Context) {
        viewModelScope.launch {
            uri?.let {
                ImageUtil.uriToBitmap(
                    uri = it,
                    context = context,
                    scope = viewModelScope,
                    onSuccess = { bitmap ->
                        updatePickedImageState(bitmap)
                    },
                )
            }
        }
    }
}