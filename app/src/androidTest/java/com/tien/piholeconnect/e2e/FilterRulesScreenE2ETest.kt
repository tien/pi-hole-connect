package com.tien.piholeconnect.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import io.ktor.http.HttpMethod
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FilterRulesScreenE2ETest : E2ETestBase() {
    @Test
    fun navigateToFilterRules_displaysDenyRules() {
        launchApp().use {
            navigateToFilterRules()
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule
                    .onAllNodes(hasText("ads.example.com"))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeRule.onNodeWithText("ads.example.com").assertIsDisplayed()
            composeRule.onNodeWithText("tracking.analytics.com").assertIsDisplayed()
            composeRule.onNodeWithText("telemetry.microsoft.com").assertIsDisplayed()
        }
    }

    @Test
    fun addRule_callsAddDomainEndpoint_andRefreshes() {
        val newDomain = "freshly.added.test"

        launchApp().use {
            navigateToFilterRules()
            composeRule.waitUntil(timeoutMillis = 30_000) {
                composeRule
                    .onAllNodes(hasText("ads.example.com"))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            composeRule.onNodeWithContentDescription("Add filter rule").performClick()

            composeRule.waitUntil(timeoutMillis = 30_000) {
                composeRule.onAllNodes(hasText("Add rule")).fetchSemanticsNodes().isNotEmpty()
            }

            composeRule.onAllNodes(hasSetTextAction()).onFirst().performTextInput(newDomain)
            composeRule.onNodeWithText("ADD").performClick()

            // Verify the POST was made with the typed domain in the body.
            composeRule.waitUntil(timeoutMillis = 30_000) {
                mockResponses.recorded.any {
                    it.method == HttpMethod.Post &&
                        it.path.startsWith("/api/domains/") &&
                        newDomain in it.bodyText
                }
            }
            // Verify a follow-up GET /api/domains landed *after* the POST — that's
            // the doRefresh() the screen runs on success. We assert on the recorded
            // requests rather than the rendered LazyColumn because the new row sits
            // outside the CI emulator's viewport (off-screen, not composed) so the
            // semantics-tree check is flaky there. The follow-up GET is what
            // "andRefreshes" actually means at the contract level.
            val firstIndexAfterPost =
                mockResponses.recorded.indexOfFirst {
                    it.method == HttpMethod.Post && it.path.startsWith("/api/domains/")
                } + 1
            composeRule.waitUntil(timeoutMillis = 30_000) {
                mockResponses.recorded.drop(firstIndexAfterPost).any {
                    it.method == HttpMethod.Get && it.path == "/api/domains"
                }
            }
        }
    }

    private fun navigateToFilterRules() {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule
                .onAllNodes(hasText("Queries over last 24 hours"))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeRule.onNodeWithText("Filters").performClick()
    }
}
