package com.tien.piholeconnect.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.tien.piholeconnect.model.PiHoleConnections
import java.io.InputStream
import java.io.OutputStream

object PiHoleConnectionsSerializer : Serializer<PiHoleConnections> {
    override val defaultValue = PiHoleConnections.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PiHoleConnections {
        try {
            return PiHoleConnections.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: PiHoleConnections, output: OutputStream) {
        t.writeTo(output)
    }
}
