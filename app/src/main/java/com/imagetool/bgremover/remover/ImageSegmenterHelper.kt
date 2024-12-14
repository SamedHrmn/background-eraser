package com.imagetool.bgremover.remover

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions

class ImageSegmenterHelper(val imageSegmenterListener: ImageSegmenterListener,val context: Context) {

    private var segmenter: SubjectSegmenter? = null

    fun initializeSegmenter() {
        try {
            val subjectResultOptions = SubjectSegmenterOptions.SubjectResultOptions.Builder()
                .enableSubjectBitmap()
                .build()

            val options = SubjectSegmenterOptions.Builder()
                .enableMultipleSubjects(subjectResultOptions)
                .build()

            segmenter = SubjectSegmentation.getClient(options)
            imageSegmenterListener.onInitialized()
        } catch (e: Exception) {
            imageSegmenterListener.onError(message = e.message)
        }
    }

    fun segmentImage(bitmap: Bitmap) {
        imageSegmenterListener.onProcessing()

        if (segmenter == null) {
            imageSegmenterListener.onError(message = "Segmenter does not initialize")
            return
        }

        val inputImage = InputImage.fromBitmap(bitmap, 0)
        segmenter!!.process(inputImage).addOnSuccessListener { result->
            val bitmaps = mutableListOf<Bitmap>()
            result.subjects.forEach {
                if(it.bitmap != null){
                    bitmaps.add(it.bitmap!!)
                }
            }
            imageSegmenterListener.onResult(bitmaps = bitmaps.toList(), context = context)
        }.addOnFailureListener { e->
            imageSegmenterListener.onError(message = e.message)
        }
    }

    fun close(){
        segmenter?.close()
        segmenter = null
    }

    interface ImageSegmenterListener {
        fun onInitialized()
        fun onProcessing()
        fun onError(message: String?)
        fun onResult(bitmaps:List<Bitmap>,context: Context)
    }
}