package com.imagetool.bgremover.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.imagetool.bgremover.R
import com.imagetool.bgremover.features.subscription.SubscriptionViewModel
import com.imagetool.bgremover.theme.Diamond
import com.imagetool.bgremover.theme.Green1
import com.imagetool.bgremover.theme.Typography
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.features.subscription.ui.GetPremiumButton
import com.imagetool.bgremover.features.subscription.ui.ProCard
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(subscriptionViewModel: SubscriptionViewModel = koinViewModel()) {
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
                style = Typography.titleLarge.copy(fontSize = if (showPremiumButton.value) 14.sp else 18.sp),
            )
        },
        actions = {
            if (showPremiumButton.value) {
                GetPremiumButton(subscriptionViewModel = subscriptionViewModel)
            } else {
                ProCard()
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


