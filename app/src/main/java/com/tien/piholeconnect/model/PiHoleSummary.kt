package com.tien.piholeconnect.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PiHoleSummary(
    @SerialName("domains_being_blocked")
    val domainsBeingBlocked: Int = 0,
    @SerialName("dns_queries_today")
    val dnsQueriesToday: Int = 0,
    @SerialName("ads_blocked_today")
    val adsBlockedToday: Int = 0,
    @SerialName("ads_percentage_today")
    val adsPercentageToday: Double = 0.0,
    @SerialName("unique_domains")
    val uniqueDomains: Int = 0,
    @SerialName("queries_forwarded")
    val queriesForwarded: Int = 0,
    @SerialName("queries_cached")
    val queriesCached: Int = 0,
    @SerialName("clients_ever_seen")
    val clientsEverSeen: Int = 0,
    @SerialName("unique_clients")
    val uniqueClients: Int = 0,
    @SerialName("dns_queries_all_types")
    val dnsQueriesAllTypes: Int = 0,
    @SerialName("reply_NODATA")
    val replyNODATA: Int = 0,
    @SerialName("reply_NXDOMAIN")
    val replyNXDOMAIN: Int = 0,
    @SerialName("reply_CNAME")
    val replyCNAME: Int = 0,
    @SerialName("reply_IP")
    val replyIP: Int = 0,
    @SerialName("privacy_level")
    val privacyLevel: Int = 0,
    @SerialName("status")
    val status: String = "",
    @SerialName("gravity_last_updated")
    val gravityLastUpdated: GravityLastUpdated = GravityLastUpdated()
) {
    @Serializable
    data class GravityLastUpdated(
        @SerialName("file_exists")
        val fileExists: Boolean = false,
        @SerialName("absolute")
        val absolute: Int = 0,
        @SerialName("relative")
        val relative: Relative = Relative()
    ) {
        @Serializable
        data class Relative(
            @SerialName("days")
            val days: Int = 0,
            @SerialName("hours")
            val hours: Int = 0,
            @SerialName("minutes")
            val minutes: Int = 0
        )
    }
}