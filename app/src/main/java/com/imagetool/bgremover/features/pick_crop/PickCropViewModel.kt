package com.imagetool.bgremover.features.pick_crop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetool.bgremover.features.erase_by_hand.ui.EraseByHandActivity
import com.imagetool.bgremover.util.ImageUtil
import com.imagetool.bgremover.util.IntentKeys
import com.imagetool.bgremover.util.IntentUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PickCropViewModel(private val imageUtil: ImageUtil) : ViewModel() {
    private val _pickedImageState = MutableStateFlow<Bitmap?>(null)
    val pickedImage = _pickedImageState.asStateFlow()
    private var pickedImageUri: Uri? = null

    fun updatePickedImageState(bitmap: Bitmap?) {
        _pickedImageState.value = bitmap
    }

    fun setSelectedPhoto(uri: Uri?, context: Context) {
        viewModelScope.launch {
            uri?.let {
                pickedImageUri = it
                imageUtil.uriToBitmap(
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

    fun navigateEraseByHand(context: Context) {
        if (pickedImageUri == null) return

        IntentUtil.intentWithArgs(
            context = context,
            dest = EraseByHandActivity::class.java,
            argKey = IntentKeys.PickedImageUri,
            arg = pickedImageUri.toString()
        )
    }

}