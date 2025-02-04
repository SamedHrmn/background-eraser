package com.imagetool.bgremover.features.erase_by_hand.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalNavController
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.features.erase_by_hand.EraseByHandViewModel
import com.imagetool.bgremover.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun EraseByHandCanvasBottomToolbar(eraseByHandViewModel: EraseByHandViewModel = koinViewModel()) {

    val localContext = LocalContext.current
    val localResource = LocalResources.current
    val localNavController = LocalNavController.current
    val coroutineScopeState = rememberCoroutineScope()
    val drawingByHandState = eraseByHandViewModel.drawingByHandState.collectAsState()

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        AppElevatedButton(modifier = Modifier.defaultMinSize(minWidth = 100.dp), onClick = {
            eraseByHandViewModel.navigateMainActivity(navController = localNavController)
        }) {
            AppText(localResource.getString(R.string.erasebyhand_back_button_text))
        }
        AppElevatedButton(
            modifier = Modifier
                .defaultMinSize(minWidth = 100.dp),
            enabled = drawingByHandState.value.undoStack.isNotEmpty(),
            onClick = {
                if (eraseByHandViewModel.drawingByHandState.value.tempBitmap == null) return@AppElevatedButton

                coroutineScopeState.launch {
                    val isSuccess = eraseByHandViewModel.saveErasedImage(
                        context = localContext,
                        localResource = localResource,
                        bitmap = eraseByHandViewModel.drawingByHandState.value.tempBitmap!!
                    )

                    withContext(Dispatchers.Main) {
                        if (isSuccess) {
                            localContext.showToast(text = localResource.getString(R.string.save_success_text))
                        } else {
                            localContext.showToast(text = localResource.getString(R.string.save_error_text))
                        }
                    }

                    eraseByHandViewModel.navigateMainActivity(navController = localNavController)
                }

            }) {
            AppText(localResource.getString(R.string.save_button_text))
        }
    }
}