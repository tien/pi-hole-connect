package com.tien.piholeconnect.ui.screen.tipjar

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import com.tien.piholeconnect.model.RefreshableViewModel
import com.tien.piholeconnect.service.InAppPurchase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TipJarViewModel @Inject constructor(
    private val inAppPurchase: InAppPurchase
) : RefreshableViewModel() {
    private val productList =
        listOf("coin", "coffee", "beer", "dinner").map { "com.tien.piholeconnect.tip.$it" }.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP).setProductId(it).build()
        }

    var tipOptions by mutableStateOf(listOf<ProductDetails>())
        private set

    override suspend fun queueRefresh() {
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()
        tipOptions =
            inAppPurchase.billingClient.queryProductDetails(params).productDetailsList?.sortedBy { it.oneTimePurchaseOfferDetails?.priceAmountMicros }
                ?: listOf()
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val params = BillingFlowParams.newBuilder().setProductDetailsParamsList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails).build()
            )
        ).build()
        inAppPurchase.billingClient.launchBillingFlow(activity, params)
    }
}
