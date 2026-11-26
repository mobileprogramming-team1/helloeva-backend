package com.helloeva.integration.oauth.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUserInfoResponse(
    val id: Long,
    val properties: Properties? = null,
    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount? = null,
) {
    data class Properties(
        val nickname: String? = null,
    )

    data class KakaoAccount(
        val profile: Profile? = null,
    )

    data class Profile(
        val nickname: String? = null,
    )

    val nickname: String?
        get() = kakaoAccount?.profile?.nickname ?: properties?.nickname
}
