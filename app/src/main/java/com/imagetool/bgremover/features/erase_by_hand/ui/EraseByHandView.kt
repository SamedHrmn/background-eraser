package com.imagetool.bgremover.features.erase_by_hand.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EraseByHandView(){
    Surface(modifier = Modifier.fillMaxSize()) {
        EraseByHandCanvas()
    }
}