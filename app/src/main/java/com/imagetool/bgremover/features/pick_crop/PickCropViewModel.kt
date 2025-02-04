package com.imagetool.bgremover.features.pick_crop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.imagetool.bgremover.common.navigation.AppScreens
import com.imagetool.bgremover.util.ImageUtil
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

    @RequiresApi(Build.VERSION_CODES.P)
    fun setSelectedPhoto(uri: Uri?, context: Context) {
        MainScope().launch {
            uri?.let {
                pickedImageUri = it

                imageUtil.uriToBitmap(
                    uri = it,
                    context = context,
                    onSuccess = { bitmap ->
                        updatePickedImageState(bitmap)
                    },
                )
            }
        }
    }

    fun navigateEraseByHand(navController: NavController) {
        if (pickedImageUri == null) return
        navController.navigate(AppScreens.EraseByHandView.createRoute(pickedImageUri = pickedImageUri!!.toString()))
    }

}