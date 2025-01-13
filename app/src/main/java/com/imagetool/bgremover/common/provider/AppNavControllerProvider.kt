package com.imagetool.bgremover.common.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No resource provided")
}

@Composable
fun AppNavControllerProvider( content: @Composable () -> Unit){
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        content()
    }
}