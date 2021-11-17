package com.tien.piholeconnect.repository

import com.tien.piholeconnect.model.*
import kotlin.time.Duration

interface PiHoleRepository {
    suspend fun getStatusSummary(): PiHoleSummary
    suspend fun getOverTimeData10Minutes(): PiHoleOverTimeData
    suspend fun getStatistics(): PiHoleStatistics
    suspend fun getLogs(limit: Int): PiHoleLogs
    suspend fun getFilterRules(ruleType: RuleType): PiHoleFilterRules
    suspend fun addFilterRule(rule: String, ruleType: RuleType): ModifyFilterRuleResponse
    suspend fun removeFilterRule(rule: String, ruleType: RuleType): ModifyFilterRuleResponse
    suspend fun disable(duration: Duration): PiHoleStatusResponse
    suspend fun enable(): PiHoleStatusResponse
}
