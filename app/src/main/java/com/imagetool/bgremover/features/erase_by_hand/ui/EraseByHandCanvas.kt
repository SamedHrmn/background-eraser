package com.imagetool.bgremover.features.erase_by_hand.ui

import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.features.erase_by_hand.DrawingByHandAction
import com.imagetool.bgremover.features.erase_by_hand.EraseByHandViewModel
import com.imagetool.bgremover.theme.ImagetoolbackgroundremoverTheme
import com.imagetool.bgremover.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EraseByHandCanvas() {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 12.dp)
    ) {
        EraseByHandCanvasActionToolbar()
        Spacer(Modifier.height(12.dp))
        EraseByHandCanvasContent(modifier = Modifier.weight(8f))
        Spacer(Modifier.height(24.dp))
        EraseByHandCanvasBottomToolbar()
        Spacer(Modifier.weight(2f))
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EraseByHandCanvasContent(
    modifier: Modifier = Modifier,
    eraseByHandViewModel: EraseByHandViewModel = koinViewModel()
) {

    val drawingByHandState = eraseByHandViewModel.drawingByHandState.collectAsState()
    val bitmap = eraseByHandViewModel.pickedImage.collectAsState()

    val paint = remember {
        derivedStateOf {
            Paint().apply {
                isAntiAlias = true
                color = Color.Transparent.toArgb()
                style = Paint.Style.STROKE
                strokeWidth = drawingByHandState.value.eraseBrushSize
                blendMode = BlendMode.CLEAR
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .onPlaced { layoutCoordinates ->
                eraseByHandViewModel.setCanvasSize(layoutCoordinates.size)
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        eraseByHandViewModel.onDrawingAction(
                            DrawingByHandAction.onDraw(
                                offset = change.position
                            )
                        )
                        change.consume()
                    },
                    onDragStart = { offset ->
                        eraseByHandViewModel.onDrawingAction(
                            DrawingByHandAction.onNewPathDraw(
                                offset = offset
                            )
                        )
                    },
                    onDragEnd = {
                        eraseByHandViewModel.onDrawingAction(
                            DrawingByHandAction.onPathEnd()
                        )
                    }
                )
            }) {
            val currentBitmap =
                drawingByHandState.value.tempBitmap
                    ?: bitmap.value?.copy(Bitmap.Config.ARGB_8888, true)?.apply {
                        isPremultiplied = true
                        setHasAlpha(true)
                    }

            currentBitmap?.let { baseBitmap ->
                val outputBitmap = Bitmap.createScaledBitmap(
                    baseBitmap,
                    size.width.toInt(),
                    size.height.toInt(),
                    false,
                )
                val combinedCanvas = android.graphics.Canvas(outputBitmap)

                combinedCanvas.drawPath(
                    drawingByHandState.value.currentPath.asAndroidPath(),
                    paint.value,
                )

                eraseByHandViewModel.setTempBitmap(outputBitmap)

                drawImage(
                    image = outputBitmap.asImageBitmap(),
                )
            }
        }
    }
}

@Composable
fun EraseByHandCanvasBottomToolbar(eraseByHandViewModel: EraseByHandViewModel = koinViewModel()) {

    val localContext = LocalContext.current
    val localResource = LocalResources.current
    val coroutineScopeState = rememberCoroutineScope()
    val drawingByHandState = eraseByHandViewModel.drawingByHandState.collectAsState()

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        AppElevatedButton(modifier = Modifier.defaultMinSize(minWidth = 100.dp), onClick = {
            eraseByHandViewModel.navigateMainActivity(context = localContext)
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

                    eraseByHandViewModel.navigateMainActivity(context = localContext)
                }

            }) {
            AppText(localResource.getString(R.string.save_button_text))
        }
    }
}

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
                    eraseByHandViewModel.onDrawingAction(DrawingByHandAction.onUndo)
                },
            ) {
                AppText(localResource.getString(R.string.erasebyhand_undo_button_text))
            }

            AppElevatedButton(
                modifier = Modifier.defaultMinSize(minWidth = 100.dp),
                enabled = drawingByHandState.value.redoStack.isNotEmpty(),
                onClick = {
                    eraseByHandViewModel.onDrawingAction(DrawingByHandAction.onRedo)
                },
            ) {
                AppText(localResource.getString(R.string.erasebyhand_redo_button_text))
            }

            AppElevatedButton(
                modifier = Modifier.defaultMinSize(minWidth = 100.dp),
                enabled = drawingByHandState.value.undoStack.isNotEmpty(),
                onClick = {
                    eraseByHandViewModel.onDrawingAction(DrawingByHandAction.onClear)
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


@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun EraseByHandCanvasPreview() {
    ImagetoolbackgroundremoverTheme {
        EraseByHandCanvas()
    }
}





