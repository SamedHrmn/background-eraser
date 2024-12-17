package com.imagetool.bgremover.features.subscription.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.theme.Typography

@Composable
fun ProCard() {
    Box(
        modifier = Modifier.background(
            color = Color.White,
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
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
            Text("PRO", style = Typography.titleSmall)
        }
    }
}