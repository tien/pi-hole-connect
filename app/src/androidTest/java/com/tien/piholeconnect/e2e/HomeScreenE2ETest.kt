package com.tien.piholeconnect.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tien.piholeconnect.fixtures.Fixtures
import com.tien.piholeconnect.fixtures.respondJson
import dagger.hilt.android.testing.HiltAndroidTest
import io.ktor.http.HttpMethod
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeScreenE2ETest : E2ETestBase() {
    @Test
    fun displaysMetricsHistoryAndBlockingEnabledFab() {
        launchApp().use {
            // Wait for the animated counter to settle at its final formatted value.
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule.onAllNodes(hasText("84,254")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("84,254").assertIsDisplayed()
            composeRule.onNodeWithText("14,732").assertIsDisplayed()
            composeRule.onNodeWithText("17.49%").assertIsDisplayed()
            composeRule.onNodeWithText("143,891").assertIsDisplayed()
            composeRule.onNodeWithText("Queries over last 24 hours").assertIsDisplayed()
            // FAB shows "Disable blocking" when ads blocking is currently ENABLED.
            composeRule.onNodeWithContentDescription("Disable blocking").assertIsDisplayed()
        }
    }

    @Test
    fun toggleBlocking_disablePiHole_callsDnsControlApi() {
        // Default GET /api/dns/blocking returns ENABLED, so the FAB starts as "Disable blocking".
        // We assert only the POST body, so we don't need to override the post-action GET.
        launchApp().use {
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule
                    .onAllNodesWithContentDescription("Disable blocking")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeRule.onNodeWithContentDescription("Disable blocking").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodes(hasText("5 MINUTES")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("5 MINUTES").performClick()

            // Wait until at least one POST to /api/dns/blocking has been recorded.
            composeRule.waitUntil(timeoutMillis = 5_000) {
                mockResponses.recorded.any {
                    it.method == HttpMethod.Post && it.path == "/api/dns/blocking"
                }
            }
            val postedBody =
                mockResponses.recorded
                    .first { it.method == HttpMethod.Post && it.path == "/api/dns/blocking" }
                    .bodyText
            assert("\"blocking\":false" in postedBody) {
                "expected POST body to disable blocking; got: $postedBody"
            }
            assert("\"timer\":300" in postedBody) {
                "expected timer=300 (5 minutes) in POST body; got: $postedBody"
            }
        }
    }

    @Test
    fun toggleBlocking_enablePiHole_callsDnsControlApiWithBlockingTrue() {
        // Start with blocking DISABLED so the FAB offers "Enable blocking".
        mockResponses.onGet("/api/dns/blocking") { respondJson(Fixtures.blockingDisabledJson) }
        launchApp().use {
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule
                    .onAllNodesWithContentDescription("Enable blocking")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeRule.onNodeWithContentDescription("Enable blocking").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodes(hasText("ENABLE")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("ENABLE").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                mockResponses.recorded.any {
                    it.method == HttpMethod.Post && it.path == "/api/dns/blocking"
                }
            }
            val postedBody =
                mockResponses.recorded
                    .first { it.method == HttpMethod.Post && it.path == "/api/dns/blocking" }
                    .bodyText
            // Regression guard: Pi-hole's schema wrongly marks `blocking` as `default: true`
            // (the server does not treat an omitted field as "enable"), so without encodeDefaults
            // the body serializes to `{}` and never re-enables blocking.
            assert("\"blocking\":true" in postedBody) {
                "expected POST body to enable blocking; got: $postedBody"
            }
        }
    }
}
