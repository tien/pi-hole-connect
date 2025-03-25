package com.tien.piholeconnect.model

import com.tien.piholeconnect.repository.models.Queries1QueriesInner
import com.tien.piholeconnect.repository.models.Queries1QueriesInnerClient
import com.tien.piholeconnect.repository.models.Queries1QueriesInnerReply

typealias QueryLog = Queries1QueriesInner

typealias QueryLogClient = Queries1QueriesInnerClient

typealias QueryLogReply = Queries1QueriesInnerReply

enum class QueryStatusType {
    BLOCK,
    ALLOW,
    CACHE,
    UNKNOWN,
}

enum class QueryStatus(val type: QueryStatusType) {
    GRAVITY(QueryStatusType.BLOCK),
    FORWARDED(QueryStatusType.ALLOW),
    CACHE(QueryStatusType.CACHE),
    REGEX(QueryStatusType.BLOCK),
    DENYLIST(QueryStatusType.BLOCK),
    EXTERNAL_BLOCKED_IP(QueryStatusType.BLOCK),
    EXTERNAL_BLOCKED_NULL(QueryStatusType.BLOCK),
    EXTERNAL_BLOCKED_NXRA(QueryStatusType.BLOCK),
    GRAVITY_CNAME(QueryStatusType.BLOCK),
    REGEX_CNAME(QueryStatusType.BLOCK),
    DENYLIST_CNAME(QueryStatusType.BLOCK),
    RETRIED(QueryStatusType.ALLOW),
    RETRIED_DNSSEC(QueryStatusType.ALLOW),
    IN_PROGRESS(QueryStatusType.ALLOW),
    CACHE_STALE(QueryStatusType.CACHE),
    SPECIAL_DOMAIN(QueryStatusType.BLOCK),
    UNKNOWN(QueryStatusType.UNKNOWN);

    companion object {}
}

fun QueryStatus.Companion.fromStatusString(status: String): QueryStatus {
    return try {
        enumValueOf<QueryStatus>(status)
    } catch (error: Throwable) {
        QueryStatus.UNKNOWN
    }
}
