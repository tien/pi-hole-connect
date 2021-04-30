package com.tien.piholeconnect.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PiHoleEnableStatus(
    @SerialName("status")
    val status: String
)