package com.imagetool.bgremover.ui.composables

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.imagetool.bgremover.MainActivityViewModel
import com.imagetool.bgremover.R
import com.imagetool.bgremover.ui.theme.ErrorRed
import com.imagetool.bgremover.util.LocalResources
import com.imagetool.bgremover.util.animatePlacement
import com.imagetool.bgremover.util.showToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedImagesBottomSheet(
    bitmaps: List<Bitmap>,
    mainActivityViewModel: MainActivityViewModel,
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
        modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.times(0.6).dp),
        sheetState = sheetState,
        properties = ModalBottomSheetDefaults.properties(shouldDismissOnBackPress = false),
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column {
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
                        modifier = Modifier.animatePlacement(),
                        onClick = {
                            val isSuccess = mainActivityViewModel.saveSelectedImages(
                                context = localContext,
                                localResources = localResource,
                                bitmaps = selectedImagesState.toList()
                            )

                            if (isSuccess) {
                                localContext.showToast(text = localResource.getString(R.string.save_success_text))
                            } else {
                                localContext.showToast(text = localResource.getString(R.string.save_error_text))
                            }

                            coroutineScopeState.launch {
                                sheetState.hide()
                            }

                            onSave()

                        }) {
                        Text(localResource.getString(R.string.save_button_text))
                    }
                }
                AppElevatedButton(
                    modifier = Modifier.animatePlacement(),
                    borderColor = ErrorRed,
                    onClick = {
                        coroutineScopeState.launch {
                            sheetState.hide()

                        }
                        hasSelectedItemState.value = false
                        selectedImagesState.clear()
                        mainActivityViewModel.clearLastSegmentation()
                        onCancelClick()
                    },
                ) {
                    Text(localResource.getString(R.string.cancel_button_text))
                }
            }
        }
    }
}