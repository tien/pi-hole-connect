package com.tien.piholeconnect.e2e

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import io.ktor.http.HttpMethod
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ToolsScreenE2ETest : E2ETestBase() {
    @Test
    fun runUpdateGravity_callsActionEndpoint() {
        launchApp().use {
            composeRule.waitUntil(timeoutMillis = 15_000) {
                composeRule
                    .onAllNodes(hasText("Queries over last 24 hours"))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeRule.onNodeWithText("Tools").performClick()

            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodes(hasText("Update gravity")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("Update gravity").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                mockResponses.recorded.any {
                    it.method == HttpMethod.Post && it.path == "/api/action/gravity"
                }
            }
        }
    }
}
