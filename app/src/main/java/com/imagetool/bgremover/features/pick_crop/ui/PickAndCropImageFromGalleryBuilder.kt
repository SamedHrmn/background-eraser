package com.imagetool.bgremover.features.pick_crop.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.features.erase.BackgroundEraserViewModel
import com.imagetool.bgremover.features.pick_crop.PickCropViewModel
import com.imagetool.bgremover.theme.Green1
import com.imagetool.bgremover.theme.Typography
import org.koin.androidx.compose.koinViewModel

@Composable
fun PickAndCropImageFromGalleryBuilder(
    pickCropViewModel: PickCropViewModel = koinViewModel(),
    backgroundEraserViewModel: BackgroundEraserViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val selectedImageState = pickCropViewModel.pickedImage.collectAsState()
    val localResource = LocalResources.current

    when (val bitmap = selectedImageState.value) {
        null -> {
            PickAndCropImageGallery(
                cropActivityToolbarColor = Green1.toArgb(),
                cropMenuCropButtonTitle = R.string.crop_activity_title,
                onCropSuccess = { uri ->
                    pickCropViewModel.setSelectedPhoto(uri = uri, context = context)
                }
            ) { onPickImage ->
                TapToSelectBox(
                    onTap = {
                        onPickImage()
                    },
                )
            }
        }

        else -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PickAndCropImageGallery(
                    cropActivityToolbarColor = Green1.toArgb(),
                    cropMenuCropButtonTitle = R.string.crop_activity_title,
                    onCropSuccess = { uri ->
                        pickCropViewModel.setSelectedPhoto(uri = uri, context = context)
                    }) { onPickImage ->

                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable {
                                onPickImage()
                            },

                        model = bitmap,
                        contentDescription = "",
                        alignment = Alignment.TopCenter,
                        contentScale = ContentScale.Crop,
                    )

                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AppElevatedButton(
                        modifier = Modifier.defaultMinSize(minWidth = 100.dp),
                        onClick = {
                            backgroundEraserViewModel.eraseBackground()
                        }) {
                        AppText(
                            text = localResource.getString(R.string.magic_erase_button_text),
                        )
                    }
                    AppElevatedButton(
                        modifier = Modifier.defaultMinSize(minWidth = 100.dp),
                        onClick = {
                            pickCropViewModel.navigateEraseByHand(context = context)
                        }) {
                        AppText(
                            text = localResource.getString(R.string.erasebyhand_button_text),
                        )
                    }
                }
            }
        }
    }
}