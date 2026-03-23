package com.tien.piholeconnect.model

import kotlinx.serialization.json.Json

class PiHoleSerializer {
    companion object {
        val DefaultJson = Json {
            isLenient = false
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = false
        }
    }
}
