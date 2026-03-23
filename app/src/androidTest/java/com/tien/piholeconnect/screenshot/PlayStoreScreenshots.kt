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
                queries = QueriesQueries(total = 84254, blocked = 14732, percentBlocked = 17.49),
                clients = QueriesClients(total = 12),
                gravity = QueriesGravity(domainsBeingBlocked = 143891),
            )

        private val history = run {
            val baseTime = 1700000000.0
            // 144 data points at 10-minute intervals (24 hours)
            // Traffic follows a realistic daily pattern: quiet at night, morning + evening peaks
            val totalData =
                listOf(
                    // 00:00 – 00:50 (winding down from evening)
                    98,
                    94,
                    102,
                    88,
                    91,
                    85,
                    // 01:00 – 01:50
                    82,
                    78,
                    80,
                    74,
                    77,
                    71,
                    // 02:00 – 02:50
                    68,
                    65,
                    63,
                    60,
                    57,
                    62,
                    // 03:00 – 03:50 (overnight trough)
                    52,
                    48,
                    45,
                    42,
                    44,
                    47,
                    // 04:00 – 04:50 (start of rise)
                    50,
                    53,
                    58,
                    62,
                    67,
                    72,
                    // 05:00 – 05:50
                    78,
                    85,
                    92,
                    100,
                    108,
                    118,
                    // 06:00 – 06:50
                    130,
                    142,
                    155,
                    168,
                    178,
                    190,
                    // 07:00 – 07:50
                    200,
                    212,
                    218,
                    225,
                    230,
                    238,
                    // 08:00 – 08:50 (morning peak)
                    248,
                    255,
                    260,
                    268,
                    272,
                    278,
                    // 09:00 – 09:50
                    282,
                    275,
                    270,
                    265,
                    260,
                    255,
                    // 10:00 – 10:50
                    248,
                    242,
                    238,
                    240,
                    235,
                    232,
                    // 11:00 – 11:50
                    228,
                    222,
                    218,
                    215,
                    210,
                    208,
                    // 12:00 – 12:50 (midday bump)
                    215,
                    220,
                    225,
                    230,
                    228,
                    232,
                    // 13:00 – 13:50
                    235,
                    240,
                    238,
                    234,
                    230,
                    226,
                    // 14:00 – 14:50 (post-lunch dip)
                    222,
                    218,
                    215,
                    212,
                    208,
                    205,
                    // 15:00 – 15:50
                    202,
                    198,
                    200,
                    205,
                    210,
                    215,
                    // 16:00 – 16:50 (building to evening peak)
                    220,
                    228,
                    235,
                    242,
                    250,
                    258,
                    // 17:00 – 17:50
                    265,
                    272,
                    278,
                    282,
                    288,
                    292,
                    // 18:00 – 18:50 (evening peak)
                    298,
                    305,
                    310,
                    315,
                    318,
                    320,
                    // 19:00 – 19:50
                    315,
                    308,
                    302,
                    295,
                    288,
                    280,
                    // 20:00 – 20:50
                    272,
                    265,
                    258,
                    250,
                    242,
                    235,
                    // 21:00 – 21:50
                    228,
                    220,
                    212,
                    205,
                    198,
                    190,
                    // 22:00 – 22:50
                    182,
                    175,
                    168,
                    160,
                    152,
                    145,
                    // 23:00 – 23:50
                    138,
                    130,
                    122,
                    115,
                    108,
                    102,
                )
            val blockedData =
                listOf(
                    // 00:00 – 00:50
                    17,
                    16,
                    18,
                    15,
                    16,
                    15,
                    // 01:00 – 01:50
                    14,
                    13,
                    14,
                    13,
                    13,
                    12,
                    // 02:00 – 02:50
                    12,
                    11,
                    11,
                    10,
                    10,
                    11,
                    // 03:00 – 03:50
                    9,
                    8,
                    8,
                    7,
                    8,
                    8,
                    // 04:00 – 04:50
                    9,
                    9,
                    10,
                    11,
                    12,
                    12,
                    // 05:00 – 05:50
                    14,
                    15,
                    16,
                    17,
                    19,
                    21,
                    // 06:00 – 06:50
                    23,
                    25,
                    27,
                    29,
                    31,
                    33,
                    // 07:00 – 07:50
                    35,
                    37,
                    38,
                    39,
                    40,
                    42,
                    // 08:00 – 08:50
                    43,
                    45,
                    45,
                    47,
                    48,
                    49,
                    // 09:00 – 09:50
                    49,
                    48,
                    47,
                    46,
                    45,
                    45,
                    // 10:00 – 10:50
                    43,
                    42,
                    42,
                    42,
                    41,
                    41,
                    // 11:00 – 11:50
                    40,
                    39,
                    38,
                    38,
                    37,
                    36,
                    // 12:00 – 12:50
                    38,
                    38,
                    39,
                    40,
                    40,
                    41,
                    // 13:00 – 13:50
                    41,
                    42,
                    42,
                    41,
                    40,
                    40,
                    // 14:00 – 14:50
                    39,
                    38,
                    38,
                    37,
                    36,
                    36,
                    // 15:00 – 15:50
                    35,
                    35,
                    35,
                    36,
                    37,
                    38,
                    // 16:00 – 16:50
                    38,
                    40,
                    41,
                    42,
                    44,
                    45,
                    // 17:00 – 17:50
                    46,
                    48,
                    49,
                    49,
                    50,
                    51,
                    // 18:00 – 18:50
                    52,
                    53,
                    54,
                    55,
                    56,
                    56,
                    // 19:00 – 19:50
                    55,
                    54,
                    53,
                    52,
                    50,
                    49,
                    // 20:00 – 20:50
                    48,
                    46,
                    45,
                    44,
                    42,
                    41,
                    // 21:00 – 21:50
                    40,
                    38,
                    37,
                    36,
                    35,
                    33,
                    // 22:00 – 22:50
                    32,
                    31,
                    29,
                    28,
                    27,
                    25,
                    // 23:00 – 23:50
                    24,
                    23,
                    21,
                    20,
                    19,
                    18,
                )
            totalData.mapIndexed { i, total ->
                TotalHistoryHistoryInner(
                    timestamp = baseTime + i * 600,
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
                gravityUpdatedAtData = System.currentTimeMillis() - 2 * 60 * 60 * 1000
            )
        composeTestRule.setContent { ScreenshotToolsScreen(viewModel = viewModel) }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("5_tools")
    }
}
