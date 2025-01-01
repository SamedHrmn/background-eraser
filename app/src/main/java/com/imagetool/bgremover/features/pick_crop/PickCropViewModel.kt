package com.imagetool.bgremover.features.pick_crop

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.BitmapCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetool.bgremover.features.erase_by_hand.ui.EraseByHandActivity
import com.imagetool.bgremover.util.ImageUtil
import com.imagetool.bgremover.util.IntentKeys
import com.imagetool.bgremover.util.IntentUtil
import kotlinx.coroutines.MainScope
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


    private fun countTransparentPixels(bitmap: Bitmap) {
        var countTransparent = 0
        var countNonTransparent = 0

        val copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true).apply {
            setHasAlpha(true)
            isPremultiplied = true
        }

        for (x in 0 until copyBitmap.width) {
            for (y in 0 until copyBitmap.height) {
                val pixel = copyBitmap.getPixel(x, y)

                if (pixel == androidx.compose.ui.graphics.Color.Transparent.toArgb()){
                    countTransparent++
                } else {
                    countNonTransparent++
                }
            }
        }

        Log.d("COUNTERS", "$countTransparent---$countNonTransparent")

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun setSelectedPhoto(uri: Uri?, context: Context) {
        MainScope().launch {
            uri?.let {
                pickedImageUri = it

                imageUtil.uriToBitmap(
                    uri = it,
                    context = context,
                    onSuccess = { bitmap ->
                        countTransparentPixels(bitmap)
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
            arg = pickedImageUri.toString(),
            flags = listOf(Intent.FLAG_ACTIVITY_NEW_TASK, Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

}