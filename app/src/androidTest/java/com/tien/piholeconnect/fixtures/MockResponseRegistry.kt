package com.tien.piholeconnect.fixtures

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import javax.inject.Inject
import javax.inject.Singleton

typealias MockHandler = suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData

/**
 * A test-scoped registry of mock HTTP responses. The test [MockEngine] dispatches every request to
 * this registry; the registry walks its handlers in reverse-registration order, so later
 * registrations override earlier ones — tests can call [installDefaults] once and then override a
 * single endpoint per test.
 */
@Singleton
class MockResponseRegistry @Inject constructor() {
    private data class Match(val method: HttpMethod, val pathRegex: Regex)

    private val handlers = mutableListOf<Pair<Match, MockHandler>>()
    private val recordedRequests = mutableListOf<RecordedRequest>()

    data class RecordedRequest(
        val method: HttpMethod,
        val path: String,
        val host: String,
        val query: Map<String, List<String>>,
        val bodyText: String,
    )

    val recorded: List<RecordedRequest>
        get() = synchronized(recordedRequests) { recordedRequests.toList() }

    fun reset() {
        synchronized(handlers) { handlers.clear() }
        synchronized(recordedRequests) { recordedRequests.clear() }
    }

    /**
     * Register a handler for [method] + an exact-literal [path]. Dots and other regex
     * metacharacters are escaped so `/api/stats/summary` matches only that literal path, not e.g.
     * `/apiXstatsXsummary`. Later registrations win — see the class header.
     */
    fun on(method: HttpMethod, path: String, handler: MockHandler) =
        onMatching(method, Regex.escape(path), handler)

    /**
     * Like [on], but [pathRegex] is interpreted as a regex (full-match against `url.encodedPath`).
     */
    fun onMatching(method: HttpMethod, pathRegex: String, handler: MockHandler) {
        synchronized(handlers) { handlers.add(Match(method, pathRegex.toRegex()) to handler) }
    }

    fun onGet(path: String, handler: MockHandler) = on(HttpMethod.Get, path, handler)

    fun onPost(path: String, handler: MockHandler) = on(HttpMethod.Post, path, handler)

    fun onPut(path: String, handler: MockHandler) = on(HttpMethod.Put, path, handler)

    fun onDelete(path: String, handler: MockHandler) = on(HttpMethod.Delete, path, handler)

    fun onPostMatching(pathRegex: String, handler: MockHandler) =
        onMatching(HttpMethod.Post, pathRegex, handler)

    fun onPutMatching(pathRegex: String, handler: MockHandler) =
        onMatching(HttpMethod.Put, pathRegex, handler)

    fun onDeleteMatching(pathRegex: String, handler: MockHandler) =
        onMatching(HttpMethod.Delete, pathRegex, handler)

    suspend fun dispatch(
        scope: MockRequestHandleScope,
        request: HttpRequestData,
    ): HttpResponseData {
        val bodyText =
            (request.body as? OutgoingContent.ByteArrayContent)
                ?.bytes()
                ?.toString(Charsets.UTF_8)
                .orEmpty()
        synchronized(recordedRequests) {
            recordedRequests.add(
                RecordedRequest(
                    method = request.method,
                    path = request.url.encodedPath,
                    host = request.url.host,
                    query = request.url.parameters.entries().associate { (k, v) -> k to v },
                    bodyText = bodyText,
                )
            )
        }

        val snapshot = synchronized(handlers) { handlers.toList() }
        for ((match, handler) in snapshot.asReversed()) {
            if (
                match.method == request.method && match.pathRegex.matches(request.url.encodedPath)
            ) {
                return scope.handler(request)
            }
        }
        return scope.respond(
            content =
                """{"error":"no mock for ${request.method.value} ${request.url.encodedPath}"}""",
            status = HttpStatusCode.NotFound,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }
}

/** Builds a [MockEngine] that delegates every request to [registry]. */
fun buildMockEngine(registry: MockResponseRegistry): MockEngine = MockEngine { request ->
    registry.dispatch(this, request)
}

fun MockRequestHandleScope.respondJson(
    content: String,
    status: HttpStatusCode = HttpStatusCode.OK,
): HttpResponseData =
    respond(
        content = ByteReadChannel(content),
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

fun MockRequestHandleScope.respondText(
    content: String,
    status: HttpStatusCode = HttpStatusCode.OK,
): HttpResponseData =
    respond(
        content = ByteReadChannel(content),
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "text/plain"),
    )

/**
 * Pre-loads a baseline of canned responses sufficient for `PiHoleRepository.authenticate()` and
 * every screen's initial data fetch. Individual tests can override any path via a later
 * `onGet`/`onPost` registration; later registrations win.
 */
fun MockResponseRegistry.installDefaults() {
    onGet("/api/auth") { respondJson(Fixtures.validSessionJson) }
    onPost("/api/auth") { respondJson(Fixtures.validSessionJson) }

    onGet("/api/stats/summary") { respondJson(Fixtures.metricSummaryJson) }
    onGet("/api/history") { respondJson(Fixtures.historyJson) }
    onGet("/api/stats/top_domains") { request ->
        val blocked = request.url.parameters["blocked"]?.toBooleanStrictOrNull() == true
        respondJson(
            if (blocked) Fixtures.topBlockedDomainsJson else Fixtures.topPermittedDomainsJson
        )
    }
    onGet("/api/stats/top_clients") { respondJson(Fixtures.topClientsJson) }
    onGet("/api/stats/query_types") { respondJson(Fixtures.queryTypesJson) }
    onGet("/api/stats/upstreams") { respondJson(Fixtures.upstreamsJson) }
    onGet("/api/queries") { respondJson(Fixtures.queriesJson) }

    onGet("/api/dns/blocking") { respondJson(Fixtures.blockingEnabledJson) }
    onPost("/api/dns/blocking") { respondJson(Fixtures.blockingEnabledJson) }

    onGet("/api/domains") { respondJson(Fixtures.filterRulesJson) }
    onPostMatching("""/api/domains/[^/]+/[^/]+""") { respondJson(Fixtures.addedDomainJson) }
    onPutMatching("""/api/domains/[^/]+/[^/]+/[^/]+""") { respondJson(Fixtures.addedDomainJson) }
    onDeleteMatching("""/api/domains/[^/]+/[^/]+/[^/]+""") {
        respond(content = ByteReadChannel.Empty, status = HttpStatusCode.NoContent)
    }

    onPost("/api/action/gravity") { respondText("OK") }
    onPost("/api/action/restartdns") { respondJson(Fixtures.actionOkJson) }
    onPost("/api/action/flush/arp") { respondJson(Fixtures.actionOkJson) }
    onPost("/api/action/flush/logs") { respondJson(Fixtures.actionOkJson) }

    onGet("/api/info/ftl") { respondJson(Fixtures.ftlInfoJson) }
}
