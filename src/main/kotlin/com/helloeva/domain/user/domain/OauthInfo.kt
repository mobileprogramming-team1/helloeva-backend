package com.helloeva.domain.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class OauthInfo(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val provider: OauthProvider,

    @Column(nullable = false, length = 64)
    val providerId: String,
)
