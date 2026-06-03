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
            // The top-domain/client cards sit below the overview and breakdown cards on a
            // phone-sized screen; scroll each into view before asserting it's displayed.
            composeRule.onNodeWithText("debug.opendns.com").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("ads.google.com").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("desktop-pc.lan").performScrollTo().assertIsDisplayed()
        }
    }
}
