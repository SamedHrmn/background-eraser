package com.imagetool.bgremover.features.subscription

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SubscriptionViewModel : ViewModel(), PurchasesUpdatedListener {

    private val productsState = MutableStateFlow<ProductStates>(ProductStates.Initial)
    val products = productsState.asStateFlow()
    private var billingClient: BillingClient? = null

    private val _isSubscribed = MutableStateFlow(false)
    val isSubscribed = _isSubscribed.asStateFlow()

    fun launchBillingFlow(activity: Activity) {
        if (billingClient == null || productsState.value !is ProductStates.Loaded<*>) return

        val productsLoaded = productsState.value as ProductStates.Loaded<*>

        val paramDetail = when (val item = productsLoaded.products.first()) {
            is SkuDetails -> {
                item
            }

            is ProductDetails -> {
                item
            }

            else -> {
                null
            }
        }

        if (paramDetail == null) return


        val billingFlowParams = when (paramDetail) {
            is SkuDetails -> {
                BillingFlowParams.newBuilder().setSkuDetails(paramDetail).build()
            }

            is ProductDetails -> {

                val offerToken = paramDetail.subscriptionOfferDetails?.get(0)?.offerToken ?: return

                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            ProductDetailsParams.newBuilder()
                                .setOfferToken(offerToken)
                                .setProductDetails(paramDetail).build()
                        )
                    ).build()
            }

            else -> {
                null
            }
        }

        if (billingFlowParams == null) return

        billingClient!!.launchBillingFlow(activity, billingFlowParams)
    }


    fun initBillingClient(context: Context) {
        val client = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                client.startConnection(this)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    billingClient = client
                    querySubscriptionProduct()
                    queryActivePurchases()
                }
            }
        })
    }


    fun querySubscriptionProduct() {
        if (billingClient == null || billingClient?.isReady == false) return

        productsState.value = ProductStates.Loading



        try {
            when (billingClient!!.isFeatureSupported(FeatureType.PRODUCT_DETAILS).responseCode) {
                /// For legacy versions
                BillingResponseCode.FEATURE_NOT_SUPPORTED -> {
                    val skuParams = SkuDetailsParams.newBuilder().setSkusList(listOf(PRODUCT_ID))
                        .setType(BillingClient.SkuType.SUBS).build()
                    billingClient!!.querySkuDetailsAsync(skuParams) { _, skuDetailsList ->
                        if (skuDetailsList != null) {
                            productsState.value = ProductStates.Loaded<SkuDetails>(skuDetailsList)
                        }
                    }
                }

                BillingResponseCode.OK -> {
                    val queryProductDetailsParams =
                        QueryProductDetailsParams.newBuilder()
                            .setProductList(
                                listOf(
                                    QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId(PRODUCT_ID)
                                        .setProductType(BillingClient.ProductType.SUBS)
                                        .build()
                                )
                            )
                            .build()

                    billingClient!!.queryProductDetailsAsync(queryProductDetailsParams) { _, productDetails ->
                        productsState.value = ProductStates.Loaded<ProductDetails>(productDetails)
                    }
                }

                else -> {
                    productsState.value = ProductStates.Error("Cannot load subscriptions.")
                }
            }
        } catch (e: Exception) {
            productsState.value = ProductStates.Error("Cannot load subscriptions.")
        }
    }

    companion object {
        private const val PRODUCT_ID = "bgeraser_subs1"
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        if (p0.responseCode == BillingResponseCode.OK && p1 != null) {
            var hasActiveSubscription = false
            for (purchase in p1) {
                handlePurchase(purchase)
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    hasActiveSubscription = true
                }
            }
            _isSubscribed.value = hasActiveSubscription
        } else if (p0.responseCode == BillingResponseCode.USER_CANCELED) {
            // Canceled
        } else {
            // Error
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Purchase success
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        // Purchases acknowledgement
                    }
                }
            }
        }
    }

    fun queryActivePurchases() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                var hasActiveSubscription = false
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        handlePurchase(purchase)
                    }
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        hasActiveSubscription = true
                    }
                }
                _isSubscribed.value = hasActiveSubscription
            }
        }
    }
}

sealed interface ProductStates {
    data object Initial : ProductStates
    data object Loading : ProductStates
    data class Loaded<T>(val products: List<T>) : ProductStates
    data class Error(val message: String?) : ProductStates
}

