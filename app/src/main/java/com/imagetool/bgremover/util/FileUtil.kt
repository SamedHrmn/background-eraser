package com.imagetool.bgremover.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore

object FileUtil {

    fun openGallery(context: Context, onError: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                data = MediaStore.Images.Media.INTERNAL_CONTENT_URI
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            try {
                context.startActivity(intent)
            } catch (_: Exception) {
                onError()
            }
        } else {
            val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            try {
                context.startActivity(pickImageIntent)
            } catch (_: Exception) {
                onError()
            }
        }
    }
}