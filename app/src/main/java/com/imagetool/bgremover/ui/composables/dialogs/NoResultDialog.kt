package com.imagetool.bgremover.ui.composables.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.imagetool.bgremover.R

@Composable
fun NoResultDialog(modifier: Modifier = Modifier, onDismiss: () -> Unit = {}) {
    AppAlertDialog(
        modifier = modifier,
        onDismiss = {
            onDismiss()
        },
        confirmButtonText = R.string.no_result_dialog_confirm_button_text,
        titleText = R.string.no_result_dialog_title,
        contentText = R.string.no_result_dialog_text
    )
}