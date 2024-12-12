package com.imagetool.bgremover.ui.composables.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.imagetool.bgremover.util.LocalResources

@Composable
fun AppAlertDialog(
    modifier: Modifier = Modifier,

    @StringRes confirmButtonText: Int,
    @StringRes titleText: Int,
    @StringRes contentText: Int,
    onDismiss: () -> Unit,
) {
    val showDialogState = remember {
        mutableStateOf(true)
    }

    val localResources = LocalResources.current

    if (!showDialogState.value) return

    AlertDialog(
        modifier = modifier.fillMaxSize(),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
        onDismissRequest = {
            showDialogState.value = false
            onDismiss()
        },
        confirmButton = {
            ElevatedButton(onClick = {
                showDialogState.value = false
                onDismiss()
            }) {
                Text(localResources.getString(confirmButtonText))
            }
        },
        title = {
            Text(localResources.getString(titleText))
        },
        text = {
            Text(localResources.getString(contentText))
        }
    )
}