package com.tien.piholeconnect.fixtures

import com.tien.piholeconnect.model.PiHoleConfiguration
import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.PiHoleMetadata
import com.tien.piholeconnect.model.PiHoleSerializer
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.repository.models.GetActivityMetrics200Response
import com.tien.piholeconnect.repository.models.GetAuth200Response
import com.tien.piholeconnect.repository.models.GetBlocking200Response
import com.tien.piholeconnect.repository.models.GetDomain200Response
import com.tien.piholeconnect.repository.models.GetDomainsInner
import com.tien.piholeconnect.repository.models.GetMetricsSummary200Response
import com.tien.piholeconnect.repository.models.GetMetricsTopClients200Response
import com.tien.piholeconnect.repository.models.GetMetricsTopDomains200Response
import com.tien.piholeconnect.repository.models.GetQueries200Response
import com.tien.piholeconnect.repository.models.Queries1QueriesInner
import com.tien.piholeconnect.repository.models.Queries1QueriesInnerClient
import com.tien.piholeconnect.repository.models.Queries1QueriesInnerReply
import com.tien.piholeconnect.repository.models.QueriesClients
import com.tien.piholeconnect.repository.models.QueriesGravity
import com.tien.piholeconnect.repository.models.QueriesQueries
import com.tien.piholeconnect.repository.models.SessionSession
import com.tien.piholeconnect.repository.models.TopClientsClientsInner
import com.tien.piholeconnect.repository.models.TopDomainsDomainsInner
import com.tien.piholeconnect.repository.models.TotalHistoryHistoryInner
import kotlinx.serialization.encodeToString

/**
 * Canonical test fixtures shared between the e2e suite and the screenshot suite. Each `*Json`
 * constant is the serialized form of its companion model object, generated once via
 * [PiHoleSerializer.DefaultJson] so the bytes match production deserialization exactly.
 */
object Fixtures {
    private val json = PiHoleSerializer.DefaultJson

    // --- Auth ---

    val validSession =
        GetAuth200Response(
            session =
                SessionSession(
                    valid = true,
                    totp = false,
                    sid = "test-sid",
                    csrf = "test-csrf",
                    validity = 3600,
                )
        )
    val validSessionJson: String by lazy { json.encodeToString(validSession) }

    // --- Home / Statistics ---

    val metricSummary =
        GetMetricsSummary200Response(
            queries = QueriesQueries(total = 84254, blocked = 14732, percentBlocked = 17.49),
            clients = QueriesClients(total = 12, active = 8),
            gravity = QueriesGravity(domainsBeingBlocked = 143891),
        )
    val metricSummaryJson: String by lazy { json.encodeToString(metricSummary) }

    val secondaryMetricSummary =
        GetMetricsSummary200Response(
            queries = QueriesQueries(total = 12345, blocked = 1234, percentBlocked = 9.99),
            clients = QueriesClients(total = 3, active = 2),
            gravity = QueriesGravity(domainsBeingBlocked = 99999),
        )
    val secondaryMetricSummaryJson: String by lazy { json.encodeToString(secondaryMetricSummary) }

    val history: List<TotalHistoryHistoryInner> = run {
        val baseTime = 1700000000.0
        val totalData =
            listOf(
                98,
                94,
                102,
                88,
                91,
                85,
                82,
                78,
                80,
                74,
                77,
                71,
                68,
                65,
                63,
                60,
                57,
                62,
                52,
                48,
                45,
                42,
                44,
                47,
                50,
                53,
                58,
                62,
                67,
                72,
                78,
                85,
                92,
                100,
                108,
                118,
                130,
                142,
                155,
                168,
                178,
                190,
                200,
                212,
                218,
                225,
                230,
                238,
                248,
                255,
                260,
                268,
                272,
                278,
                282,
                275,
                270,
                265,
                260,
                255,
                248,
                242,
                238,
                240,
                235,
                232,
                228,
                222,
                218,
                215,
                210,
                208,
                215,
                220,
                225,
                230,
                228,
                232,
                235,
                240,
                238,
                234,
                230,
                226,
                222,
                218,
                215,
                212,
                208,
                205,
                202,
                198,
                200,
                205,
                210,
                215,
                220,
                228,
                235,
                242,
                250,
                258,
                265,
                272,
                278,
                282,
                288,
                292,
                298,
                305,
                310,
                315,
                318,
                320,
                315,
                308,
                302,
                295,
                288,
                280,
                272,
                265,
                258,
                250,
                242,
                235,
                228,
                220,
                212,
                205,
                198,
                190,
                182,
                175,
                168,
                160,
                152,
                145,
                138,
                130,
                122,
                115,
                108,
                102,
            )
        val blockedData =
            listOf(
                17,
                16,
                18,
                15,
                16,
                15,
                14,
                13,
                14,
                13,
                13,
                12,
                12,
                11,
                11,
                10,
                10,
                11,
                9,
                8,
                8,
                7,
                8,
                8,
                9,
                9,
                10,
                11,
                12,
                12,
                14,
                15,
                16,
                17,
                19,
                21,
                23,
                25,
                27,
                29,
                31,
                33,
                35,
                37,
                38,
                39,
                40,
                42,
                43,
                45,
                45,
                47,
                48,
                49,
                49,
                48,
                47,
                46,
                45,
                45,
                43,
                42,
                42,
                42,
                41,
                41,
                40,
                39,
                38,
                38,
                37,
                36,
                38,
                38,
                39,
                40,
                40,
                41,
                41,
                42,
                42,
                41,
                40,
                40,
                39,
                38,
                38,
                37,
                36,
                36,
                35,
                35,
                35,
                36,
                37,
                38,
                38,
                40,
                41,
                42,
                44,
                45,
                46,
                48,
                49,
                49,
                50,
                51,
                52,
                53,
                54,
                55,
                56,
                56,
                55,
                54,
                53,
                52,
                50,
                49,
                48,
                46,
                45,
                44,
                42,
                41,
                40,
                38,
                37,
                36,
                35,
                33,
                32,
                31,
                29,
                28,
                27,
                25,
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
    val historyJson: String by lazy {
        json.encodeToString(GetActivityMetrics200Response(history = history))
    }

    val topPermitted: Map<String, Int> =
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
    val topPermittedDomainsJson: String by lazy {
        json.encodeToString(
            GetMetricsTopDomains200Response(
                domains =
                    topPermitted.map { (domain, count) ->
                        TopDomainsDomainsInner(domain = domain, count = count)
                    },
                totalQueries = topPermitted.values.sum(),
                blockedQueries = 0,
            )
        )
    }

    val topBlocked: Map<String, Int> =
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
    val topBlockedDomainsJson: String by lazy {
        json.encodeToString(
            GetMetricsTopDomains200Response(
                domains =
                    topBlocked.map { (domain, count) ->
                        TopDomainsDomainsInner(domain = domain, count = count)
                    },
                totalQueries = topPermitted.values.sum() + topBlocked.values.sum(),
                blockedQueries = topBlocked.values.sum(),
            )
        )
    }

    val topClients: Map<String, Int> =
        mapOf(
            "desktop-pc.lan" to 32145,
            "iphone.lan" to 18762,
            "android-tv.lan" to 12456,
            "macbook.lan" to 9873,
            "iot-hub.lan" to 5218,
        )
    val topClientsJson: String by lazy {
        json.encodeToString(
            GetMetricsTopClients200Response(
                clients =
                    topClients.entries.mapIndexed { index, (name, count) ->
                        TopClientsClientsInner(
                            ip = "192.168.1.${100 + index}",
                            name = name,
                            count = count,
                        )
                    },
                totalQueries = topClients.values.sum(),
            )
        )
    }

    // --- Log ---

    val logEntries: List<Queries1QueriesInner> =
        listOf(
            Queries1QueriesInner(
                time = 1700000000.0,
                status = "FORWARDED",
                type = "A",
                domain = "google.com",
                client = Queries1QueriesInnerClient(ip = "192.168.1.100", name = "desktop-pc.lan"),
                reply = Queries1QueriesInnerReply(type = "IP", time = 12.5),
            ),
            Queries1QueriesInner(
                time = 1699999950.0,
                status = "GRAVITY",
                type = "AAAA",
                domain = "ads.doubleclick.net",
                client = Queries1QueriesInnerClient(ip = "192.168.1.101", name = "iphone.lan"),
                reply = Queries1QueriesInnerReply(type = "NXDOMAIN", time = 0.8),
            ),
            Queries1QueriesInner(
                time = 1699999900.0,
                status = "CACHE",
                type = "A",
                domain = "connectivity-check.ubuntu.com",
                client = Queries1QueriesInnerClient(ip = "192.168.1.100", name = "desktop-pc.lan"),
                reply = Queries1QueriesInnerReply(type = "IP", time = 0.2),
            ),
            Queries1QueriesInner(
                time = 1699999850.0,
                status = "FORWARDED",
                type = "A",
                domain = "api.github.com",
                client = Queries1QueriesInnerClient(ip = "192.168.1.103", name = "macbook.lan"),
                reply = Queries1QueriesInnerReply(type = "IP", time = 24.3),
            ),
            Queries1QueriesInner(
                time = 1699999800.0,
                status = "GRAVITY",
                type = "A",
                domain = "graph.facebook.com",
                client = Queries1QueriesInnerClient(ip = "192.168.1.102", name = "android-tv.lan"),
                reply = Queries1QueriesInnerReply(type = "NXDOMAIN", time = 0.5),
            ),
            Queries1QueriesInner(
                time = 1699999750.0,
                status = "CACHE",
                type = "AAAA",
                domain = "cdn.jsdelivr.net",
                client = Queries1QueriesInnerClient(ip = "192.168.1.101", name = "iphone.lan"),
                reply = Queries1QueriesInnerReply(type = "IP", time = 0.1),
            ),
            Queries1QueriesInner(
                time = 1699999700.0,
                status = "FORWARDED",
                type = "A",
                domain = "gateway.fe.apple-dns.net",
                client = Queries1QueriesInnerClient(ip = "192.168.1.103", name = "macbook.lan"),
                reply = Queries1QueriesInnerReply(type = "IP", time = 18.7),
            ),
            Queries1QueriesInner(
                time = 1699999650.0,
                status = "GRAVITY",
                type = "A",
                domain = "analytics.tiktok.com",
                client = Queries1QueriesInnerClient(ip = "192.168.1.101", name = "iphone.lan"),
                reply = Queries1QueriesInnerReply(type = "NXDOMAIN", time = 0.3),
            ),
        )
    val queriesJson: String by lazy {
        json.encodeToString(
            GetQueries200Response(queries = logEntries, recordsTotal = logEntries.size)
        )
    }

    // --- Filter rules ---

    val filterRules: List<GetDomainsInner> =
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
    val filterRulesJson: String by lazy {
        json.encodeToString(GetDomain200Response(domains = filterRules))
    }

    /**
     * Response returned when a domain is added or replaced; production swallows the body so any
     * shape with a `processed.errors == null` works.
     */
    val addedDomainJson: String = """{"processed":{"success":[],"errors":[]}}"""

    fun filterRulesJsonExcluding(domain: String): String =
        json.encodeToString(
            GetDomain200Response(domains = filterRules.filterNot { it.domain == domain })
        )

    fun filterRulesJsonIncluding(extra: GetDomainsInner): String =
        json.encodeToString(GetDomain200Response(domains = filterRules + extra))

    // --- DNS blocking ---

    val blockingEnabled =
        GetBlocking200Response(blocking = GetBlocking200Response.Blocking.ENABLED, timer = null)
    val blockingEnabledJson: String by lazy { json.encodeToString(blockingEnabled) }

    val blockingDisabled =
        GetBlocking200Response(blocking = GetBlocking200Response.Blocking.DISABLED, timer = 300.0)
    val blockingDisabledJson: String by lazy { json.encodeToString(blockingDisabled) }

    // --- Actions / FTL ---

    val actionOkJson: String = """{"status":"success","took":0.001}"""
    val ftlInfoJson: String =
        """{"ftl":{"version":"v6.0","database":{"gravity":{"file_size":0,"last_update":""""" +
            ""","queries_24h":0}},"clients":{"active":1,"total":1},"privacy_level":0,""" +
            """"messages":{"total":0,"blocking":0}},"took":0.001}"""

    // --- PiHoleConnection protos for DataStore seeding ---

    val defaultConnection: PiHoleConnection =
        PiHoleConnection.newBuilder()
            .setMetadata(PiHoleMetadata.newBuilder().setName("Test Pi-hole"))
            .setConfiguration(
                PiHoleConfiguration.newBuilder()
                    .setProtocol(URLProtocol.HTTP)
                    .setHost("primary.test")
                    .setApiPath("/api")
                    .setPort(80)
                    .setPassword("test-password")
            )
            .build()

    val secondaryConnection: PiHoleConnection =
        PiHoleConnection.newBuilder()
            .setMetadata(PiHoleMetadata.newBuilder().setName("Secondary Pi-hole"))
            .setConfiguration(
                PiHoleConfiguration.newBuilder()
                    .setProtocol(URLProtocol.HTTP)
                    .setHost("secondary.test")
                    .setApiPath("/api")
                    .setPort(80)
                    .setPassword("test-password")
            )
            .build()
}
