package com.imagetool.bgremover.features.erase

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions

class BackgroundEraserHelper(
    val context: Context,
) {
    private lateinit var backgroundEraserResultListener: BackgroundEraserResultListener
    private var subjectSegmenter: SubjectSegmenter? = null

    fun setListener(listener: BackgroundEraserResultListener) {
        backgroundEraserResultListener = listener
    }

    fun initializeSegmenter() {
        try {
            val subjectResultOptions = SubjectSegmenterOptions.SubjectResultOptions.Builder()
                .enableSubjectBitmap()
                .build()

            val options = SubjectSegmenterOptions.Builder()
                .enableMultipleSubjects(subjectResultOptions)
                .build()

            subjectSegmenter = SubjectSegmentation.getClient(options)
            backgroundEraserResultListener.onInitialized()
        } catch (e: Exception) {
            backgroundEraserResultListener.onError(message = e.message)
        }
    }

    fun segmentImage(bitmap: Bitmap) {
        backgroundEraserResultListener.onProcessing()

        if (subjectSegmenter == null) {
            backgroundEraserResultListener.onError(message = "Segmenter does not initialize")
            return
        }

        val inputImage = InputImage.fromBitmap(bitmap, 0)
        subjectSegmenter!!.process(inputImage).addOnSuccessListener { result ->
            val bitmaps = mutableListOf<Bitmap>()
            result.subjects.forEach {
                if (it.bitmap != null) {
                    bitmaps.add(it.bitmap!!)
                }
            }
            backgroundEraserResultListener.onResult(bitmaps = bitmaps.toList(), context = context)
        }.addOnFailureListener { e ->
            backgroundEraserResultListener.onError(message = e.message)
        }
    }

    fun close() {
        subjectSegmenter?.close()
        subjectSegmenter = null
    }

    interface BackgroundEraserResultListener {
        fun onInitialized()
        fun onProcessing()
        fun onError(message: String?)
        fun onResult(bitmaps: List<Bitmap>, context: Context)
    }
}