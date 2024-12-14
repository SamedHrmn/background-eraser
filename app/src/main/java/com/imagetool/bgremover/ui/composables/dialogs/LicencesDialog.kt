package com.imagetool.bgremover.ui.composables.dialogs

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.imagetool.bgremover.R
import com.imagetool.bgremover.ui.theme.Green1
import com.imagetool.bgremover.ui.theme.WhiteText
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.LibraryDefaults
import com.mikepenz.aboutlibraries.util.withJson

@Composable
fun LicencesDialog(onDismiss: () -> Unit) {
    AppAlertDialog(
        onDismiss = {
            onDismiss()
        },
        titleText = R.string.licences_dialog_title,
        confirmButtonText = R.string.licences_dialog_confirm_button_text,
        content = {
            LibrariesContainer(
                colors = LibraryDefaults.libraryColors(
                    badgeContentColor = WhiteText,
                    badgeBackgroundColor = Green1,
                    backgroundColor = Color.Transparent
                ),
                librariesBlock = { ctx ->
                    Libs.Builder().withJson(ctx, R.raw.aboutlibraries).build()
                }
            )
        }
    )
}