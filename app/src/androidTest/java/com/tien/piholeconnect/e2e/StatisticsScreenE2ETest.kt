package com.tien.piholeconnect.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StatisticsScreenE2ETest : E2ETestBase() {
    @Test
    fun navigateToStatistics_displaysTopPermittedBlockedClients() {
        launchApp().use {
            // Wait for Home to be ready before navigating, so the NavController is wired up.
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule
                    .onAllNodes(hasText("Queries over last 24 hours"))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            composeRule.onNodeWithText("Statistics").performClick()

            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule
                    .onAllNodes(hasText("debug.opendns.com"))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeRule.onNodeWithText("debug.opendns.com").assertIsDisplayed()
            // The "Top Blocked" and "Top Clients" cards sit below the fold on a phone-sized
            // screen; scroll them into view before asserting they're displayed.
            composeRule.onNodeWithText("ads.google.com").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("desktop-pc.lan").performScrollTo().assertIsDisplayed()
        }
    }
}
