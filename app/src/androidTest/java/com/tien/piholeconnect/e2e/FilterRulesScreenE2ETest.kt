package com.tien.piholeconnect.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tien.piholeconnect.fixtures.Fixtures
import com.tien.piholeconnect.fixtures.respondJson
import com.tien.piholeconnect.repository.models.GetDomainsInner
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
        val added =
            GetDomainsInner(
                id = 99,
                domain = newDomain,
                type = GetDomainsInner.Type.DENY,
                kind = GetDomainsInner.Kind.EXACT,
                enabled = true,
                dateAdded = 1700000000,
            )
        // Serve the base list initially; once the addDomain POST is recorded, subsequent GETs
        // return the augmented list. Without this gate, the final row-displayed assertion would
        // pass even if addDomain() / doRefresh() never ran — the seeded response would already
        // contain the new row on first render.
        mockResponses.onGet("/api/domains") {
            val posted =
                mockResponses.recorded.any {
                    it.method == HttpMethod.Post && it.path.startsWith("/api/domains/")
                }
            respondJson(
                if (posted) Fixtures.filterRulesJsonIncluding(added) else Fixtures.filterRulesJson
            )
        }

        launchApp().use {
            navigateToFilterRules()
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule
                    .onAllNodes(hasText("ads.example.com"))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            composeRule.onNodeWithContentDescription("Add filter rule").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodes(hasText("Add rule")).fetchSemanticsNodes().isNotEmpty()
            }

            composeRule.onAllNodes(hasSetTextAction()).onFirst().performTextInput(newDomain)
            composeRule.onNodeWithText("ADD").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                mockResponses.recorded.any {
                    it.method == HttpMethod.Post && it.path.startsWith("/api/domains/")
                }
            }
            // The refresh appends the new row at the bottom of the LazyColumn; on
            // shorter emulator screens it sits below the fold, which keeps it out
            // of the semantics tree. Scroll the list to surface the row before
            // asserting — performScrollToNode also waits for the refreshed list
            // to compose, so this doubles as the "data has arrived" wait.
            composeRule.waitUntil(timeoutMillis = 10_000) {
                try {
                    composeRule
                        .onAllNodes(hasScrollAction())
                        .onLast()
                        .performScrollToNode(hasText(newDomain))
                    true
                } catch (_: Throwable) {
                    false
                }
            }
            composeRule.onNodeWithText(newDomain).assertIsDisplayed()
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
