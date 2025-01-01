package com.imagetool.bgremover.features.erase_by_hand.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults.Thumb
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EraseByHandBrushSizeSlider(
    defaultValue: Float = 0f,
    onValueChange: (Float) -> Unit
) {
    val sliderPositionState = remember {
        mutableFloatStateOf(defaultValue)
    }

    val interactionSource = remember { MutableInteractionSource() }

    val localResource = LocalResources.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        AppText(
            localResource.getString(R.string.erasebyhand_brush_size_label),
            modifier = Modifier.padding(top = 10.dp),
            style = Typography.titleSmall
        )
        Column {
            Slider(
                value = sliderPositionState.floatValue,
                modifier = Modifier.height(40.dp),
                interactionSource = interactionSource,
                thumb = {
                    Label(
                        label = {
                            PlainTooltip(
                                modifier = Modifier
                                    .sizeIn(45.dp, 25.dp)
                                    .wrapContentWidth()
                            ) {
                                AppText("%.2f".format(sliderPositionState.floatValue))
                            }
                        },
                        interactionSource = interactionSource,
                    ) {
                        Thumb(interactionSource = interactionSource)
                    }
                },
                valueRange = 10f..50f,
                onValueChange = {
                    sliderPositionState.floatValue = it
                    onValueChange(it)
                }
            )
            Box(Modifier.wrapContentHeight()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    listOf("10", "20", "30", "40", "50").map {
                        AppText(it, style = Typography.titleSmall.copy(fontSize = 10.sp))
                    }
                }
            }

        }
    }
}