package com.tien.piholeconnect.di

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.tien.piholeconnect.service.InAppPurchase
import dagger.Binds
import dagger.Module
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.testing.TestInstallIn
import javax.inject.Inject

@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [PiHoleConnectActivityModule::class],
)
abstract class TestPiHoleConnectActivityModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindInAppPurchase(noop: NoopInAppPurchase): InAppPurchase
}

class NoopInAppPurchase @Inject constructor() : InAppPurchase {
    override val billingClient: BillingClient
        get() =
            throw UnsupportedOperationException(
                "BillingClient is unavailable in e2e tests; TipJarScreen flows are not exercised"
            )

    override fun onBillingSetupFinished(billingResult: BillingResult) {}

    override fun onBillingServiceDisconnected() {}

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
    ) {}

    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>,
    ) {}

    override fun onConsumeResponse(billingResult: BillingResult, token: String) {}
}
