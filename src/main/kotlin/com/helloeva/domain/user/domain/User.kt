package com.helloeva.domain.user.domain

import com.helloeva.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, length = 50)
    var nickname: String,

    @Embedded
    val oauthInfo: OauthInfo,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: UserStatus = UserStatus.ACTIVE
        protected set

    var loggedOutAt: LocalDateTime? = null
        protected set

    var withdrawnAt: LocalDateTime? = null
        protected set

    val isActive: Boolean
        get() = status == UserStatus.ACTIVE

    fun logout(now: LocalDateTime = LocalDateTime.now()) {
        loggedOutAt = now
    }

    fun withdraw(now: LocalDateTime = LocalDateTime.now()) {
        status = UserStatus.WITHDRAWN
        withdrawnAt = now
    }

    fun isTokenRevoked(issuedAt: LocalDateTime): Boolean =
        !isActive || loggedOutAt?.let { !issuedAt.isAfter(it) } == true
}
