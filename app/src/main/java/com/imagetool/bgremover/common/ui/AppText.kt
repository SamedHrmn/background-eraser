package com.imagetool.bgremover.common.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.imagetool.bgremover.theme.Typography

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Typography.titleSmall,
    ) {

    val textStyle = remember(style) {
        mutableStateOf(style)
    }

    Text(
        text = text, style = textStyle.value,

        modifier = modifier,
        onTextLayout = { result ->
            if (result.didOverflowWidth || result.didOverflowHeight) {
                textStyle.value = textStyle.value.copy(fontSize = textStyle.value.fontSize * 0.96)
            }
        },
    )
}