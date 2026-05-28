package com.tien.piholeconnect.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tien.piholeconnect.fixtures.Fixtures
import com.tien.piholeconnect.fixtures.respondJson
import dagger.hilt.android.testing.HiltAndroidTest
import java.io.IOException
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MultiPiHoleAndErrorE2ETest : E2ETestBase() {
    /** Each test seeds its own connection set, so no base seeding here. */
    override fun seed() {}

    @Test
    fun multiPiHoleSwitch_reloadsMetricsForSecondConnection() {
        // Seed both connections (primary selected). Differentiate metric responses by host.
        testState.seedConnection(
            id = "primary",
            connection = Fixtures.defaultConnection,
            selected = true,
        )
        testState.seedConnection(
            id = "secondary",
            connection = Fixtures.secondaryConnection,
            selected = false,
        )

        mockResponses.onGet("/api/stats/summary") { request ->
            if (request.url.host == "secondary.test")
                respondJson(Fixtures.secondaryMetricSummaryJson)
            else respondJson(Fixtures.metricSummaryJson)
        }

        launchApp().use {
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule.onAllNodes(hasText("84,254")).fetchSemanticsNodes().isNotEmpty()
            }

            // Open the options menu (top-right "More options" icon) — the connection
            // switcher only renders once the dropdown is expanded, and entries are
            // formatted "<name>@<host>".
            composeRule.onNodeWithContentDescription("More options").performClick()
            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule
                    .onAllNodes(hasText("Secondary Pi-hole", substring = true))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeRule.onNode(hasText("Secondary Pi-hole", substring = true)).performClick()

            // The secondary Pi-hole's metrics should now render.
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule.onAllNodes(hasText("12,345")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("12,345").assertIsDisplayed()
        }
    }

    @Test
    fun serverError_showsSnackbar() {
        testState.seedConnection(connection = Fixtures.defaultConnection)
        // Throw a network exception from the mock — the HTTP client doesn't enable
        // `expectSuccess`, and `PiHoleSerializer` has `ignoreUnknownKeys = true` with
        // every model field nullable, so a 500 response with a JSON body would silently
        // deserialise to nulls and never reach the error channel. An IOException is the
        // realistic "server unreachable / generic failure" path.
        mockResponses.onGet("/api/stats/summary") { throw IOException("simulated server error") }

        launchApp().use {
            // The snackbar text comes from R.string.error_pi_hole_connection_generic plus the
            // HTTP status. Either fragment is sufficient evidence.
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule
                    .onAllNodes(hasText("Please check your internet connection", substring = true))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
        }
    }
}
