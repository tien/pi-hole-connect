package com.tien.piholeconnect.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/** Empty DataStore → user must first add a Pi-hole. */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SetupFlowE2ETest : E2ETestBase() {
    /** Start with an empty DataStore so the app shows the "Add Pi-hole" entry point. */
    override fun seed() {}

    @Test
    fun firstLaunch_showsAddPiHoleButton() {
        launchApp().use {
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodes(hasText("Add Pi-hole")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("Add Pi-hole").assertIsDisplayed()
        }
    }

    @Test
    fun addConnection_savesAndReturnsToHome_loadsMetrics() {
        launchApp().use {
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodes(hasText("Add Pi-hole")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("Add Pi-hole").performClick()

            // PiHoleConnectionScreen — wait for the form to render.
            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().isNotEmpty()
            }

            composeRule
                .onAllNodes(hasSetTextAction())
                .filterToOne(hasText("Host", substring = true))
                .performTextInput("primary.test")

            composeRule
                .onAllNodes(hasSetTextAction())
                .filterToOne(hasText("Password", substring = true))
                .performTextInput("test-password")

            composeRule.onNodeWithContentDescription("Save").performClick()

            // We should be back on Home, which now renders the seeded summary.
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodes(hasText("84,254")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("84,254").assertIsDisplayed()
        }
    }
}
