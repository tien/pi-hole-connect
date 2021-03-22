package com.tien.piholeconnect.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
enum class AnswerType {
    @SerialName("1")
    GRAVITY_BLOCK,

    @SerialName("2")
    UPSTREAM,

    @SerialName("3")
    LOCAL_CACHE,

    @SerialName("4")
    WILD_CARD_BLOCK,
    UNKNOWN
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
            answerType = when (jsonArray[4].jsonPrimitive.content) {
                "1" -> AnswerType.GRAVITY_BLOCK
                "2" -> AnswerType.UPSTREAM
                "3" -> AnswerType.LOCAL_CACHE
                "4" -> AnswerType.WILD_CARD_BLOCK
                else -> AnswerType.UNKNOWN
            },
            responseTime = jsonArray[7].jsonPrimitive.int
        )
    }
}