package com.imagetool.bgremover.util

import android.app.Activity
import android.content.Context
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

object ReviewUtil{
    private var reviewManager:ReviewManager? = null

    fun initReview(context:Context){
        try {
            reviewManager = ReviewManagerFactory.create(context)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    suspend fun launchReview(activity: Activity){
        if(reviewManager == null) return

       val reviewInfo = reviewManager!!.requestReview()
        reviewManager!!.launchReview(activity,reviewInfo)
    }
}