package com.tien.piholeconnect.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tien.piholeconnect.fixtures.Fixtures
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.locale.LocaleTestRule

@RunWith(AndroidJUnit4::class)
class PlayStoreScreenshots {
    companion object {
        @get:ClassRule @JvmStatic val localeTestRule = LocaleTestRule()

        @BeforeClass
        @JvmStatic
        fun setUp() {
            Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
        }
    }

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun homeScreen() {
        val viewModel =
            FakeHomeViewModel(
                metricSummaryData = Fixtures.metricSummary,
                historyData = Fixtures.history,
                adsBlockingEnabled = true,
            )
        composeTestRule.setContent { ScreenshotHomeScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        // The dashboard chart animates in from an empty state (Vico plays the data load as an
        // animated transition) and Screengrab captures the real device surface, so advancing the
        // virtual test clock doesn't settle what's drawn. Sleep on the real frame clock to let the
        // animation finish before grabbing the screenshot.
        Thread.sleep(2000)
        composeTestRule.waitForIdle()
        Screengrab.screenshot("1_home")
    }

    @Test
    fun statisticsScreen() {
        val viewModel =
            FakeStatisticsViewModel(
                topDomainsData = Fixtures.topPermitted,
                topBlockedDomainsData = Fixtures.topBlocked,
                topClientsData = Fixtures.topClients,
            )
        composeTestRule.setContent { ScreenshotStatisticsScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("2_statistics")
    }

    @Test
    fun filterRulesScreen() {
        val viewModel = FakeFilterRulesViewModel(rulesData = Fixtures.filterRules)
        composeTestRule.setContent { ScreenshotFilterRulesScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("3_filter_rules")
    }

    @Test
    fun logScreen() {
        val viewModel = FakeLogViewModel(logsData = Fixtures.logEntries)
        composeTestRule.setContent { ScreenshotLogScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("4_log")
    }

    @Test
    fun toolsScreen() {
        val viewModel =
            FakeToolsViewModel(
                gravityUpdatedAtData = System.currentTimeMillis() - 2 * 60 * 60 * 1000
            )
        composeTestRule.setContent { ScreenshotToolsScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("5_tools")
    }
}
