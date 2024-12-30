package com.imagetool.bgremover.features.feedback.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.features.feedback.FeedbackViewModel
import com.imagetool.bgremover.theme.BlackText
import com.imagetool.bgremover.theme.Green1
import com.imagetool.bgremover.theme.Typography
import com.imagetool.bgremover.theme.WhiteText
import com.imagetool.bgremover.util.showToast
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendFeedbackDialog(
    feedbackViewModel: FeedbackViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val text = remember { mutableStateOf("") }
    val localContext = LocalContext.current
    val localResource = LocalResources.current
    val showSendButtonState = remember {
        derivedStateOf {
            text.value.isNotEmpty()
        }
    }


    BasicAlertDialog(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color = Color.White)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true),
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppText(localResource.getString(R.string.feedback_dialog_title))
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    IconButton(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(8.dp)
                            .border(
                                width = 2.dp, brush = Brush.linearGradient(
                                    colors = listOf(
                                        Green1, Green1
                                    )
                                ), shape = CircleShape
                            ),
                        onClick = {
                            onDismiss()
                        }) {
                        Icon(Icons.Default.Close, contentDescription = "")
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            TextField(
                modifier = Modifier
                    .border(
                        width = 2.dp, brush = Brush.linearGradient(
                            colors = listOf(
                                Green1, Green1
                            )
                        ), shape = RoundedCornerShape(12.dp)
                    )
                    .height(150.dp),
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = WhiteText,
                    disabledContainerColor = WhiteText,
                    unfocusedContainerColor = WhiteText,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = BlackText,
                ),
                shape = RoundedCornerShape(12.dp),
                value = text.value,
                label = {
                    AppText(
                        localResource.getString(R.string.feedback_dialog_textfield_hint),
                        style = Typography.titleLarge.copy(Color.Gray)
                    )
                },
                maxLines = 5,
                minLines = 5,
                onValueChange = {
                    text.value = it
                },
            )
            Spacer(Modifier.height(16.dp))
            AnimatedVisibility(
                visible = showSendButtonState.value,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                AppElevatedButton(
                    modifier = Modifier.height(50.dp),
                    onClick = {
                        feedbackViewModel.sendFeedback(
                            text = text.value,
                            context = localContext,
                            onError = {
                                localContext.showToast(text = localResource.getString(R.string.feedback_dialog_error_text))
                            })
                    }
                ) {
                    AppText(
                        text = localResource.getString(R.string.feedback_dialog_send_feedback_button_text),
                    )
                }
            }
        }
    }

}