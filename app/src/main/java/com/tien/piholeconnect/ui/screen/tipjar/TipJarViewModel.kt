package com.tien.piholeconnect.ui.screen.tipjar

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.querySkuDetails
import com.tien.piholeconnect.model.RefreshableViewModel
import com.tien.piholeconnect.service.InAppPurchase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TipJarViewModel @Inject constructor(
    private val inAppPurchase: InAppPurchase
) :
    RefreshableViewModel() {
    private val skuLists =
        listOf("coffee", "beer", "dinner").map { "com.tien.piholeconnect.tip.$it" }

    var tipOptions by mutableStateOf(listOf<SkuDetails>())
        private set

    override fun CoroutineScope.queueRefresh() = launch {
        val params = SkuDetailsParams.newBuilder().setType(INAPP).setSkusList(skuLists).build()
        inAppPurchase.billingClient.querySkuDetails(params).skuDetailsList?.also { options ->
            tipOptions = options.sortedBy { it.priceAmountMicros }
        }
    }

    fun launchBillingFlow(activity: Activity, skuDetails: SkuDetails) {
        val params = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
        inAppPurchase.billingClient.launchBillingFlow(activity, params)
    }
}