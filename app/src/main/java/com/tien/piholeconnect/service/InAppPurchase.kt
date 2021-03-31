package com.tien.piholeconnect.service

import androidx.lifecycle.LifecycleObserver
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener

interface InAppPurchase : LifecycleObserver, BillingClientStateListener, PurchasesUpdatedListener,
    ConsumeResponseListener {
    val billingClient: BillingClient
}