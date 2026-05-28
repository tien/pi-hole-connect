package com.tien.piholeconnect.rule

import androidx.datastore.core.DataStore
import androidx.test.platform.app.InstrumentationRegistry
import com.tien.piholeconnect.fixtures.MockResponseRegistry
import com.tien.piholeconnect.fixtures.installDefaults
import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.PiHoleConnections
import com.tien.piholeconnect.model.UserPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Per-test cleanup + mock-response baseline.
 *
 * Install this rule on a `@HiltAndroidTest` class **after** `HiltAndroidRule` (e.g. order 1).
 * Before each test runs we:
 * 1. Delete the persisted DataStore files so the app starts from a clean slate.
 * 2. Reset and re-install the default canned responses on [MockResponseRegistry].
 *
 * Singletons are retrieved through a Hilt [EntryPoint] rather than `@Inject` fields, because
 * `HiltAndroidRule.inject()` only injects the test class — rule fields stay uninitialised.
 *
 * The [seedConnection] helper writes a single Pi-hole into the connections DataStore (selected by
 * default) and is meant to be called from `@Before` after [installDefaults].
 */
class TestStateRule : TestWatcher() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface TestStateEntryPoint {
        fun mockResponses(): MockResponseRegistry

        fun connectionsDataStore(): DataStore<PiHoleConnections>

        fun userPreferencesDataStore(): DataStore<UserPreferences>
    }

    private val entryPoint: TestStateEntryPoint
        get() =
            EntryPoints.get(
                InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
                TestStateEntryPoint::class.java,
            )

    val mockResponses: MockResponseRegistry
        get() = entryPoint.mockResponses()

    private val connectionsDataStore: DataStore<PiHoleConnections>
        get() = entryPoint.connectionsDataStore()

    private val userPreferencesDataStore: DataStore<UserPreferences>
        get() = entryPoint.userPreferencesDataStore()

    override fun starting(description: Description) {
        super.starting(description)
        // Reset DataStore contents *through* the live instance — deleting the underlying file
        // while the singleton DataStore is still active would leave it in an inconsistent state
        // and cause "multiple DataStores active for the same file" on the next test.
        runBlocking {
            connectionsDataStore.updateData { PiHoleConnections.getDefaultInstance() }
            userPreferencesDataStore.updateData { UserPreferences.getDefaultInstance() }
        }
    }

    /** Call from `@Before` (after `hiltRule.inject()`) to wire up defaults. */
    fun installDefaults() {
        mockResponses.reset()
        mockResponses.installDefaults()
    }

    /** Persist a single Pi-hole connection into the test DataStore. */
    fun seedConnection(
        id: String = "primary",
        connection: PiHoleConnection,
        selected: Boolean = true,
    ) = runBlocking {
        connectionsDataStore.updateData { current ->
            val builder = current.toBuilder().putConnections(id, connection)
            if (selected) builder.setSelectedConnectionId(id)
            builder.build()
        }
    }
}
