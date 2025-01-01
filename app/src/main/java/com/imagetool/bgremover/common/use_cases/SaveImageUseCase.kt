package com.imagetool.bgremover.common.use_cases

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import com.imagetool.bgremover.util.ImageUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveImageUseCase(
    private val imageUtils: ImageUtil,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun execute(
        context: Context,
        localResources: Resources,
        bitmaps: List<Bitmap>
    ): Boolean {

        if (bitmaps.isEmpty()) return false

        return withContext(ioDispatcher) {
            try {
                imageUtils.saveBitmapsAsPngToGallery(
                    context = context,
                    localResources = localResources,
                    bitmaps = bitmaps
                )
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    }
}