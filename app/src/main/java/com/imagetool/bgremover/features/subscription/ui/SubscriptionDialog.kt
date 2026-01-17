package com.imagetool.bgremover.features.subscription.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.SkuDetails
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.features.subscription.ProductStates
import com.imagetool.bgremover.features.subscription.SubscriptionViewModel
import com.imagetool.bgremover.theme.BlackText
import com.imagetool.bgremover.theme.Diamond
import com.imagetool.bgremover.theme.ErrorRed
import com.imagetool.bgremover.theme.Green1
import com.imagetool.bgremover.theme.ImagetoolbackgroundremoverTheme
import com.imagetool.bgremover.theme.Typography
import org.koin.androidx.compose.koinViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun SubscriptionDialog(
    subscriptionViewModel: SubscriptionViewModel = koinViewModel(),
    onDismiss: () -> Unit,
) {
    val localActivity = LocalContext.current as Activity
    val localResource = LocalResources.current
    val productDetails = subscriptionViewModel.products.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .graphicsLayer { clip = false },
            contentAlignment = Alignment.TopCenter
        ) {

            // Floating Crown
            Image(
                painter = painterResource(R.drawable.premium_crown),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .offset(y = (-120).dp)
                    .zIndex(2f)
            )

            // Dialog Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .padding(top = 80.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    // Title
                    AppText(
                        text = localResource.getString(R.string.subs_dialog_title),
                        style = Typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    // Content
                    when (val productsState = productDetails.value) {
                        is ProductStates.Initial -> {}
                        is ProductStates.Loading -> {
                            CircularProgressIndicator()
                        }

                        is ProductStates.Error -> {
                            AppText(localResource.getString(R.string.subs_dialog_products_query_error))
                        }

                        is ProductStates.Loaded<*> -> {

                            if (productsState.products.isEmpty()) {
                                AppText(localResource.getString(R.string.subs_dialog_no_product_text))
                            } else {
                                when (val item = productsState.products.first()) {
                                    is SkuDetails -> {
                                        DialogSubsProductCard(
                                            title = item.title,
                                            subsPeriodText = localResource.getString(R.string.subs_period_weekly_text),
                                            price = item.price,
                                            features = listOf(
                                                localResource.getString(R.string.subs_features_text1),
                                                localResource.getString(R.string.subs_features_text2)
                                            )
                                        )
                                    }

                                    is ProductDetails -> {
                                        val offerDetail = item.subscriptionOfferDetails?.first()
                                        val pricePhase =
                                            offerDetail?.pricingPhases?.pricingPhaseList?.first()

                                        if (offerDetail != null && pricePhase != null) {
                                            DialogSubsProductCard(
                                                title = item.name,
                                                subsPeriodText = localResource.getString(R.string.subs_period_weekly_text),
                                                price = pricePhase.formattedPrice,
                                                features = listOf(
                                                    localResource.getString(R.string.subs_features_text1),
                                                    localResource.getString(R.string.subs_features_text2)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }


                    Spacer(Modifier.height(24.dp))

                    // Buttons
                    when (val state = productDetails.value) {
                        is ProductStates.Loaded<*> -> {
                            if (state.products.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    AppElevatedButton(
                                        modifier = Modifier
                                            .height(56.dp)
                                            .weight(1f),
                                        borderColor = ErrorRed,
                                        onClick = onDismiss
                                    ) {
                                        AppText(
                                            localResource.getString(
                                                R.string.subs_dialog_cancel_button_text
                                            ),
                                            style = Typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            ),
                                        )
                                    }

                                    AppElevatedButton(
                                        modifier = Modifier

                                            .height(56.dp)
                                            .weight(1f),
                                        backgroundColor = Green1,
                                        onClick = {
                                            subscriptionViewModel.launchBillingFlow(
                                                activity = localActivity
                                            )
                                        }
                                    ) {
                                        AppText(
                                            localResource.getString(
                                                R.string.subs_dialog_confirm_button_text

                                            ),
                                            style = Typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = Color.White,
                                            ),
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            AppElevatedButton(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = ErrorRed,
                                onClick = onDismiss
                            ) {
                                AppText(
                                    localResource.getString(
                                        R.string.subs_dialog_cancel_button_text
                                    ),
                                    style = Typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DialogSubsProductCard(
    title: String,
    subsPeriodText: String,
    price: String,
    features: List<String>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),

        ) {
        AppText(
            title, style = Typography.bodyLarge.copy(
                fontSize = 18.sp, color = BlackText
            )
        )
        Spacer(Modifier.height(12.dp))
        AppText(
            "$subsPeriodText $price", style = Typography.bodyLarge.copy(
                fontSize = 20.sp, color = BlackText
            )
        )
        Spacer(Modifier.height(8.dp))
        features.map {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = Color.White)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                AppText(
                    "- $it", style = Typography.bodyLarge.copy(
                        fontSize = 18.sp, color = BlackText
                    )
                )
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:pixel_4a"
)
@Composable
fun PreviewSubscriptionDialog() {

    val showNoProductState = false

    ImagetoolbackgroundremoverTheme {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            AlertDialog(
                modifier = Modifier.padding(horizontal = 16.dp),
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                ),
                title = {
                    AppText("Be Pro, Remove Ads!")
                },
                confirmButton = {
                    if (!showNoProductState) {

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppElevatedButton(borderColor = ErrorRed, onClick = {

                            }) {
                                AppText("Cancel")
                            }
                            AppElevatedButton(onClick = {

                            }) {
                                AppText("Show Details")
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            AppElevatedButton(borderColor = ErrorRed, onClick = {

                            }) {
                                AppText("Cancel")
                            }
                        }
                    }

                },
                onDismissRequest = {},
                text = {
                    Box(
                        modifier = Modifier
                            .border(
                                width = 2.dp, brush = Brush.linearGradient(
                                    colors = listOf(
                                        Green1, Diamond
                                    )
                                ), shape = RoundedCornerShape(12.dp)
                            )
                            .fillMaxWidth()
                            .padding(12.dp)


                    ) {
                        DialogSubsProductCard(
                            title = "Item title",
                            subsPeriodText = "Weekly",
                            price = "0.99",
                            features = listOf("Feature text1", "Feature text2")
                        )
                    }
                })
        }
    }
}
