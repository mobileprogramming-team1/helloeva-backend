package com.helloeva.domain.usage.application

import com.helloeva.domain.usage.domain.ApiUsage
import com.helloeva.domain.usage.infrastructure.ApiUsageRepository
import com.helloeva.integration.usage.AiUsage
import com.helloeva.integration.usage.AiUsageType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class ApiUsageRecorder(
    private val apiUsageRepository: ApiUsageRepository,
    private val pricing: AiPricingProperties,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun record(conversationId: Long?, usages: List<AiUsage>) {
        val entities = usages.map { usage ->
            val cost = calculateCost(usage)
            log.info(
                "[AI 비용] conversationId={} type={} model={} input={} output={} costUsd={}",
                conversationId, usage.type, usage.model, usage.inputUnits, usage.outputUnits, cost.toPlainString(),
            )
            ApiUsage(conversationId, usage.type, usage.model, usage.inputUnits, usage.outputUnits, cost)
        }
        apiUsageRepository.saveAll(entities)
    }

    fun calculateCost(usage: AiUsage): BigDecimal =
        when (usage.type) {
            AiUsageType.QUESTION_GENERATION,
            AiUsageType.ANSWER_EVALUATION,
            AiUsageType.OVERALL_EVALUATION,
            -> perMillion(usage.inputUnits, pricing.geminiInputPerMillionTokens) +
                perMillion(usage.outputUnits, pricing.geminiOutputPerMillionTokens)

            AiUsageType.TEXT_TO_SPEECH -> perMillion(usage.inputUnits, pricing.ttsPerMillionCharacters)

            AiUsageType.SPEECH_TO_TEXT ->
                BigDecimal(usage.inputUnits).multiply(pricing.sttPerMinute).divide(SECONDS_PER_MINUTE, SCALE, RoundingMode.HALF_UP)
        }.setScale(SCALE, RoundingMode.HALF_UP)

    private fun perMillion(units: Long, price: BigDecimal): BigDecimal =
        BigDecimal(units).multiply(price).divide(MILLION, SCALE, RoundingMode.HALF_UP)

    companion object {
        private const val SCALE = 8
        private val MILLION = BigDecimal(1_000_000)
        private val SECONDS_PER_MINUTE = BigDecimal(60)
    }
}
