package com.tien.piholeconnect.model


import com.tien.piholeconnect.util.EnumIntSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(RuleType.Companion.Serializer::class)
enum class RuleType {
    WHITE,
    BLACK,
    REGEX_WHITE,
    REGEX_BLACK;

    companion object {
        object Serializer : EnumIntSerializer<RuleType>(RuleType::class, WHITE)
    }
}

@Serializable
data class PiHoleFilterRule(
    @SerialName("comment")
    val comment: String? = null,
    @SerialName("date_added")
    val dateAdded: Long = 0,
    @SerialName("date_modified")
    val dateModified: Long = 0,
    @SerialName("domain")
    val domain: String = "",
    @SerialName("enabled")
    val enabled: Int = 0,
    @SerialName("groups")
    val groups: List<Int> = listOf(),
    @SerialName("id")
    val id: Int = 0,
    @SerialName("type")
    val type: RuleType = RuleType.REGEX_WHITE
)

@Serializable
data class PiHoleFilterRules(
    @SerialName("data")
    val `data`: List<PiHoleFilterRule> = listOf()
)

@Serializable
data class ModifyFilterRuleResponse(
    @SerialName("success")
    val success: Boolean = false,
    @SerialName("message")
    val message: String = ""
)