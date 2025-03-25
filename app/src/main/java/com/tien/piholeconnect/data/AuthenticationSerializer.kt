package com.tien.piholeconnect.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.tien.piholeconnect.model.Authentication
import java.io.InputStream
import java.io.OutputStream

object AuthenticationSerializer : Serializer<Authentication> {
    override val defaultValue = Authentication.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Authentication {
        try {
            return Authentication.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Authentication, output: OutputStream) {
        t.writeTo(output)
    }
}
