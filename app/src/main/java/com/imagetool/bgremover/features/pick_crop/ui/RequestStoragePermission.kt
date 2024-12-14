package com.imagetool.bgremover.features.pick_crop.ui

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.imagetool.bgremover.common.ui.dialogs.PermissionRationaleDialog
import com.imagetool.bgremover.util.openAppSettings

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestStoragePermission(
    content: @Composable (onPermissionRequest: () -> Unit) -> Unit,
) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    } else {
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)
    val permissionRequestedOnceState = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current



    if (permissionState.status.isGranted) {
        content {
            //Permission granted already
        }
    } else if (permissionState.status.shouldShowRationale) {
        PermissionRationaleDialog {
            permissionState.launchPermissionRequest()
        }

        permissionRequestedOnceState.value = true
    } else if (permissionRequestedOnceState.value) {
        PermissionRationaleDialog {
            context.openAppSettings()
        }
        permissionRequestedOnceState.value = false
    } else {
        content {
            permissionState.launchPermissionRequest()
            permissionRequestedOnceState.value = true
        }
    }
}