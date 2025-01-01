package com.imagetool.bgremover.features.subscription.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.features.subscription.SubscriptionViewModel
import com.imagetool.bgremover.theme.Typography

@Composable
fun GetPremiumButton(subscriptionViewModel: SubscriptionViewModel) {

    val showSubscriptionDialogState = remember {
        mutableStateOf(false)
    }

    val localResource = LocalResources.current

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
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified,
            )
            Spacer(Modifier.width(4.dp))
            AppText(
                localResource.getString(R.string.get_pro_button_text),
                style = Typography.titleSmall,
                fontSize = 14.sp,
            )
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