package com.imagetool.bgremover.features.feedback

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel

class FeedbackViewModel : ViewModel(){

    fun sendFeedback(text: String,context:Context, onError: () -> Unit){
        val recipient = "xxsamedx@gmail.com" // Hi hackers. This is contact mail.
        val subject = "App Feedback"
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        val androidVersion = Build.VERSION.RELEASE
        val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"

        val body = """
        $text

        ---
        App Version: $appVersion
        Android Version: $androidVersion
        Device: $deviceModel
        ---
    """.trimIndent()

        val selectorIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
        }
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            selector = selectorIntent
        }
        if (emailIntent.resolveActivity(context.packageManager) != null) {
            try {
                context.startActivity(emailIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
    }
}