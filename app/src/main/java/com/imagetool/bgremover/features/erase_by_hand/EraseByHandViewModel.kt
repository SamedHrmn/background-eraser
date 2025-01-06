package com.imagetool.bgremover.features.erase_by_hand

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetool.bgremover.MainActivity
import com.imagetool.bgremover.common.use_cases.SaveImageUseCase
import com.imagetool.bgremover.util.ImageUtil
import com.imagetool.bgremover.util.IntentUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EraseByHandViewModel(
    private val imageUtil: ImageUtil,
    private val saveImageUseCase: SaveImageUseCase
) : ViewModel() {
    private val _pickedImageState = MutableStateFlow<Bitmap?>(null)
    val pickedImage = _pickedImageState.asStateFlow()
    private val _drawingByHandState = MutableStateFlow(DrawingByHandState())
    val drawingByHandState = _drawingByHandState.asStateFlow()

    fun setDrawingBrushSize(size: Float) {
        _drawingByHandState.update {
            it.copy(eraseBrushSize = size)
        }
    }

    fun setCanvasSize(size: IntSize) {
        _drawingByHandState.update {
            it.copy(canvasSize = size)
        }
    }

    fun setTempBitmap(bitmap: Bitmap?) {
        _drawingByHandState.update {
            it.copy(tempBitmap = bitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onDrawingAction(action: DrawingByHandAction) {
        when (action) {
            is DrawingByHandAction.OnNewPathDraw -> {
                action.offset?.let {
                    _drawingByHandState.update { state ->
                        state.copy(
                            lastAction = action,
                            currentPath = Path().apply {
                                moveTo(
                                    x = it.x,
                                    y = it.y
                                )
                            },
                        )
                    }

                }
            }

            is DrawingByHandAction.OnDraw -> {
                action.offset?.let {
                    _drawingByHandState.update { state ->
                        state.copy(lastAction = action)
                    }
                    _drawingByHandState.value.currentPath.lineTo(x = it.x, y = it.y)
                }
            }

            is DrawingByHandAction.OnClear -> {
                _drawingByHandState.value =
                    DrawingByHandState(
                        canvasSize = drawingByHandState.value.canvasSize,
                        lastAction = action,
                    )
                setTempBitmap(recreateDrawedBitmap())
            }

            is DrawingByHandAction.OnPathEnd -> {
                _drawingByHandState.value = _drawingByHandState.value.copy(
                    undoStack = (_drawingByHandState.value.undoStack + _drawingByHandState.value.currentPath).toList(),
                    currentPath = Path(),
                    lastAction = action,
                )
            }

            is DrawingByHandAction.OnUndo -> {
                if (_drawingByHandState.value.undoStack.isEmpty()) return

                val lastItem = _drawingByHandState.value.undoStack.last()
                _drawingByHandState.value = _drawingByHandState.value.copy(
                    undoStack = _drawingByHandState.value.undoStack.dropLast(1).toList(),
                    redoStack = (_drawingByHandState.value.redoStack + lastItem).toList(),
                    lastAction = action,
                )

                setTempBitmap(recreateDrawedBitmap())

            }

            is DrawingByHandAction.OnRedo -> {
                if (_drawingByHandState.value.redoStack.isEmpty()) return

                val lastItem = _drawingByHandState.value.redoStack.last()

                _drawingByHandState.value = _drawingByHandState.value.copy(
                    redoStack = _drawingByHandState.value.redoStack.dropLast(1).toList(),
                    undoStack = (_drawingByHandState.value.undoStack + lastItem).toList(),
                    lastAction = action,
                )
                setTempBitmap(recreateDrawedBitmap())
            }
        }
    }

    fun setPickedImageUri(uriString: String, context: Context) {
        val uri = Uri.parse(uriString)

        viewModelScope.launch {
            imageUtil.uriToBitmap(
                uri = uri,
                context = context,
                onSuccess = { bitmap ->
                    _pickedImageState.value = bitmap
                },
            )
        }
    }

    suspend fun saveErasedImage(
        context: Context,
        localResource: Resources,
        bitmap: Bitmap
    ): Boolean {
        return saveImageUseCase.execute(
            context = context,
            localResources = localResource,
            bitmaps = listOf(bitmap)
        )
    }

    fun navigateMainActivity(context: Context) {
        IntentUtil.intent(
            context = context,
            dest = MainActivity::class.java,
            flags = listOf(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun recreateDrawedBitmap(): Bitmap? {
        if (pickedImage.value == null) return null

        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.Transparent.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = drawingByHandState.value.eraseBrushSize
            blendMode = BlendMode.CLEAR
        }

        val scaledBitmap = Bitmap.createScaledBitmap(
            pickedImage.value!!,
            drawingByHandState.value.canvasSize.width,
            drawingByHandState.value.canvasSize.height,
            false
        )

        val mutableBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true)

        val combinedCanvas = android.graphics.Canvas(mutableBitmap)
        combinedCanvas.drawBitmap(
            mutableBitmap,
            0f,
            0f,
            null
        )

        drawingByHandState.value.undoStack.forEach { pathData ->
            combinedCanvas.drawPath(pathData.asAndroidPath(), paint)
        }
        return mutableBitmap
    }
}

data class DrawingByHandState(
    val currentPath: Path = Path(),
    val undoStack: List<Path> = listOf(),
    val redoStack: List<Path> = listOf(),
    val tempBitmap: Bitmap? = null,
    val eraseBrushSize: Float = 32f,
    val canvasSize: IntSize = IntSize.Zero,
    val lastAction: DrawingByHandAction? = null,
)

sealed interface DrawingByHandAction {
    data class OnNewPathDraw(val offset: Offset? = null) : DrawingByHandAction
    data class OnDraw(val offset: Offset? = null) : DrawingByHandAction
    data class OnPathEnd(val path: Path? = null) : DrawingByHandAction
    data object OnClear : DrawingByHandAction
    data object OnUndo : DrawingByHandAction
    data object OnRedo : DrawingByHandAction
}
