package com.tien.piholeconnect.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PiHoleStatistics(
    @SerialName("querytypes")
    val queryTypes: QueryTypes = QueryTypes(),
    @SerialName("top_queries")
    val topQueries: Map<String, Int> = mapOf(),
    @SerialName("top_ads")
    val topAds: Map<String, Int> = mapOf(),
    @SerialName("top_sources")
    val topSources: Map<String, Int> = mapOf()
) {
    @Serializable
    data class QueryTypes(
        @SerialName("A (IPv4)")
        val AIPv4: Double = 0.0,
        @SerialName("AAAA (IPv6)")
        val AAAAIPv6: Double = 0.0,
        @SerialName("ANY")
        val any: Double = 0.0,
        @SerialName("SRV")
        val SRV: Double = 0.0,
        @SerialName("SOA")
        val SOA: Double = 0.0,
        @SerialName("PTR")
        val PTR: Double = 0.0,
        @SerialName("TXT")
        val TXT: Double = 0.0,
        @SerialName("NAPTR")
        val NAPTR: Double = 0.0,
        @SerialName("MX")
        val MX: Double = 0.0,
        @SerialName("DS")
        val DS: Double = 0.0,
        @SerialName("RRSIG")
        val RRSIG: Double = 0.0,
        @SerialName("DNSKEY")
        val DNSKey: Double = 0.0,
        @SerialName("NS")
        val NS: Double = 0.0,
        @SerialName("OTHER")
        val other: Double = 0.0,
        @SerialName("SVCB")
        val SVCB: Double = 0.0,
        @SerialName("HTTPS")
        val HTTPS: Double = 0.0
    )
}