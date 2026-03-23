package com.tien.piholeconnect.util

import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.PiHoleConnections
import com.tien.piholeconnect.model.URLProtocol
import io.ktor.http.URLProtocol.Companion.HTTP
import io.ktor.http.URLProtocol.Companion.HTTPS

fun URLProtocol.toKtorURLProtocol() =
    when (this) {
        URLProtocol.HTTP -> HTTP
        URLProtocol.HTTPS -> HTTPS
        else -> HTTP
    }

fun PiHoleConnection.Builder.populateDefaultValues(): PiHoleConnection.Builder =
    this.setConfiguration(
        this.configuration
            .toBuilder()
            .setProtocol(URLProtocol.HTTP)
            .setHost("pi.hole")
            .setApiPath("/api")
            .setPort(HTTP.defaultPort)
    )

fun PiHoleConnections.getSelectedConnection(): Pair<String, PiHoleConnection>? =
    if (this.hasSelectedConnectionId())
        this.connectionsMap[this.selectedConnectionId]?.let { this.selectedConnectionId to it }
    else this.connectionsMap.entries.firstOrNull()?.let { it.key to it.value }
