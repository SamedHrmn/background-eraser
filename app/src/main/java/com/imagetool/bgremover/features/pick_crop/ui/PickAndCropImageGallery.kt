package com.imagetool.bgremover.features.pick_crop.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.imagetool.bgremover.common.provider.LocalResources

@Composable
fun PickAndCropImageGallery(
    cropActivityToolbarColor: Int?,
    @StringRes cropMenuCropButtonTitle: Int,
    onCropSuccess: (uri: android.net.Uri) -> Unit,
    content: @Composable (onPickImage: () -> Unit) -> Unit
) {

    val localResources = LocalResources.current

    val imageCropLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful && result.uriContent != null) {
            onCropSuccess(result.uriContent!!)
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val cropOption = CropImageContractOptions(
            uri = uri, cropImageOptions = CropImageOptions(
                cropMenuCropButtonTitle = localResources.getText(cropMenuCropButtonTitle),
                toolbarColor = cropActivityToolbarColor,
                backgroundColor = Color.Transparent.toArgb(),
                outputCompressFormat = Bitmap.CompressFormat.PNG
            )
        )
        imageCropLauncher.launch(cropOption)
    }

    content {
        pickImageLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}