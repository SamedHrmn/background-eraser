package com.imagetool.bgremover.util

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.imagetool.bgremover.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtil {

    private const val SAVED_IMAGE_DIR_PATH = "Pictures/"

    fun uriToBitmap(
        uri: Uri,
        context: Context,
        scope: CoroutineScope,
        onSuccess: (bitmap: Bitmap) -> Unit
    ) {
        var bitmap: Bitmap? = null

        val loadBitmap = scope.launch(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context).data(uri).build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                bitmap = result.image.toBitmap()
            } else if (result is ErrorResult) {
                cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
            }
        }

        loadBitmap.invokeOnCompletion {
            bitmap?.let {
                onSuccess(it)
            }
        }
    }

    fun saveBitmapsAsPngToGallery(
        context: Context,
        localResources: Resources,
        bitmaps: List<Bitmap>
    ) {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm_ss", Locale.getDefault())


        for (bitmap in bitmaps) {
            val fileName = "${dateFormat.format(Date())}.png"

            try {
                val outputStream: OutputStream? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // For API 29 and above
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                            put(
                                MediaStore.Images.Media.RELATIVE_PATH,
                                "$SAVED_IMAGE_DIR_PATH${localResources.getString(R.string.app_name)}"
                            )
                        }
                        val uri = context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                        uri?.let { context.contentResolver.openOutputStream(it) }
                    } else {
                        // For older APIs
                        val picturesDir = File(
                            context.getExternalFilesDir(null),
                            "$SAVED_IMAGE_DIR_PATH${localResources.getString(R.string.app_name)}"
                        )
                        if (!picturesDir.exists()) picturesDir.mkdirs()
                        val file = File(picturesDir, fileName)
                        FileOutputStream(file)
                    }

                outputStream?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}