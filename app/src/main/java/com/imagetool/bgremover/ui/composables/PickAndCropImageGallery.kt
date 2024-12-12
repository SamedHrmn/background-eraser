package com.imagetool.bgremover.ui.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.imagetool.bgremover.util.LocalResources

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
            )
        )
        imageCropLauncher.launch(cropOption)
    }

    content {
        pickImageLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}