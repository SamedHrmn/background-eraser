package com.imagetool.bgremover.ui.composables.dialogs

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.BasicRichText
import com.imagetool.bgremover.R
import com.imagetool.bgremover.util.LocalResources

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {

    val localResource = LocalResources.current
    val scrollState = rememberScrollState()


    AppAlertDialog(
        confirmButtonText = R.string.privacy_policy_dialog_confirm_button_text,
        titleText = R.string.privacy_policy_dialog_title,
        content = {
            BasicRichText(modifier = Modifier.verticalScroll(state = scrollState)) {
                Markdown(
                    localResource.getString(R.string.privacy_policy),
                )
            }
        },
        onDismiss = {
            onDismiss()
        }
    )
}