package com.helloeva.domain.user.infrastructure

import com.helloeva.domain.user.domain.OauthProvider
import com.helloeva.domain.user.domain.User
import com.helloeva.domain.user.domain.UserStatus
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByOauthInfoProviderAndOauthInfoProviderIdAndStatus(
        provider: OauthProvider,
        providerId: String,
        status: UserStatus,
    ): User?
}
