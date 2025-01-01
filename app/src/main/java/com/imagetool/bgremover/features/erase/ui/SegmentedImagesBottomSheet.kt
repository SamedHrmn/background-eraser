package com.imagetool.bgremover.features.erase.ui

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.features.erase.BackgroundEraserViewModel
import com.imagetool.bgremover.theme.ErrorRed
import com.imagetool.bgremover.util.animatePlacement
import com.imagetool.bgremover.util.showToast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedImagesBottomSheet(
    bitmaps: List<Bitmap>,
    backgroundEraserViewModel: BackgroundEraserViewModel = koinViewModel(),
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onCancelClick: () -> Unit
) {


    val localContext = LocalContext.current
    val localResource = LocalResources.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScopeState = rememberCoroutineScope()

    val hasSelectedItemState = remember { mutableStateOf(false) }
    val selectedImagesState = remember { mutableStateListOf<Bitmap>() }



    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            SelectableImageListView(
                images = bitmaps,
                onItemsSelected = { items ->
                    hasSelectedItemState.value = items.isNotEmpty()
                    items.forEach {
                        selectedImagesState.add(bitmaps[it])
                    }
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                AnimatedVisibility(visible = hasSelectedItemState.value) {
                    AppElevatedButton(
                        modifier = Modifier.animatePlacement().defaultMinSize(minWidth = 100.dp),
                        onClick = {
                            coroutineScopeState.launch {
                                val isSuccess = backgroundEraserViewModel.saveSelectedImages(
                                    context = localContext,
                                    localResources = localResource,
                                    bitmaps = selectedImagesState.toList()
                                )

                                if (isSuccess) {
                                    localContext.showToast(text = localResource.getString(R.string.save_success_text))
                                } else {
                                    localContext.showToast(text = localResource.getString(R.string.save_error_text))
                                }

                                sheetState.hide()
                            }

                            onSave()

                        }) {
                        AppText(localResource.getString(R.string.save_button_text))
                    }
                }
                AppElevatedButton(
                    modifier = Modifier.animatePlacement().defaultMinSize(minWidth = 100.dp),
                    borderColor = ErrorRed,
                    onClick = {
                        coroutineScopeState.launch {
                            sheetState.hide()

                        }
                        hasSelectedItemState.value = false
                        selectedImagesState.clear()
                        backgroundEraserViewModel.clearLastSegmentation()
                        onCancelClick()
                    },
                ) {
                    AppText(localResource.getString(R.string.cancel_button_text))
                }
            }
        }
    }
}