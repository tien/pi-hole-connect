package com.tien.piholeconnect.util

import com.tien.piholeconnect.model.URLProtocol
import io.ktor.http.URLProtocol.Companion.HTTP
import io.ktor.http.URLProtocol.Companion.HTTPS

fun URLProtocol.toKtorURLProtocol() =
    when (this) {
        URLProtocol.HTTP -> HTTP
        URLProtocol.HTTPS -> HTTPS
        else -> HTTP
    }
