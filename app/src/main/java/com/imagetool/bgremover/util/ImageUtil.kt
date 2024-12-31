package com.imagetool.bgremover.util

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.imagetool.bgremover.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageUtil {

    companion object{
        private const val SAVED_IMAGE_DIR_PATH = "Pictures/"
    }

    suspend fun uriToBitmap(
        uri: Uri,
        context: Context,
        onSuccess: (bitmap: Bitmap) -> Unit
    ) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context).data(uri).build()

        val result = withContext(Dispatchers.IO) {
            val response = loader.execute(request)
            if (response is SuccessResult) {
                response.image.toBitmap()
            } else if (response is ErrorResult) {
                throw response.throwable
            } else {
                throw Exception("Unknown error while loading image")
            }
        }

        onSuccess(result)
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