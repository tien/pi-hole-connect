package com.tien.piholeconnect.service

import androidx.lifecycle.DefaultLifecycleObserver
import com.android.billingclient.api.*

interface InAppPurchase :
    DefaultLifecycleObserver,
    BillingClientStateListener,
    PurchasesUpdatedListener,
    PurchasesResponseListener,
    ConsumeResponseListener {
    val billingClient: BillingClient
}
