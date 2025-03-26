package com.tien.piholeconnect.ui.screen.tipjar

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import com.tien.piholeconnect.viewmodel.BaseViewModel
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.service.InAppPurchase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow

@HiltViewModel
class TipJarViewModel
@Inject
constructor(
    private val inAppPurchase: InAppPurchase,
    userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel(userPreferencesRepository) {
    private val productList =
        listOf("coin", "coffee", "beer", "dinner")
            .map { "com.tien.piholeconnect.tip.$it" }
            .map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .setProductId(it)
                    .build()
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tipOptions =
        flow {
                emit(
                    inAppPurchase.billingClient
                        .queryProductDetails(
                            QueryProductDetailsParams.newBuilder()
                                .setProductList(productList)
                                .build()
                        )
                        .productDetailsList
                        ?.sortedBy { it.oneTimePurchaseOfferDetails?.priceAmountMicros } ?: listOf()
                )
            }
            .asViewFlowState()

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val params =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                )
                .build()
        inAppPurchase.billingClient.launchBillingFlow(activity, params)
    }
}
