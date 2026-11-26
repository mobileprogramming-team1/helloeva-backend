package com.helloeva.domain.usage.application

import org.springframework.boot.context.properties.ConfigurationProperties
import java.math.BigDecimal

@ConfigurationProperties(prefix = "ai.pricing")
data class AiPricingProperties(
    val geminiInputPerMillionTokens: BigDecimal,
    val geminiOutputPerMillionTokens: BigDecimal,
    val ttsPerMillionCharacters: BigDecimal,
    val sttPerMinute: BigDecimal,
)
