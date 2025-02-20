package com.tien.piholeconnect.util

import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class EnumIntSerializer<T : Enum<T>>(private val kClass: KClass<T>, private val fallback: T) :
    KSerializer<T> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(kClass::class.simpleName!!)

    override fun deserialize(decoder: Decoder): T {
        val ordinal = decoder.decodeInt()
        return kClass.java.enumConstants?.firstOrNull { it.ordinal == ordinal } ?: fallback
    }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeInt(value.ordinal)
    }
}
