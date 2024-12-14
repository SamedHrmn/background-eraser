package com.imagetool.bgremover.features.subscription.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.imagetool.bgremover.R
import com.imagetool.bgremover.features.subscription.SubscriptionViewModel
import com.imagetool.bgremover.theme.Typography
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton

@Composable
fun GetPremiumButton(subscriptionViewModel: SubscriptionViewModel) {

    val showSubscriptionDialogState = remember {
        mutableStateOf(false)
    }

    AppElevatedButton(
        modifier = Modifier.padding(horizontal = 4.dp),
        onClick = {
            showSubscriptionDialogState.value = true
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                ResourcesCompat.getDrawable(
                    LocalResources.current,
                    R.drawable.premium_icon,
                    null
                )!!.toBitmap().asImageBitmap(),

                "",
                modifier = Modifier.size(22.dp),
                tint = Color.Unspecified,
            )
            Text("Remove Ads", style = Typography.titleLarge.copy(fontSize = 13.sp))
        }
    }

    if (showSubscriptionDialogState.value) {
        SubscriptionDialog(
            onDismiss = {
                showSubscriptionDialogState.value = false
            },
            subscriptionViewModel = subscriptionViewModel,
        )
    }
}