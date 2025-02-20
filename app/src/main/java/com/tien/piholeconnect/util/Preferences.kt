package com.tien.piholeconnect.util

import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.model.UserPreferences
import io.ktor.http.URLProtocol.Companion.HTTP
import java.util.*

fun PiHoleConnection.Builder.populateDefaultValues(): PiHoleConnection.Builder =
    this.setId(UUID.randomUUID().toString())
        .setName("My Pi-hole")
        .setProtocol(URLProtocol.HTTP)
        .setHost("pi.hole")
        .setApiPath("/admin/api.php")
        .setPort(HTTP.defaultPort)

fun UserPreferences.Builder.populateDefaultValues(): UserPreferences.Builder = this
