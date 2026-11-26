package com.helloeva.integration.oauth.infrastructure

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.integration.oauth.application.OauthClient
import com.helloeva.integration.oauth.application.OauthUserInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class KakaoOauthClient(
    @Value("\${kakao.api.base-url}")
    baseUrl: String,
    restClientBuilder: RestClient.Builder,
) : OauthClient {

    private val log = LoggerFactory.getLogger(javaClass)

    private val restClient = restClientBuilder
        .baseUrl(baseUrl)
        .build()

    override fun getUserInfo(accessToken: String): OauthUserInfo {
        val response = try {
            restClient.get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(KakaoUserInfoResponse::class.java)
        } catch (e: HttpClientErrorException) {
            log.warn("[Kakao] 토큰 오류 status={}", e.statusCode)
            throw CustomException(ErrorCode.INVALID_KAKAO_TOKEN)
        } catch (e: RestClientException) {
            log.error("[Kakao] 서버 오류", e)
            throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
        } ?: throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)

        return OauthUserInfo(
            providerId = response.id.toString(),
            nickname = response.nickname,
        )
    }
}
