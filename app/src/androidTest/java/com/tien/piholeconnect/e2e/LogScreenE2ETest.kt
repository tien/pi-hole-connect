package com.tien.piholeconnect.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LogScreenE2ETest : E2ETestBase() {
    @Test
    fun navigateToLog_displaysQueryHistory() {
        launchApp().use {
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule
                    .onAllNodes(hasText("Queries over last 24 hours"))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeRule.onNodeWithText("Log").performClick()

            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodes(hasText("google.com")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("google.com").assertIsDisplayed()
            composeRule.onNodeWithText("ads.doubleclick.net").assertIsDisplayed()
            composeRule.onNodeWithText("api.github.com").assertIsDisplayed()
        }
    }

    @Test
    fun filterByDomain_narrowsList() {
        launchApp().use {
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule
                    .onAllNodes(hasText("Queries over last 24 hours"))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeRule.onNodeWithText("Log").performClick()
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodes(hasText("api.github.com")).fetchSemanticsNodes().isNotEmpty()
            }

            // The Log screen exposes a Material3 SearchBar — the only editable field on
            // the screen. Type a substring that should narrow results to just the
            // doubleclick row, then submit so the SearchBar collapses; otherwise the
            // expanded SearchBar overlay renders a second copy of the LogList and
            // every matching row appears twice.
            composeRule.onAllNodes(hasSetTextAction()).onFirst().performTextInput("doubleclick")
            composeRule.onAllNodes(hasSetTextAction()).onFirst().performImeAction()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodes(hasText("api.github.com")).fetchSemanticsNodes().isEmpty()
            }
            composeRule.onNodeWithText("ads.doubleclick.net").assertIsDisplayed()
        }
    }
}
