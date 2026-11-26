package com.helloeva.domain.user.presentation

import com.helloeva.domain.user.domain.OauthProvider
import com.helloeva.domain.user.domain.User

data class MeResponse(
    val userId: Long,
    val nickname: String,
    val provider: OauthProvider,
) {
    companion object {
        fun from(user: User): MeResponse =
            MeResponse(
                userId = user.id,
                nickname = user.nickname,
                provider = user.oauthInfo.provider,
            )
    }
}
