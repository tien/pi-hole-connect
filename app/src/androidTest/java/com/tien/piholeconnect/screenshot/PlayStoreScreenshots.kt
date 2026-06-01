package com.tien.piholeconnect.screenshot

import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tien.piholeconnect.fixtures.Fixtures
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.cleanstatusbar.CleanStatusBar
import tools.fastlane.screengrab.locale.LocaleTestRule

@RunWith(AndroidJUnit4::class)
class PlayStoreScreenshots {
    companion object {
        @get:ClassRule @JvmStatic val localeTestRule = LocaleTestRule()

        @BeforeClass
        @JvmStatic
        fun setUp() {
            Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
            // CleanStatusBar only broadcasts SystemUI demo-mode commands; SystemUI ignores them
            // unless demo mode is allowed first, so enable it on the test device ourselves.
            val pfd =
                InstrumentationRegistry.getInstrumentation()
                    .uiAutomation
                    .executeShellCommand("settings put global sysui_demo_allowed 1")
            // Drain to EOF so the command completes before we broadcast the demo-mode commands.
            ParcelFileDescriptor.AutoCloseInputStream(pfd).use { it.readBytes() }
            // Fixed 12:30 clock, full signal/battery, notification icons hidden.
            CleanStatusBar.enableWithDefaults()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            CleanStatusBar.disable()
        }
    }

    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Renders [content] in an edge-to-edge host so the captured status bar matches the real app —
     * transparent, with the top-bar surface drawn behind it — instead of the default opaque grey
     * scrim the plain test activity paints. Mirrors [MainActivity]'s `enableEdgeToEdge()` call.
     */
    private fun showScreen(content: @Composable () -> Unit) {
        composeTestRule.activityRule.scenario.onActivity { it.enableEdgeToEdge() }
        composeTestRule.setContent(content)
    }

    @Test
    fun homeScreen() {
        val viewModel =
            FakeHomeViewModel(
                metricSummaryData = Fixtures.metricSummary,
                historyData = Fixtures.history,
                adsBlockingEnabled = true,
            )
        showScreen { ScreenshotHomeScreen(viewModel = viewModel) }
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
        showScreen { ScreenshotStatisticsScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("2_statistics")
    }

    @Test
    fun filterRulesScreen() {
        val viewModel = FakeFilterRulesViewModel(rulesData = Fixtures.filterRules)
        showScreen { ScreenshotFilterRulesScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("3_filter_rules")
    }

    @Test
    fun logScreen() {
        val viewModel = FakeLogViewModel(logsData = Fixtures.logEntries)
        showScreen { ScreenshotLogScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("4_log")
    }

    @Test
    fun toolsScreen() {
        val viewModel =
            FakeToolsViewModel(
                gravityUpdatedAtData = System.currentTimeMillis() - 2 * 60 * 60 * 1000
            )
        showScreen { ScreenshotToolsScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("5_tools")
    }
}
