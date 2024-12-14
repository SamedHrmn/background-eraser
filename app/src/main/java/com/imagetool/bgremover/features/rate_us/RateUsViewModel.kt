package com.imagetool.bgremover.features.rate_us

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetool.bgremover.util.DataStoreHelper
import com.imagetool.bgremover.util.ReviewUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RateUsViewModel : ViewModel(){
    private val _showReviewState = MutableStateFlow(false)
    val showReviewState = _showReviewState.asStateFlow()

    fun initReview(context: Context) {
        ReviewUtil.initReview(context)
    }

    fun launchReview(context: Context, activity: Activity) {
        viewModelScope.launch {
            DataStoreHelper.getInt(
                DataStoreHelper.USER_IMAGE_COUNT_SHARED_KEY,
                0,
                context = context
            ).collect {
                if (it == 2 || it == 40 || it == 100) {
                    ReviewUtil.launchReview(activity)
                }
            }
        }
    }
}