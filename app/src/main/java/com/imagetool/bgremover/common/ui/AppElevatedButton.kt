package com.imagetool.bgremover.common.ui

import androidx.compose.foundation.layout.PaddingValues
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
    backgroundColor: Color = WhiteText,
    onClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp),
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {

    val _borderColor = when {
        !enabled -> listOf(Color.LightGray, Color.LightGray)
        borderColor != null -> listOf(borderColor, borderColor)
        else -> listOf(
            Green1, Green1
        )

    }

    ElevatedButton(
        modifier = modifier,
        contentPadding = contentPadding,
        colors = ButtonDefaults.elevatedButtonColors()
            .copy(containerColor = if (enabled) backgroundColor else Color.LightGray.copy(alpha = 0.1f)),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = enabled).copy(
            width = 2.dp, brush = Brush.linearGradient(
                colors = _borderColor,
            )
        ),
        onClick = {
            onClick()

        }) {
        content()
    }
}