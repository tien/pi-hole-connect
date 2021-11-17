package com.tien.piholeconnect.service

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InAppPurchaseImpl @Inject constructor(
    @ApplicationContext private val context: Context
) :
    InAppPurchase {
    override lateinit var billingClient: BillingClient
        private set

    override fun onCreate(owner: LifecycleOwner) {
        billingClient =
            BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build()

        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        consumeOutstandingPurchases()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingResponseCode.OK) {
            consumeOutstandingPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingResponseCode.OK) {
            purchases?.apply(this::handlePurchases)
        }
    }

    override fun onConsumeResponse(billingResult: BillingResult, token: String) {
    }

    private fun consumeOutstandingPurchases() {
        billingClient.queryPurchasesAsync(SkuType.INAPP) { _, purchases ->
            handlePurchases(purchases)
        }
    }

    private fun handlePurchases(purchases: Iterable<Purchase>) {
        purchases.map { purchase ->
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build().let {
                    billingClient.consumeAsync(it, this)
                }
        }
    }
}
