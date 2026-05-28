package com.tien.piholeconnect.e2e

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import com.tien.piholeconnect.MainActivity
import com.tien.piholeconnect.fixtures.Fixtures
import com.tien.piholeconnect.fixtures.MockResponseRegistry
import com.tien.piholeconnect.rule.TestStateRule
import dagger.hilt.android.testing.HiltAndroidRule
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule

/**
 * Shared scaffolding for e2e tests.
 *
 * Tests extend this class and call [launchApp] (typically from a `@Test` body) once the DataStore
 * is in its desired starting state. The activity is launched via [ActivityScenario] *after*
 * `@Before` runs so we can seed the DataStore — `createAndroidComposeRule<MainActivity>()` would
 * launch the activity inside the rule's constructor, before any seeding could happen.
 *
 * The base `@Before` injects, installs the canned response defaults, and delegates to [seed] for
 * any per-class DataStore seeding. Subclasses override [seed] instead of repeating boilerplate.
 */
abstract class E2ETestBase {
    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val testState = TestStateRule()
    @get:Rule(order = 2) val composeRule: ComposeTestRule = createEmptyComposeRule()

    @Inject lateinit var mockResponses: MockResponseRegistry

    @Before
    fun baseSetUp() {
        hilt.inject()
        testState.installDefaults()
        seed()
    }

    /**
     * Override to populate the DataStore for the test class. Default seeds a single
     * [Fixtures.defaultConnection] selected as primary — the common case. Tests that start with an
     * empty DataStore (e.g. setup-flow) or seed multiple connections override this.
     */
    protected open fun seed() {
        testState.seedConnection(connection = Fixtures.defaultConnection)
    }

    protected fun launchApp(): ActivityScenario<MainActivity> =
        ActivityScenario.launch(MainActivity::class.java)
}
