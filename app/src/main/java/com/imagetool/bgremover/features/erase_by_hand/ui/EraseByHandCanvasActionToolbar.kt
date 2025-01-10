package com.imagetool.bgremover.features.erase_by_hand.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.features.erase_by_hand.DrawingByHandAction
import com.imagetool.bgremover.features.erase_by_hand.EraseByHandViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EraseByHandCanvasActionToolbar(eraseByHandViewModel: EraseByHandViewModel = koinViewModel()) {

    val drawingByHandState = eraseByHandViewModel.drawingByHandState.collectAsState()
    val localResource = LocalResources.current

    Column {
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            AppElevatedButton(
                modifier = Modifier.defaultMinSize(minWidth = 100.dp),
                enabled = drawingByHandState.value.undoStack.isNotEmpty(),
                onClick = {
                    eraseByHandViewModel.onDrawingAction(DrawingByHandAction.OnUndo)
                },
            ) {
                AppText(localResource.getString(R.string.erasebyhand_undo_button_text))
            }

            AppElevatedButton(
                modifier = Modifier.defaultMinSize(minWidth = 100.dp),
                enabled = drawingByHandState.value.redoStack.isNotEmpty(),
                onClick = {
                    eraseByHandViewModel.onDrawingAction(DrawingByHandAction.OnRedo)
                },
            ) {
                AppText(localResource.getString(R.string.erasebyhand_redo_button_text))
            }

            AppElevatedButton(
                modifier = Modifier.defaultMinSize(minWidth = 100.dp),
                enabled = drawingByHandState.value.undoStack.isNotEmpty(),
                onClick = {
                    eraseByHandViewModel.onDrawingAction(DrawingByHandAction.OnClear)
                },
            ) {
                AppText(localResource.getString(R.string.erasebyhand_clear_button_text))
            }
        }

        EraseByHandBrushSizeSlider(
            defaultValue = drawingByHandState.value.eraseBrushSize,
            onValueChange = {
                eraseByHandViewModel.setDrawingBrushSize(it)
            })
    }
}