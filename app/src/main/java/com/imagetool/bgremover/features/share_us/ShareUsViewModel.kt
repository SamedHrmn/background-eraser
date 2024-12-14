package com.imagetool.bgremover.features.share_us

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.imagetool.bgremover.util.isUrlReachable

class ShareUsViewModel : ViewModel() {

    private fun playStoreUrl(context: Context): String {
        return "https://play.google.com/store/apps/details?id=${context.packageName}"
    }

    fun showShareSheet(context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, playStoreUrl(context))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    suspend fun isValidPlayStoreUrl(context: Context): Boolean {
        return isUrlReachable(playStoreUrl(context))
    }
}