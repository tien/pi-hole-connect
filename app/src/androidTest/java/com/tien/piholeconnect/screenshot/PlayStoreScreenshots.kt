package com.tien.piholeconnect.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tien.piholeconnect.model.QueryLog
import com.tien.piholeconnect.model.QueryLogClient
import com.tien.piholeconnect.model.QueryLogReply
import com.tien.piholeconnect.repository.models.GetDomainsInner
import com.tien.piholeconnect.repository.models.GetMetricsSummary200Response
import com.tien.piholeconnect.repository.models.QueriesClients
import com.tien.piholeconnect.repository.models.QueriesGravity
import com.tien.piholeconnect.repository.models.QueriesQueries
import com.tien.piholeconnect.repository.models.TotalHistoryHistoryInner
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

    private val metricSummary =
        GetMetricsSummary200Response(
            queries =
                QueriesQueries(
                    total = 84254,
                    blocked = 14732,
                    percentBlocked = 17.49,
                ),
            clients = QueriesClients(total = 12),
            gravity = QueriesGravity(domainsBeingBlocked = 143891),
        )

    private val history = run {
      val baseTime = 1700000000.0
      val totalData =
          listOf(
              120,
              95,
              72,
              55,
              40,
              35,
              30,
              45,
              85,
              150,
              210,
              280,
              310,
              290,
              265,
              240,
              220,
              250,
              300,
              320,
              280,
              220,
              170,
              135,
          )
      val blockedData =
          listOf(
              25,
              18,
              12,
              8,
              6,
              5,
              4,
              7,
              15,
              30,
              45,
              60,
              65,
              58,
              52,
              48,
              42,
              50,
              62,
              68,
              55,
              42,
              32,
              26,
          )
      totalData.mapIndexed { i, total ->
        TotalHistoryHistoryInner(
            timestamp = baseTime + i * 3600,
            total = total,
            blocked = blockedData[i],
        )
      }
    }

    private val topPermitted =
        mapOf(
            "debug.opendns.com" to 2385,
            "ipv4only.arpa" to 2382,
            "gateway.fe.apple-dns.net" to 1095,
            "connectivity-check.ubuntu.com" to 796,
            "api.github.com" to 617,
            "cdn.jsdelivr.net" to 583,
            "www.google.com" to 475,
            "e17437.dscb.akamaiedge.net" to 473,
            "time.apple.com" to 431,
            "dns.google" to 398,
        )

    private val topBlocked =
        mapOf(
            "ads.google.com" to 1432,
            "graph.facebook.com" to 987,
            "analytics.tiktok.com" to 654,
            "tracking.amazon.com" to 432,
            "telemetry.microsoft.com" to 321,
            "ads.doubleclick.net" to 298,
            "pixel.facebook.com" to 245,
            "stats.wp.com" to 189,
            "crashlytics.google.com" to 156,
            "metrics.icloud.com" to 134,
        )

    private val topClients =
        mapOf(
            "desktop-pc.lan" to 32145,
            "iphone.lan" to 18762,
            "android-tv.lan" to 12456,
            "macbook.lan" to 9873,
            "iot-hub.lan" to 5218,
        )

    private val logData =
        listOf(
            QueryLog(
                time = 1700000000.0,
                status = "FORWARDED",
                type = "A",
                domain = "google.com",
                client = QueryLogClient(name = "desktop-pc.lan"),
                reply = QueryLogReply(time = 12.5),
            ),
            QueryLog(
                time = 1699999950.0,
                status = "GRAVITY",
                type = "AAAA",
                domain = "ads.doubleclick.net",
                client = QueryLogClient(name = "iphone.lan"),
                reply = QueryLogReply(time = 0.8),
            ),
            QueryLog(
                time = 1699999900.0,
                status = "CACHE",
                type = "A",
                domain = "connectivity-check.ubuntu.com",
                client = QueryLogClient(name = "desktop-pc.lan"),
                reply = QueryLogReply(time = 0.2),
            ),
            QueryLog(
                time = 1699999850.0,
                status = "FORWARDED",
                type = "A",
                domain = "api.github.com",
                client = QueryLogClient(name = "macbook.lan"),
                reply = QueryLogReply(time = 24.3),
            ),
            QueryLog(
                time = 1699999800.0,
                status = "GRAVITY",
                type = "A",
                domain = "graph.facebook.com",
                client = QueryLogClient(name = "android-tv.lan"),
                reply = QueryLogReply(time = 0.5),
            ),
            QueryLog(
                time = 1699999750.0,
                status = "CACHE",
                type = "AAAA",
                domain = "cdn.jsdelivr.net",
                client = QueryLogClient(name = "iphone.lan"),
                reply = QueryLogReply(time = 0.1),
            ),
            QueryLog(
                time = 1699999700.0,
                status = "FORWARDED",
                type = "A",
                domain = "gateway.fe.apple-dns.net",
                client = QueryLogClient(name = "macbook.lan"),
                reply = QueryLogReply(time = 18.7),
            ),
            QueryLog(
                time = 1699999650.0,
                status = "GRAVITY",
                type = "A",
                domain = "analytics.tiktok.com",
                client = QueryLogClient(name = "iphone.lan"),
                reply = QueryLogReply(time = 0.3),
            ),
        )

    private val filterRules =
        listOf(
            GetDomainsInner(
                id = 1,
                domain = "ads.example.com",
                type = GetDomainsInner.Type.DENY,
                kind = GetDomainsInner.Kind.EXACT,
                enabled = true,
                dateAdded = 1695000000,
            ),
            GetDomainsInner(
                id = 2,
                domain = "tracking.analytics.com",
                type = GetDomainsInner.Type.DENY,
                kind = GetDomainsInner.Kind.EXACT,
                enabled = true,
                dateAdded = 1694500000,
            ),
            GetDomainsInner(
                id = 3,
                domain = ".*\\.doubleclick\\.net",
                type = GetDomainsInner.Type.DENY,
                kind = GetDomainsInner.Kind.REGEX,
                enabled = true,
                dateAdded = 1694000000,
            ),
            GetDomainsInner(
                id = 4,
                domain = "telemetry.microsoft.com",
                type = GetDomainsInner.Type.DENY,
                kind = GetDomainsInner.Kind.EXACT,
                enabled = false,
                dateAdded = 1693500000,
            ),
            GetDomainsInner(
                id = 5,
                domain = ".*\\.adserver\\..*",
                type = GetDomainsInner.Type.DENY,
                kind = GetDomainsInner.Kind.REGEX,
                enabled = true,
                dateAdded = 1693000000,
            ),
            GetDomainsInner(
                id = 6,
                domain = "pixel.facebook.com",
                type = GetDomainsInner.Type.DENY,
                kind = GetDomainsInner.Kind.EXACT,
                enabled = true,
                dateAdded = 1692500000,
            ),
        )
  }

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun homeScreen() {
    val viewModel =
        FakeHomeViewModel(
            metricSummaryData = metricSummary,
            historyData = history,
            adsBlockingEnabled = true,
        )
    composeTestRule.setContent { ScreenshotHomeScreen(viewModel = viewModel) }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    Screengrab.screenshot("1_home")
  }

  @Test
  fun statisticsScreen() {
    val viewModel =
        FakeStatisticsViewModel(
            topDomainsData = topPermitted,
            topBlockedDomainsData = topBlocked,
            topClientsData = topClients,
        )
    composeTestRule.setContent { ScreenshotStatisticsScreen(viewModel = viewModel) }
    composeTestRule.waitForIdle()
    Screengrab.screenshot("2_statistics")
  }

  @Test
  fun filterRulesScreen() {
    val viewModel = FakeFilterRulesViewModel(rulesData = filterRules)
    composeTestRule.setContent { ScreenshotFilterRulesScreen(viewModel = viewModel) }
    composeTestRule.waitForIdle()
    Screengrab.screenshot("3_filter_rules")
  }

  @Test
  fun logScreen() {
    val viewModel = FakeLogViewModel(logsData = logData)
    composeTestRule.setContent { ScreenshotLogScreen(viewModel = viewModel) }
    composeTestRule.waitForIdle()
    Screengrab.screenshot("4_log")
  }

  @Test
  fun toolsScreen() {
    val viewModel =
        FakeToolsViewModel(
            gravityUpdatedAtData = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
        )
    composeTestRule.setContent { ScreenshotToolsScreen(viewModel = viewModel) }
    composeTestRule.waitForIdle()
    Screengrab.screenshot("5_tools")
  }
}
