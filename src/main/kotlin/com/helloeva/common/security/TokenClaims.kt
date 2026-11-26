package com.helloeva.common.security

import java.time.LocalDateTime

data class TokenClaims(
    val userId: Long,
    val issuedAt: LocalDateTime,
)
