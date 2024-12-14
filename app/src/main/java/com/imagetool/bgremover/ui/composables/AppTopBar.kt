package com.imagetool.bgremover.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imagetool.bgremover.R
import com.imagetool.bgremover.SubscriptionViewModel
import com.imagetool.bgremover.ui.theme.Diamond
import com.imagetool.bgremover.ui.theme.Green1
import com.imagetool.bgremover.ui.theme.Typography
import com.imagetool.bgremover.util.LocalResources


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(subscriptionViewModel: SubscriptionViewModel) {
    val dropdownExpandedState = remember {
        mutableStateOf(false)
    }

    val hasSubscribed = subscriptionViewModel.isSubscribed.collectAsState()

    val showPremiumButton = remember {
        derivedStateOf {
            !hasSubscribed.value
        }
    }

    TopAppBar(
        modifier = Modifier.background(
            Brush.linearGradient(
                listOf(Green1, Diamond)
            )
        ),

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Text(
                LocalResources.current.getString(R.string.app_name),
                style = Typography.titleLarge.copy( fontSize = if(showPremiumButton.value) 14.sp else 18.sp),
            )
        },
        actions = {
            if(showPremiumButton.value){
               GetPremiumButton(subscriptionViewModel = subscriptionViewModel)
            }
            IconButton(onClick = {
                dropdownExpandedState.value = !dropdownExpandedState.value
            }) {
                Box {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    AppTopBarDropdownMenu(
                        dropdownState = dropdownExpandedState,
                        onDismissRequest = {
                            dropdownExpandedState.value = false
                        })
                }
            }
        }
    )
}


