@file:Suppress("unused")

package com.tien.piholeconnect.extension

import com.tien.piholeconnect.model.PiHoleConnection.*
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.model.UserPreferences

fun Builder.populateDefaultValues(): Builder =
    getDefaultInstance().newBuilderForType()
        .setName("My Pi-hole")
        .setProtocol(URLProtocol.HTTP)
        .setHost("pi.hole")
        .setApiPath("/admin/api.php")
        .setPort(80)

fun UserPreferences.Builder.populateDefaultValues(): UserPreferences.Builder =
    UserPreferences.getDefaultInstance().newBuilderForType()
        .addPiHoleConnections(
            getDefaultInstance().newBuilderForType().populateDefaultValues()
        )
