package com.imagetool.bgremover.features.erase_by_hand.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imagetool.bgremover.theme.ImagetoolbackgroundremoverTheme

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EraseByHandCanvas() {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 12.dp)
    ) {
        EraseByHandCanvasActionToolbar()
        Spacer(Modifier.height(12.dp))
        EraseByHandCanvasContent(modifier = Modifier.weight(8f))
        Spacer(Modifier.height(24.dp))
        EraseByHandCanvasBottomToolbar()
        Spacer(Modifier.weight(2f))
    }
}



@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun EraseByHandCanvasPreview() {
    ImagetoolbackgroundremoverTheme {
        EraseByHandCanvas()
    }
}





