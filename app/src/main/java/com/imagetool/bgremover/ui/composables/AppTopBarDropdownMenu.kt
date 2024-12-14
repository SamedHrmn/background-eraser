package com.imagetool.bgremover.ui.composables

import android.content.res.Resources
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.imagetool.bgremover.R
import com.imagetool.bgremover.ui.composables.dialogs.LicencesDialog
import com.imagetool.bgremover.ui.composables.dialogs.PrivacyPolicyDialog
import com.imagetool.bgremover.ui.composables.dialogs.SendFeedbackDialog
import com.imagetool.bgremover.util.LocalResources
import com.imagetool.bgremover.util.isUrlReachable
import com.imagetool.bgremover.util.showShareSheet
import kotlinx.coroutines.launch

enum class AppTopBarDropdownMenus {
    privacyPolicy,
    licences,
    share,
    feedback;


    fun toLocalizedText(resources: Resources): String {
        return when (this) {
            privacyPolicy -> resources.getString(R.string.privacy_policy_menu)
            licences -> resources.getString(R.string.licences_menu)
            share -> resources.getString(R.string.share_menu)
            feedback -> resources.getString(R.string.feedback_menu)
        }
    }
}

@Composable
fun AppTopBarDropdownMenu(dropdownState: MutableState<Boolean>, onDismissRequest: () -> Unit) {
    val localContext = LocalContext.current
    val coroutineScopeState = rememberCoroutineScope()

    val shareButtonVisible = remember { mutableStateOf(false) }
    val playStoreUrl = "https://play.google.com/store/apps/details?id=${localContext.packageName}"

    val showSendFeedbackDialogState = remember { mutableStateOf(false) }
    val showPrivacyPolicyDialogState = remember { mutableStateOf(false) }
    val showLicencesDialogState = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScopeState.launch {
            shareButtonVisible.value = isUrlReachable(playStoreUrl)
        }
    }

    DropdownMenu(
        expanded = dropdownState.value,
        onDismissRequest = onDismissRequest,
    ) {
        AppTopBarDropdownMenus.entries.forEach { menuItem ->
            if (menuItem == AppTopBarDropdownMenus.share && !shareButtonVisible.value) {
                return@forEach
            }

            AppTopBarDropdownMenuItem(menu = menuItem, onItemSelected = { item ->
                dropdownState.value = false

                when (item) {
                    AppTopBarDropdownMenus.privacyPolicy -> {
                        showPrivacyPolicyDialogState.value = true
                    }

                    AppTopBarDropdownMenus.licences -> {
                        showLicencesDialogState.value = true
                    }

                    AppTopBarDropdownMenus.share -> {
                        localContext.showShareSheet(text = playStoreUrl)
                    }

                    AppTopBarDropdownMenus.feedback -> {
                        showSendFeedbackDialogState.value = true
                    }
                }
            })
        }
    }

    if (showSendFeedbackDialogState.value) {
        SendFeedbackDialog(onDismiss = {
            showSendFeedbackDialogState.value = false
        })
    } else if (showPrivacyPolicyDialogState.value) {
        PrivacyPolicyDialog(onDismiss = {
            showPrivacyPolicyDialogState.value = false
        })
    } else if (showLicencesDialogState.value) {
        LicencesDialog {
            showLicencesDialogState.value = false
        }
    }
}

@Composable
fun AppTopBarDropdownMenuItem(
    menu: AppTopBarDropdownMenus,
    onItemSelected: (item: AppTopBarDropdownMenus) -> Unit
) {
    val selectedMenuItemIndex = remember {
        mutableStateOf<Int?>(null)
    }

    DropdownMenuItem(
        text = { Text(menu.toLocalizedText(resources = LocalResources.current)) },
        onClick = {
            selectedMenuItemIndex.value = AppTopBarDropdownMenus.entries.indexOf(menu)
            onItemSelected(AppTopBarDropdownMenus.entries.elementAt(selectedMenuItemIndex.value!!))
        }
    )
}