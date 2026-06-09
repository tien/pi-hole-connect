package com.tien.piholeconnect.ui.screen.statistics

import com.tien.piholeconnect.repository.PiHoleRepositoryManager
import com.tien.piholeconnect.repository.models.QueryTypesTypes
import com.tien.piholeconnect.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest

/**
 * A single answer source for forwarded queries — either a real upstream resolver or one of
 * Pi-hole's internal sources (cache / blocklist). [responseTimeMs] is null when the source has no
 * measured latency (e.g. locally answered queries).
 */
data class UpstreamStat(val name: String, val count: Int, val responseTimeMs: Double?)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
open class StatisticsViewModel
@Inject
constructor(piHoleRepositoryManager: PiHoleRepositoryManager) : BaseViewModel() {
    open val summary =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest { it.metricsApi.getMetricsSummary().body() }
            .asViewFlowState()

    open val queryTypes =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest { it.metricsApi.getMetricsQueryTypes().body().types.toSortedCountMap() }
            .asViewFlowState()

    open val upstreams =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsUpstreams()
                    .body()
                    .upstreams
                    .orEmpty()
                    .map { upstream ->
                        UpstreamStat(
                            name =
                                upstream.name?.takeIf(String::isNotBlank)
                                    ?: upstream.ip?.takeIf(String::isNotBlank)
                                    ?: "",
                            count = upstream.count ?: 0,
                            responseTimeMs =
                                upstream.statistics?.response?.takeIf { it > 0 }?.times(1000),
                        )
                    }
                    .filter { it.name.isNotBlank() && it.count > 0 }
                    .sortedByDescending { it.count }
            }
            .asViewFlowState()

    open val topDomains =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsTopDomains(blocked = false)
                    .body()
                    .domains
                    ?.map { (it.domain ?: "") to (it.count ?: 0) }
                    ?.toMap()
            }
            .asViewFlowState()

    open val topBlockedDomains =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsTopDomains(blocked = true)
                    .body()
                    .domains
                    ?.map { (it.domain ?: "") to (it.count ?: 0) }
                    ?.toMap()
            }
            .asViewFlowState()

    open val topClients =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsTopClients()
                    .body()
                    .clients
                    ?.map { (it.name ?: "") to (it.count ?: 0) }
                    ?.toMap()
            }
            .asViewFlowState()
}

/**
 * Flattens the per-record-type counts into a label→count map, dropping empties and sorting desc.
 */
private fun QueryTypesTypes?.toSortedCountMap(): Map<String, Int> {
    if (this == null) return emptyMap()
    return listOf(
            "A" to A,
            "AAAA" to AAAA,
            "ANY" to ANY,
            "SRV" to SRV,
            "SOA" to SOA,
            "PTR" to PTR,
            "TXT" to TXT,
            "NAPTR" to NAPTR,
            "MX" to MX,
            "DS" to DS,
            "RRSIG" to RRSIG,
            "DNSKEY" to DNSKEY,
            "NS" to NS,
            "SVCB" to SVCB,
            "HTTPS" to HTTPS,
            "OTHER" to OTHER,
        )
        .mapNotNull { (label, count) -> count?.takeIf { it > 0 }?.let { label to it } }
        .sortedByDescending { it.second }
        .toMap()
}
