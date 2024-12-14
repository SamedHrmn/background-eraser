package com.imagetool.bgremover.common.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.imagetool.bgremover.common.provider.LocalResources

@Composable
fun AppAlertDialog(
    modifier: Modifier = Modifier,
    @StringRes confirmButtonText: Int? = null,
    confirmButton: @Composable (() -> Unit)? = null,
    @StringRes titleText: Int,
    @StringRes contentText: Int? = null,
    content: @Composable (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    val showDialogState = remember {
        mutableStateOf(true)
    }

    val localResources = LocalResources.current
    val scrollState = rememberScrollState()

    if (!showDialogState.value) return

    AlertDialog(
        modifier = modifier,
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
            when {
                confirmButton != null -> confirmButton()
                confirmButtonText != null -> {
                    ElevatedButton(onClick = {
                        showDialogState.value = false
                        onDismiss()
                    }) {
                        Text(localResources.getString(confirmButtonText))
                    }
                }
            }
        },
        title = {
            Text(localResources.getString(titleText))
        },
        text = {
            when {
                content != null -> content()
                contentText != null -> Text(
                    localResources.getString(contentText),
                    modifier = Modifier.verticalScroll(state = scrollState)
                )
            }
        }
    )
}