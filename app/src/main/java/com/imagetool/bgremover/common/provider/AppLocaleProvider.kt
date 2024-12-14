package com.imagetool.bgremover.common.provider

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

val LocalResources =  staticCompositionLocalOf<Resources> {
    error("No resource provided")
}

@Composable
fun AppLocaleProvider(
    context: Context,
    content: @Composable () -> Unit
) {


    val config = Configuration(context.resources.configuration).apply {
        setLocale(getAppLocale())
    }

    val localizedResources = context.createConfigurationContext(config).resources

    CompositionLocalProvider(LocalResources provides localizedResources) {
        content()
    }
}

fun getAppLocale(): Locale {
    return when (Locale.getDefault().language) {
        "tr" -> Locale("tr") // Turkish
        else -> Locale("en") // Fallback to English
    }
}
