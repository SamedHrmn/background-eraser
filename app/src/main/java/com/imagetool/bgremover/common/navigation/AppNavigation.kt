package com.imagetool.bgremover.common.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.imagetool.bgremover.HomeView
import com.imagetool.bgremover.common.provider.AppNavControllerProvider
import com.imagetool.bgremover.common.provider.LocalNavController
import com.imagetool.bgremover.features.erase_by_hand.ui.EraseByHandView


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavigation() {

    AppNavControllerProvider {
        NavHost(navController = LocalNavController.current, startDestination = AppScreens.HomeView.routeName) {
            composable(AppScreens.HomeView.routeName) {
                HomeView()
            }
            composable(
                AppScreens.EraseByHandView.routeName,
                arguments = listOf(
                    navArgument(name = AppNavigationArgKeys.PickedImageUri.name) {
                        type = NavType.StringType
                    },
                ),
            ) {
                EraseByHandView()
            }
        }
    }
}

sealed class AppScreens(val routeName: String) {
    data object HomeView : AppScreens("homeView")
    data object EraseByHandView :
        AppScreens("eraseByHandView?${AppNavigationArgKeys.PickedImageUri.name}={${AppNavigationArgKeys.PickedImageUri.name}}") {
        fun createRoute(pickedImageUri: String): String =
            "eraseByHandView?${AppNavigationArgKeys.PickedImageUri.name}=$pickedImageUri"
    }
}

fun <T> SavedStateHandle.getAppArg(appNavigationArgKeys: AppNavigationArgKeys): T? {
    return get<T>(appNavigationArgKeys.name)
}

enum class AppNavigationArgKeys {
    PickedImageUri
}