package com.tien.piholeconnect.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

enum class AnswerCategory { ALLOW, BLOCK, CACHE, UNKNOWN }

@Serializable
enum class AnswerType(val category: AnswerCategory) {
    @SerialName("1")
    GRAVITY_BLOCK(AnswerCategory.BLOCK),

    @SerialName("2")
    UPSTREAM(AnswerCategory.ALLOW),

    @SerialName("3")
    LOCAL_CACHE(AnswerCategory.CACHE),

    @SerialName("4")
    REGEX_BLOCK(AnswerCategory.BLOCK),

    @SerialName("5")
    EXACT_BLOCK(AnswerCategory.BLOCK),

    @SerialName("6")
    EXTERNAL_IP_BLOCK(AnswerCategory.BLOCK),

    @SerialName("7")
    EXTERNAL_NULL_BLOCK(AnswerCategory.BLOCK),

    @SerialName("8")
    EXTERNAL_NXRA_BLOCK(AnswerCategory.BLOCK),

    @SerialName("9")
    CNAME_GRAVITY_BLOCK(AnswerCategory.BLOCK),

    @SerialName("10")
    CNAME_REGEX_BLOCK(AnswerCategory.BLOCK),

    @SerialName("11")
    CNAME_EXACT_BLOCK(AnswerCategory.BLOCK),

    @SerialName("12")
    RETRIED(AnswerCategory.ALLOW),

    @SerialName("13")
    RETRIED_IGNORED(AnswerCategory.ALLOW),

    @SerialName("14")
    ALREADY_FORWARDED(AnswerCategory.ALLOW),

    UNKNOWN(AnswerCategory.UNKNOWN)
}

@Serializable
data class PiHoleLogs(
    val data: List<PiHoleLog> = listOf()
)

@Serializable(PiHoleLogSerializer::class)
data class PiHoleLog(
    val timestamp: Long,
    val queryType: String,
    val requestedDomain: String,
    val client: String,
    val answerType: AnswerType,
    val responseTime: Int
)

object PiHoleLogSerializer : KSerializer<PiHoleLog> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(PiHoleLog::class.simpleName!!) {
            element<Int>(PiHoleLog::timestamp.name)
            element<String>(PiHoleLog::queryType.name)
            element<String>(PiHoleLog::requestedDomain.name)
            element<String>(PiHoleLog::client.name)
            element<AnswerType>(PiHoleLog::answerType.name)
        }

    override fun serialize(encoder: Encoder, value: PiHoleLog) {
        error("Serialization is not supported")
    }

    override fun deserialize(decoder: Decoder): PiHoleLog {
        require(decoder is JsonDecoder)

        val jsonArray = decoder.decodeJsonElement().jsonArray

        return PiHoleLog(
            timestamp = jsonArray[0].jsonPrimitive.long,
            queryType = jsonArray[1].jsonPrimitive.content,
            requestedDomain = jsonArray[2].jsonPrimitive.content,
            client = jsonArray[3].jsonPrimitive.content,
            answerType = AnswerType.entries.getOrElse(jsonArray[4].jsonPrimitive.int - 1) { AnswerType.UNKNOWN },
            responseTime = jsonArray[7].jsonPrimitive.int
        )
    }
}
