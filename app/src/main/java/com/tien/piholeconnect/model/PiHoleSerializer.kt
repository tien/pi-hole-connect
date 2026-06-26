package com.tien.piholeconnect.model

import kotlinx.serialization.json.Json

class PiHoleSerializer {
    companion object {
        val DefaultJson = Json {
            isLenient = false
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = false
            // Pi-hole's published OpenAPI schema declares `default: true` on
            // SetBlockingRequest.blocking, but the server does NOT treat an omitted field as
            // "enable" — so `blocking = true` must be sent explicitly. Without encodeDefaults,
            // kotlinx drops values equal to that bogus default and re-enabling POSTs an empty `{}`.
            encodeDefaults = true
            // Keep null fields omitted so enabling encodeDefaults doesn't spray explicit
            // nulls into every request body across the generated client.
            explicitNulls = false
        }
    }
}
