package com.tien.piholeconnect.util

fun String.isNumericOrWhitespace() = Regex("^(|[1-9][0-9]*)$").matches(this)