package com.tien.piholeconnect.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PiHoleStatus {
    @SerialName("enabled")
    ENABLED,

    @SerialName("disabled")
    DISABLED,

    UNKNOWN
}

@Serializable
data class PiHoleStatusResponse(
    @SerialName("status")
    val status: PiHoleStatus = PiHoleStatus.UNKNOWN
)
