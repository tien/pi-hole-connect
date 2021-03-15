@file:Suppress("unused")

package com.tien.piholeconnect.extension

import com.tien.piholeconnect.model.PiHoleConnection.*
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.model.UserPreferences
import java.util.*

fun Builder.populateDefaultValues(): Builder = newBuilder()
    .setId(UUID.randomUUID().toString())
    .setName("My Pi-hole")
    .setProtocol(URLProtocol.HTTP)
    .setHost("pi.hole")
    .setApiPath("/admin/api.php")
    .setPort(80)

fun UserPreferences.Builder.populateDefaultValues(): UserPreferences.Builder =
    UserPreferences.newBuilder()
        .addPiHoleConnections(
            getDefaultInstance().newBuilderForType().populateDefaultValues()
        )
