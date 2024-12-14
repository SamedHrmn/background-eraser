package com.imagetool.bgremover.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.imagetool.bgremover.MainActivityViewModel
import com.imagetool.bgremover.R
import com.imagetool.bgremover.ui.theme.BlackText
import com.imagetool.bgremover.ui.theme.Green1
import com.imagetool.bgremover.util.LocalResources

@Composable
fun PickAndCropImageFromGalleryBuilder(
    mainActivityViewModel: MainActivityViewModel
) {
    val context = LocalContext.current
    val selectedImageState = mainActivityViewModel.selectedPhotoState.collectAsState()

    when (val bitmap = selectedImageState.value) {
        null -> {
            PickAndCropImageGallery(
                cropActivityToolbarColor = Green1.toArgb(),
                cropMenuCropButtonTitle = R.string.crop_activity_title,
                onCropSuccess = { uri ->
                    mainActivityViewModel.setSelectedPhoto(uri = uri, context = context)
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
                        mainActivityViewModel.setSelectedPhoto(uri = uri, context = context)
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
                AppElevatedButton(
                    onClick = {
                        mainActivityViewModel.segmentImage()
                    }) {
                    Text(
                        text = LocalResources.current.getString(R.string.analyze_image_button_text),
                        color = BlackText,
                    )
                }
            }
        }
    }
}