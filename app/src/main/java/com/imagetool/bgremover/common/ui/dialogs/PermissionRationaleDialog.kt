package com.imagetool.bgremover.common.ui.dialogs

import androidx.compose.runtime.Composable
import com.imagetool.bgremover.R

@Composable
fun PermissionRationaleDialog(onDismiss:() -> Unit){
    AppAlertDialog(
        titleText = R.string.storage_permission_rationale_dialog_title,
        confirmButtonText = R.string.storage_permission_rationale_dialog_confirm_button_text,
        contentText = R.string.storage_permission_rationale_dialog_text,
        onDismiss = {
            onDismiss()
        }
    )
}