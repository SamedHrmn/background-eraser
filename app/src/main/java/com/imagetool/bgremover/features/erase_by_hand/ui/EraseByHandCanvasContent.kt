package com.imagetool.bgremover.features.erase_by_hand.ui

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import com.imagetool.bgremover.features.erase_by_hand.DrawingByHandAction
import com.imagetool.bgremover.features.erase_by_hand.EraseByHandViewModel
import com.imagetool.bgremover.util.scaledBitmapIfNeeded
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EraseByHandCanvasContent(
    modifier: Modifier = Modifier,
    eraseByHandViewModel: EraseByHandViewModel = koinViewModel()
) {

    val drawingByHandState = eraseByHandViewModel.drawingByHandState.collectAsState()
    val bitmap = eraseByHandViewModel.pickedImage.collectAsState()
    val canvasIsReady = remember { mutableStateOf(false) }

    LaunchedEffect(
        canvasIsReady.value,
        drawingByHandState.value.tempBitmap,
        drawingByHandState.value.canvasSize
    ) {
        drawingByHandState.value.tempBitmap?.let {
            eraseByHandViewModel.setTempBitmap(
                it.scaledBitmapIfNeeded(drawingByHandState.value.canvasSize)
            )
        }

    }


    val paint = remember {
        derivedStateOf {
            Paint().apply {
                color = Color.Transparent
                style = PaintingStyle.Stroke
                strokeWidth = drawingByHandState.value.eraseBrushSize
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear

            }
        }
    }

    val scaledBitmap = remember {
        derivedStateOf {
            val currentBitmap =
                drawingByHandState.value.tempBitmap
                    ?: bitmap.value?.copy(Bitmap.Config.ARGB_8888, true)?.apply {
                        isPremultiplied = true
                        setHasAlpha(true)
                    }

            currentBitmap?.let {
                if (it.width != drawingByHandState.value.canvasSize.width &&
                    it.height != drawingByHandState.value.canvasSize.height &&
                    drawingByHandState.value.lastAction is DrawingByHandAction.OnPathEnd

                ) {
                    return@derivedStateOf it.scaledBitmapIfNeeded(drawingByHandState.value.canvasSize)
                } else {
                    return@derivedStateOf it
                }
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
                canvasIsReady.value = true
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        eraseByHandViewModel.onDrawingAction(
                            DrawingByHandAction.OnDraw(
                                offset = change.position
                            )
                        )
                        change.consume()
                    },
                    onDragStart = { offset ->
                        eraseByHandViewModel.onDrawingAction(
                            DrawingByHandAction.OnNewPathDraw(
                                offset = offset
                            )
                        )
                    },
                    onDragEnd = {
                        eraseByHandViewModel.onDrawingAction(
                            DrawingByHandAction.OnPathEnd()
                        )
                    }
                )
            }) {
            if (scaledBitmap.value == null) return@Canvas

            val currentBitmap = scaledBitmap.value!!

            val combinedCanvas = androidx.compose.ui.graphics.Canvas(currentBitmap.asImageBitmap())

            combinedCanvas.drawPath(
                drawingByHandState.value.currentPath,
                paint.value,
            )

            eraseByHandViewModel.setTempBitmap(currentBitmap)

            drawImage(
                image = currentBitmap.asImageBitmap(),
            )

        }
    }
}