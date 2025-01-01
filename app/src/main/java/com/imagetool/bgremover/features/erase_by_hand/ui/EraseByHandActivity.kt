package com.imagetool.bgremover.features.erase_by_hand.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.imagetool.bgremover.common.provider.AppLocaleProvider
import com.imagetool.bgremover.features.erase_by_hand.EraseByHandViewModel
import com.imagetool.bgremover.theme.ImagetoolbackgroundremoverTheme
import com.imagetool.bgremover.util.IntentKeys
import com.imagetool.bgremover.util.IntentUtil
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.viewmodel.ext.android.getViewModel

class EraseByHandActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val eraseByHandViewModel = getViewModel<EraseByHandViewModel>()

        val originalImageUriString =
            IntentUtil.getArgs<String>(activity = this, argKey = IntentKeys.PickedImageUri)

        eraseByHandViewModel.setPickedImageUri(
            uriString = originalImageUriString,
            context = applicationContext
        )

        setContent {
            KoinAndroidContext {
                AppLocaleProvider(context = LocalContext.current) {
                    ImagetoolbackgroundremoverTheme {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            EraseByHandCanvas()
                        }
                    }
                }
            }
        }
    }
}