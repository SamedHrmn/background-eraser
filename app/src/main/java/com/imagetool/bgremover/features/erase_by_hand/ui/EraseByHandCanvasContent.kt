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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import com.imagetool.bgremover.features.erase_by_hand.DrawingByHandAction
import com.imagetool.bgremover.features.erase_by_hand.EraseByHandViewModel
import org.koin.androidx.compose.koinViewModel

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