package com.imagetool.bgremover.features.erase.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.ui.dialogs.AppAlertDialog

@Composable
fun ErrorDialog(modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    AppAlertDialog(
        modifier = modifier,
        onDismiss = {
            onDismiss()
        },
        confirmButtonText = R.string.error_dialog_confirm_button_text,
        titleText = R.string.error_dialog_title,
        contentText = R.string.error_dialog_text,
    )
}