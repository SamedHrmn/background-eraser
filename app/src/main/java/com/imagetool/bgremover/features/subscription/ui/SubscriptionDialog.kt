package com.imagetool.bgremover.features.subscription.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.SkuDetails
import com.imagetool.bgremover.R
import com.imagetool.bgremover.common.provider.LocalResources
import com.imagetool.bgremover.common.ui.AppElevatedButton
import com.imagetool.bgremover.common.ui.AppText
import com.imagetool.bgremover.common.ui.dialogs.AppAlertDialog
import com.imagetool.bgremover.features.subscription.ProductStates
import com.imagetool.bgremover.features.subscription.SubscriptionViewModel
import com.imagetool.bgremover.theme.BlackText
import com.imagetool.bgremover.theme.Diamond
import com.imagetool.bgremover.theme.ErrorRed
import com.imagetool.bgremover.theme.Green1
import com.imagetool.bgremover.theme.ImagetoolbackgroundremoverTheme
import com.imagetool.bgremover.theme.Typography

@Composable
fun SubscriptionDialog(onDismiss: () -> Unit, subscriptionViewModel: SubscriptionViewModel) {

    val productDetails = subscriptionViewModel.products.collectAsState()
    val localActivity = LocalContext.current as Activity
    val localResource = LocalResources.current

    LaunchedEffect(Unit) {
        subscriptionViewModel.querySubscriptionProduct()
    }

    AppAlertDialog(
        modifier = Modifier.padding(horizontal = 12.dp),
        titleText = R.string.subs_dialog_title,
        onDismiss = {
            onDismiss()
        },
        content = {
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
                                    currencyCode = item.priceCurrencyCode,
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
                                        currencyCode = pricePhase.priceCurrencyCode,
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
        },
        confirmButton = {
            if (productDetails.value is ProductStates.Loaded<*>) {
                if ((productDetails.value as ProductStates.Loaded<*>).products.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppElevatedButton(borderColor = ErrorRed, onClick = {
                            onDismiss()
                        }) {
                            AppText(localResource.getString(R.string.subs_dialog_cancel_button_text))
                        }
                        AppElevatedButton(onClick = {
                            subscriptionViewModel.launchBillingFlow(activity = localActivity)
                        }) {
                            AppText(localResource.getString(R.string.subs_dialog_confirm_button_text))
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AppElevatedButton(borderColor = ErrorRed, onClick = {
                            onDismiss()
                        }) {
                            AppText(localResource.getString(R.string.subs_dialog_cancel_button_text))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DialogSubsProductCard(
    title: String,
    subsPeriodText: String,
    price: String,
    currencyCode: String,
    features: List<String>
) {
    Column {
        AppText(
            title,
            style = Typography.bodyLarge.copy(
                fontSize = 16.sp,
                color = BlackText
            )
        )
        Spacer(Modifier.height(4.dp))
        AppText(
            "$subsPeriodText ${price}${currencyCode}",
            style = Typography.bodyLarge.copy(
                fontSize = 16.sp,
                color = BlackText
            )
        )
        Spacer(Modifier.height(8.dp))
        features.map {
            AppText(
                "- $it",
                style = Typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = BlackText
                )
            )
        }
    }
}

@Preview(
    showSystemUi = true, showBackground = true,
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
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
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
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Green1,
                                        Diamond
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
                            currencyCode = "$",
                            features = listOf("Feature text1", "Feature text2")
                        )
                    }
                }
            )
        }
    }
}
