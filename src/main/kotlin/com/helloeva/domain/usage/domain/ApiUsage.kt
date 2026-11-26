package com.helloeva.domain.usage.domain

import com.helloeva.common.domain.BaseEntity
import com.helloeva.integration.usage.AiUsageType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "api_usages")
class ApiUsage(
    val conversationId: Long?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    val type: AiUsageType,

    @Column(nullable = false, length = 100)
    val model: String,

    @Column(nullable = false)
    val inputUnits: Long,

    @Column(nullable = false)
    val outputUnits: Long,

    @Column(nullable = false, precision = 14, scale = 8)
    val costUsd: BigDecimal,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
