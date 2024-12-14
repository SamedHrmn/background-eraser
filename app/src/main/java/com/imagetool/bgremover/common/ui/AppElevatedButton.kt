package com.imagetool.bgremover.common.ui

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.imagetool.bgremover.theme.Green1
import com.imagetool.bgremover.theme.WhiteText

@Composable
fun AppElevatedButton(
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ElevatedButton(
        modifier = modifier,
        colors = ButtonDefaults.elevatedButtonColors().copy(containerColor = WhiteText),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            width = 2.dp, brush = Brush.linearGradient(
                colors = if (borderColor != null) listOf(borderColor, borderColor) else listOf(
                    Green1, Green1
                )
            )
        ),
        onClick = {
            onClick()

        }) {
        content()
    }
}