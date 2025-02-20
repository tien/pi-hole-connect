package com.tien.piholeconnect.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PiHoleOverTimeData(
    @SerialName("domains_over_time") val domainsOverTime: Map<Int, Int> = mapOf(),
    @SerialName("ads_over_time") val adsOverTime: Map<Int, Int> = mapOf(),
)
