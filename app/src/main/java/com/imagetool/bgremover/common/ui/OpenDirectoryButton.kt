package com.imagetool.bgremover.common.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.imagetool.bgremover.R
import com.imagetool.bgremover.util.FileUtil
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.util.showToast

@Composable
fun OpenDirectoryButton(modifier: Modifier = Modifier) {
    val localContext = LocalContext.current
    val localResources = LocalResources.current

    AppElevatedButton(
        modifier = modifier,
        onClick = {
            FileUtil.openGallery(
                context = localContext,
                onError = {
                    localContext.showToast(localResources.getString(R.string.open_folder_error_text))
                }
            )
        }
    ) {
        Row(Modifier.wrapContentSize()) {
            Icon(ImageVector.vectorResource(R.drawable.folder_open), contentDescription = "")
            Spacer(Modifier.width(8.dp))
            Text(localResources.getString(R.string.open_folder_button_text))
        }
    }

}