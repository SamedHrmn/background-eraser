package com.imagetool.bgremover.ui.composables.dialogs

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
import androidx.compose.material3.Text
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
import com.imagetool.bgremover.ProductStates
import com.imagetool.bgremover.R
import com.imagetool.bgremover.SubscriptionViewModel
import com.imagetool.bgremover.ui.composables.AppElevatedButton
import com.imagetool.bgremover.ui.theme.BlackText
import com.imagetool.bgremover.ui.theme.Diamond
import com.imagetool.bgremover.ui.theme.ErrorRed
import com.imagetool.bgremover.ui.theme.Green1
import com.imagetool.bgremover.ui.theme.ImagetoolbackgroundremoverTheme
import com.imagetool.bgremover.ui.theme.Typography
import com.imagetool.bgremover.util.LocalResources

@Composable
fun SubscriptionDialog(onDismiss: () -> Unit, subscriptionViewModel: SubscriptionViewModel) {

    val productDetails = subscriptionViewModel.products.collectAsState()
    val localActivity = LocalContext.current as Activity
    val localResource = LocalResources.current

    LaunchedEffect(Unit) {
        subscriptionViewModel.querySubscriptionProduct()
    }

    AppAlertDialog(
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
                    Text(localResource.getString(R.string.subs_dialog_products_query_error))
                }

                is ProductStates.Loaded<*> -> {

                    if (productsState.products.isEmpty()) {
                        Text(localResource.getString(R.string.subs_dialog_no_product_text))
                    } else {
                        when (val item = productsState.products.first()) {
                            is SkuDetails -> {
                                DialogSubsProductCard(
                                    title = item.title,
                                    subsPeriodText = localResource.getString(R.string.subs_period_weekly_text),
                                    price = item.price,
                                    currencyCode = item.priceCurrencyCode,
                                    featureText = localResource.getString(R.string.subs_features_text1)
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
                                        featureText = localResource.getString(R.string.subs_features_text1)
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
                            Text(localResource.getString(R.string.subs_dialog_cancel_button_text))
                        }
                        AppElevatedButton(onClick = {
                            subscriptionViewModel.launchBillingFlow(activity = localActivity)
                        }) {
                            Text(localResource.getString(R.string.subs_dialog_confirm_button_text))
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AppElevatedButton(borderColor = ErrorRed, onClick = {
                            onDismiss()
                        }) {
                            Text(localResource.getString(R.string.subs_dialog_cancel_button_text))
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
    featureText: String
) {
    Column {
        Text(
            title,
            style = Typography.bodyLarge.copy(
                fontSize = 19.sp,
                color = BlackText
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "$subsPeriodText ${price}${currencyCode}",
            style = Typography.bodyLarge.copy(
                fontSize = 16.sp,
                color = BlackText
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "- $featureText",
            style = Typography.bodyLarge.copy(
                fontSize = 16.sp,
                color = BlackText
            )
        )
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
                    Text("Be Pro, Remove Ads!")
                },
                confirmButton = {
                    if (!showNoProductState) {

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppElevatedButton(borderColor = ErrorRed, onClick = {

                            }) {
                                Text("Cancel")
                            }
                            AppElevatedButton(onClick = {

                            }) {
                                Text("Show Details")
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppElevatedButton(borderColor = ErrorRed, onClick = {

                            }) {
                                Text("Cancel")
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
                            featureText = "No Ads, no headache"
                        )
                    }
                }
            )
        }
    }
}
