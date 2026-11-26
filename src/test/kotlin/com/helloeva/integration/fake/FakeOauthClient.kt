package com.helloeva.integration.fake

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.integration.oauth.application.OauthClient
import com.helloeva.integration.oauth.application.OauthUserInfo

class FakeOauthClient : OauthClient {

    override fun getUserInfo(accessToken: String): OauthUserInfo =
        when (accessToken) {
            INVALID_TOKEN -> throw CustomException(ErrorCode.INVALID_KAKAO_TOKEN)
            SERVER_ERROR_TOKEN -> throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
            else -> OauthUserInfo(providerId = "kakao-$accessToken", nickname = NICKNAME)
        }

    companion object {
        const val INVALID_TOKEN = "invalid"
        const val SERVER_ERROR_TOKEN = "server-error"
        const val NICKNAME = "에바연습생"
    }
}
