package com.imagetool.bgremover.ui.composables

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.imagetool.bgremover.ui.theme.BlackText
import com.imagetool.bgremover.ui.theme.Green1

@Composable
fun SelectableImageListView(
    modifier: Modifier = Modifier,
    images: List<Bitmap>,
    onItemsSelected: (items: List<Int>) -> Unit
) {
    val selectedImagesIndexState = remember {
        mutableStateListOf<Int>()
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier.height(300.dp)
    ) {
        itemsIndexed(images) { index, image ->

            Box {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .width(160.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Green1,
                                    Green1
                                )
                            ),
                            shape = RoundedCornerShape(size = 12.dp)
                        )
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .clickable {
                            if (!selectedImagesIndexState.contains(index)) {
                                selectedImagesIndexState.add(index)
                            } else {
                                selectedImagesIndexState.remove(index)
                            }
                            onItemsSelected(selectedImagesIndexState.toList())
                        },
                    model = image,
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                )
                Checkbox(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    colors = CheckboxDefaults.colors().copy(
                        checkedBoxColor = Green1,
                        checkedBorderColor = BlackText,
                        uncheckedBorderColor = BlackText,
                    ),
                    checked = selectedImagesIndexState.contains(index),
                    onCheckedChange = null,
                )
            }
        }
    }

}